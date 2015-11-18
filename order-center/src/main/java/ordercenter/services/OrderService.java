package ordercenter.services;

import common.constants.MessageJobSource;
import common.exceptions.AppBusinessException;
import common.exceptions.ErrorCode;
import common.services.GeneralDao;
import common.services.MessageJobService;
import common.utils.DateUtils;
import common.utils.Money;
import common.utils.page.Page;
import ordercenter.constants.CancelOrderType;
import ordercenter.constants.OrderState;
import ordercenter.models.*;
import ordercenter.util.OrderNumberUtil;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import play.Logger;
import productcenter.constants.StoreStrategy;
import productcenter.services.SkuAndStorageService;
import usercenter.constants.MarketChannel;
import usercenter.models.User;
import usercenter.models.address.Address;
import usercenter.services.AddressService;
import usercenter.services.UserService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 订单Service
 * User: lidujun
 * Date: 2015-04-29
 */
@Service
@Transactional
public class OrderService {

    private final Logger.ALogger SCHEDULER_LOGGER = Logger.of("schedulerTask");

    @Autowired
    GeneralDao generalDao;

    @Autowired
    private CartService cartService;

    @Autowired
    private BackGoodsService backGoodsService;

    @Autowired
    private SkuAndStorageService skuAndStorageService;

    @Autowired
    MessageJobService messageJobService;

    @Autowired
    UserService userService;

    @Autowired
    AddressService addressService;

    @Autowired
    VoucherService voucherService;


    /**
     * 判断是否可以退货申请
     *
     * @param order
     * @return
     */
    public boolean isBackGoods(Order order){

        if(order.getOrderState().getName().equals(OrderState.Receiving.getName())){

            long timeDifference = DateUtils.current().getMillis() - order.getUpdateTime().getMillis();

            if(timeDifference < 7 * 24 * 60 * 60 * 1000 ){

                List<BackGoods> backGoodsList = backGoodsService.getBackGoodsByOrderId(order.getId());

                for(BackGoods backGoods:backGoodsList){
                    if(!backGoods.getBackState().isCancelForUser()){
                        return false;
                    }
                }
                return true;
            }else {
                return false;
            }
        }else{
            return false;
        }
    }

    /**
     * 更新订单
     * @param order
     */
    public void updateOrder(Order order) {
        generalDao.merge(order);
    }

    /**
     * 通过订单号更新订单支付机构信息
     * @param orderNo
     * @param orgName
     */
    public void updateOrderPayOrg(Long orderNo, String payType, String orgName) {

        String jpql = "update Order o set o.payType=:payType, o.payBank=:orgName where o.orderNo=:orderNo";
        Map<String, Object> params = new HashMap<>();
        params.put("payType", payType);
        params.put("orgName", orgName);

        generalDao.update(jpql, params);
    }

    /**
     * 更新订单状态
     * @param orderId
     * @param orderState
     * @param previousState
     * @return
     */
    public int updateOrderStateByStrictState(int orderId, OrderState orderState, OrderState previousState) {

        DateTime curTime = DateUtils.current();

        String jpql = "update Order o set o.orderState=:orderState , o.updateTime =:modifyDate ";
        Map<String, Object> params = new HashMap<>();
        params.put("orderState", orderState);
        params.put("modifyDate", curTime);

        if(OrderState.Pay.getName().equals(orderState.getName())) {
            jpql += ", o.payDate =:payDate ";
            params.put("payDate", curTime);
        } else if(OrderState.Cancel.getName().equals(orderState.getName()) || OrderState.Close.getName().equals(orderState.getName()) || OrderState.Success.getName().equals(orderState.getName())) {
            jpql += ", o.endDate =:endDate ";
            params.put("endDate", curTime);
        }
        jpql += " where o.id=:orderId ";
        params.put("orderId", orderId);
        if(!OrderState.Pay.getName().equals(orderState.getName())) {
            jpql += " and o.orderState =:previousState ";
            params.put("previousState", previousState);
        }
        return generalDao.update(jpql, params);
    }

    /**
     * 通过订单id获取订单
     * @param orderId
     * @return
     */
    @Transactional(readOnly = true)
    public Order getOrderById(int orderId) {
        return generalDao.get(Order.class, orderId);
    }

    private String getSelectAllOrderSql() {
        return "select v from Order v where 1=1 ";
    }

    /**
     * 按照订单状态获取订单列表
     * @return
     */
    @Transactional(readOnly = true)
    public List<Order> getAllOrderListByOrderState(OrderState orderState) {
        String jpql = getSelectAllOrderSql();
        Map<String, Object> queryParams = new HashMap<>();
        jpql += " and v.orderState = :orderState ";
        queryParams.put("orderState", orderState);

        List<Order> orderList = generalDao.query(jpql, Optional.ofNullable(null), queryParams);
        return orderList;
    }

    /**
     * 通过订单号获取订单
     * @param orderNo
     * @return
     */
    @Transactional(readOnly = true)
    public Order getOrderByOrderNo(long orderNo) {
        String jpql = getSelectAllOrderSql();
        Map<String, Object> queryParams = new HashMap<>();
        jpql += " and v.orderNo = :orderNo ";
        queryParams.put("orderNo", orderNo);

        List<Order> orderList = generalDao.query(jpql, Optional.ofNullable(null), queryParams);
        Order order = null;
        if(orderList != null && orderList.size() > 0) {
            order= orderList.get(0);
        }
        return  order;
    }

    /**
     * 查询订单列表
     *
     * @param page
     * @param userId
     * @param querytType
     *          0所有订单、1待收货、2待评价
     * @return
     */
    @Transactional(readOnly = true)
    public List<Order> getOrderByUserId(Optional<Page<Order>> page, int userId,int querytType) {
        OrderState[] type_1 = {OrderState.Confirm,OrderState.Print,OrderState.Verify,OrderState.Send};
        OrderState[] type_2 = {OrderState.Receiving,OrderState.Success};

        String jpql = "select o from Order o join o.orderItemList oi where 1=1 ";
        Map<String, Object> queryParams = new HashMap<>();
        jpql += " and o.userId = :userId ";
        queryParams.put("userId", userId);

        if(querytType == 1){//待收货
            jpql += " and o.orderState in (:type0, :type1, :type2, :type3) ";
            for(int i=0; i<type_1.length; i++) {
                queryParams.put("type" + i, type_1[i]);
            }
        }else if(querytType == 2){//待评价
            jpql += " and o.valuation = false and o.orderState in (:type0, :type1) ";
            for(int i=0; i<type_2.length; i++) {
                queryParams.put("type" + i, type_2[i]);
            }
        }

        jpql += " group by o.id order by o.id desc";

        return generalDao.query(jpql,page,queryParams);
    }

    /**
     * 查询订单、关联订单项
     * @param orderId
     * @return
     */
    @Transactional(readOnly = true)
    public Order getOrderById(int orderId,int userId) {

        String jpql = "select o from Order o join o.orderItemList oi where 1=1 ";
        Map<String, Object> queryParams = new HashMap<>();
        jpql += " and o.id = :orderId ";
        queryParams.put("orderId", orderId);

        jpql += " and o.userId = :userId ";
        queryParams.put("userId", userId);

        List<Order> orderList = generalDao.query(jpql, Optional.ofNullable(null), queryParams);

        Order order = null;
        if(orderList != null && orderList.size() > 0) {
            order= orderList.get(0);
        }
        return  order;
    }

    /**
     * 取消订单
     *
     * @param order
     */
    public void cancelOrder(Order order, CancelOrderType type) { //ldj  后续可能会改
        if (null == order) {
            throw new AppBusinessException("取消订单失败，无法查询订单");
        }

        if(OrderState.Cancel.equals(order.getOrderState())) {
            return;
        }
        if(!order.getOrderState().canCancel()) {
            throw new AppBusinessException(String.format("无法取消订单，订单[订单号='%s']状态现在为: %s",
                    order.getOrderNo(), order.getOrderState().getValue()));
        }

        order.setOrderState(OrderState.Cancel);
        DateTime now = DateUtils.current();
        order.setUpdateTime(now);
        order.setEndDate(now);
        generalDao.persist(order);
        this.createOrderStateHistory(new OrderStateHistory(order, OrderState.Cancel.getLogMsg(), type.getName()));

        for (OrderItem oi : order.getOrderItemList()) {
            this.updateOrderItemStateByStrictState(oi.getId(), OrderState.Cancel, oi.getOrderState());
            //增加库存
            skuAndStorageService.addSkuStock(oi.getSkuId(), oi.getNumber());
        }

    }

    /**
     *定时器定时取消订单（现在是指定1小时）
     */
    public void timerAutoCancelOrderProcess() {
        List<Order> orderList = this.getAllOrderListByOrderState(OrderState.Create);
        if(orderList != null && orderList.size() > 0) {
            for(Order order : orderList) {
                if(!order.getOrderState().equals(OrderState.Create)) {
                    continue;
                }

                if(DateUtils.current().getMillis() - order.getCreateTime().getMillis() < 3600000) {
                    continue;
                }

                try {
                    cancelOrder(order, CancelOrderType.Sys);
                } catch (AppBusinessException e) {
                    SCHEDULER_LOGGER.error("系统定时任务取消超时订单的时候发生错误: " + e.getMessage());
                }
            }
        }
    }

    /**
     *定时器定时提醒支付订单(30分钟)
     */
    public void timerAutoTipPayOrderProcess() {
        List<Order> orderList = this.getAllOrderListByOrderState(OrderState.Create);
        if(orderList != null && orderList.size() > 0) {
            for(Order order : orderList) {
                if(!order.getOrderState().equals(OrderState.Create) || order.isSendPayRemind()) {
                    continue;
                }

                if(DateUtils.current().getMillis() - order.getCreateTime().getMillis() < 1800000) {
                    continue;
                }
                //发送短信
                String tipMsg = "亲爱的会员，您抢到的商品尚未支付，仍为您保留30分钟，因库存紧张，请及时支付！";

                Logistics logistics = getLogisticsByOrderId(order.getId());
                if(logistics != null && logistics.getMobile() != null && logistics.getMobile().trim().length() > 0) {
                    messageJobService.sendSmsMessage(logistics.getMobile().trim(), tipMsg, MessageJobSource.ORDER_PAY_REMIND, null);
                    //更改订单的发送状态
                    order.setSendPayRemind(true);
                }
            }
        }
    }

    /**
     * 确认收货
     *
     * @param orderId
     * @param userId
     */
    public void receivingOrder(int orderId, int userId) { //ldj  后续可能会改

        Order order = getOrderById(orderId, userId);
        if (null == order) {
            throw new AppBusinessException("确认收货失败，无法查询订单");
        }
        // 确认收货
        try {
            this.updateOrderStateByStrictState(order.getId(), OrderState.Receiving, order.getOrderState());
            for (OrderItem oi : order.getOrderItemList()) {
                this.updateOrderItemStateByStrictState(oi.getId(), OrderState.Receiving, oi.getOrderState());
            }
            order.setOrderState(OrderState.Receiving);
            this.createOrderStateHistory(new OrderStateHistory(order, OrderState.Receiving.getLogMsg()));
        } catch (AppBusinessException a) {
            throw new AppBusinessException("确认收货失败");
        }
    }


    //////////////////////////////订单项/////////////////////////////////////////////
    /**
     * 根据ID查找订单项
     *
     * @param orderItemId
     * @return
     */
    public OrderItem getOrderItemById(int orderItemId){
        return generalDao.get(OrderItem.class,orderItemId);
    }

    /**
     * 更新订单项状态
     * @param orderItemId
     * @param orderState
     * @param previousState
     * @return
     */
    public int updateOrderItemStateByStrictState(int orderItemId, OrderState orderState, OrderState previousState) {
        DateTime curTime = DateUtils.current();

        String jpql = "update OrderItem o set o.orderState=:orderState, o.updateTime =:updateTime ";
        Map<String, Object> params = new HashMap<>();
        params.put("orderState", orderState);
        params.put("updateTime", curTime);

        jpql += " where o.id=:orderItemId ";
        params.put("orderItemId", orderItemId);

        if(previousState != null && StringUtils.isNotEmpty(previousState.getName()) && !OrderState.Pay.getName().equals(orderState.getName())) {
            jpql += " and o.orderState =:previousState ";
            params.put("previousState", previousState);
        }

        return generalDao.update(jpql, params);
    }

    /**
     * 通过订单主键id获取订单项
     * @param orderId
     * @return
     */
    @Transactional(readOnly = true)
    public List<OrderItem> queryOrderItemsByOrderId(int orderId) {
        String jpql = "select o from OrderItem o where 1=1 and o.orderId=:orderId";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("orderId", orderId);
        return generalDao.query(jpql, Optional.<Page<OrderItem>>empty(), queryParams);
    }


    ///////////////////////////订单状态历史////////////////////////////////////////////////////////////
    /**
     * 创建订单状态历史
     */
    public void createOrderStateHistory(OrderStateHistory orderStateHistory) {
        if(orderStateHistory.getOrderState().getName().equals(OrderState.Pay.getName())) {
            deleteCancelOrderStateHistory(orderStateHistory.getOrderId());
        }
        generalDao.persist(orderStateHistory);
    }

    /**
     * 删除订单状态历史
     */
    public void deleteCancelOrderStateHistory(int orderId) {
        String jpql = "delete from OrderStateHistory where orderId=:orderId and orderState=:orderState";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("orderId", orderId);
        queryParams.put("orderState", OrderState.Cancel);
        generalDao.update(jpql, queryParams);
    }

    /**
     * 通过订单id获取订单状态历史记录（订单跟踪）
     * @param orderId
     * @return
     */
    public List<OrderStateHistory> getOrderStateHistoryByOrderId(int orderId){
        String jpql = "select o from OrderStateHistory o where 1=1 and o.orderId=:orderId";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("orderId", orderId);
        return generalDao.query(jpql, Optional.<Page<OrderStateHistory>>empty(), queryParams);
    }

    //////////////////////////////订单物流-邮寄地址/////////////////////////////////////////////

    /**
     * 通过订单获取物流-邮寄地址
     * @param logisticsId
     * @return
     */
    @Transactional(readOnly = true)
    public Logistics getLogisticsById(int logisticsId) {
        return generalDao.get(Logistics.class, logisticsId);
    }

    @Transactional(readOnly = true)
    public Logistics getLogisticsByOrderId(int orderId) {

        String jpql = "select l from Logistics l where 1=1 ";
        Map<String, Object> queryParams = new HashMap<>();
        jpql += " and l.orderId = :orderId ";
        queryParams.put("orderId", orderId);

        List<Logistics> logisticsList = generalDao.query(jpql, Optional.ofNullable(null), queryParams);
        Logistics logistics = null;
        if(logisticsList != null && logisticsList.size() > 0) {
            logistics = logisticsList.get(0);
        }
        return  logistics;
    }

    /**
     * 提交订单
     * @param user
     * @param selItems 购物车项，如果是立即购买，是"skuId:number"的形式，否则是"cartItemId_cartItemId_..."的形式
     * @param addressId 地址ID
     * @param channel 渠道
     * @param vouchers 代金券编号列表
     * @return
     */
    public List<Integer> submitOrder(User user, String selItems, Integer addressId,
                                     MarketChannel channel, List<String> vouchers) {

        if (selItems == null || selItems.trim().length() == 0) {
            throw new AppBusinessException(ErrorCode.Conflict, "订单为空！");
        }

        Cart cart = cartService.buildCartForSubmitOrder(user.getId(), selItems);

        //送货地址
        Address address = addressService.getAddress(addressId, user.getId());
        if (address == null) {
            throw new AppBusinessException(ErrorCode.Conflict, "您选择的订单寄送地址已经被修改，在系统中不存在！");
        }

        //根据设计师拆单
        List<CartItem> cartItemList = cart.getNotDeleteCartItemList();

        Map<Integer, List<CartItem>> designerCartItemListMap = new LinkedHashMap<>();
        for(CartItem cartItem : cartItemList) {
            List<CartItem> designerCartItemList = designerCartItemListMap.get(cartItem.getCustomerId());
            if(designerCartItemList == null) {
                designerCartItemList = new ArrayList<>();
            }
            designerCartItemList.add(cartItem);
            designerCartItemListMap.put(cartItem.getCustomerId(), designerCartItemList);
        }

        Set<Integer> designerIdSet = designerCartItemListMap.keySet();
        List<Order> orders = new ArrayList<>(designerIdSet.size());

        for(int designerId : designerIdSet) {
            //创建订单
            Order order = createOrder(user, channel, designerId, designerCartItemListMap.get(designerId));

            orders.add(order);
        }

        //计算所有订单金额合计
        Money totalOrderMoney = orders.stream().map(Order::getTotalMoney).reduce(Money.valueOf(0d), Money::add);

        //根据编码查找可用的代金券
        List<Voucher> voucherList = voucherService.findVouchersByUniqueNo(vouchers, user.getId(), totalOrderMoney);

        //计算可用的代金券总金额
        Money totalVoucherMoney = voucherList.stream().map(Voucher::getAmount).reduce(Money.valueOf(0d), Money::add);
        Money cumulativeVoucherMoney = Money.valueOf(0d);

        if(totalVoucherMoney.getAmount() > totalOrderMoney.getAmount()) {
            //当代金券使用金额大于订单金额，则使代金券金额等于订单金额
            totalVoucherMoney = totalOrderMoney;
        }

        int i = 0;
        for(Order order : orders) {
            i++;

            //计算分摊到订单的代金券金额
            if(!voucherList.isEmpty()) {
                Money splitVoucherMoney;
                if(i == orders.size()) {
                    //最后一个订单分摊的优惠券金额为总的减去之前分摊的累加，这样算是为了避免按百分比算加起来不为1的情况
                    splitVoucherMoney = totalVoucherMoney.subtract(cumulativeVoucherMoney);
                } else {
                    splitVoucherMoney = totalVoucherMoney.multiply(order.getTotalMoney().divide(totalOrderMoney).getAmount());
                    cumulativeVoucherMoney = cumulativeVoucherMoney.add(splitVoucherMoney);
                }
                order.setVoucherFee(splitVoucherMoney);
                order.setTotalMoney(order.calcTotalMoney());
            }

            saveOrder(order);

            //扣减库存
            for(OrderItem orderItem : order.getOrderItemList()) {
                skuAndStorageService.minusSkuStock(orderItem.getSkuId(), orderItem.getNumber());
            }

            //创建订单状态历史
            createOrderStateHistory(new OrderStateHistory(order));

            //创建订单物流-邮寄地址
            Logistics logistics = new Logistics(address);
            logistics.setOrderId(order.getId());
            generalDao.persist(logistics);

        }

        if(!voucherList.isEmpty()) {
            voucherService.useVoucherForOrder(orders, voucherList);
        }

        cartService.deleteCartItemAfterSubmitOrder(selItems);

        return orders.stream().map(Order::getId).collect(Collectors.toList());
    }

    private void saveOrder(Order order) {
        generalDao.persist(order);
        for(OrderItem orderItem : order.getOrderItemList()) {
            orderItem.setOrderId(order.getId());
            generalDao.persist(orderItem);
        }
    }

    private Order createOrder(User user, MarketChannel channel, int designerId, List<CartItem> cartItemList) {

        Order order = new Order();
        //生成订单号
        order.setOrderNo(OrderNumberUtil.getOrderNo());
        order.setAccountType(user.getAccountType());
        order.setUserId(user.getId());
        order.setUserName(user.getUserName());

        order.setOrderState(OrderState.Create);
        DateTime curTime = DateUtils.current();
        order.setCreateTime(curTime);
        order.setMilliDate(curTime.getMillis());
        order.setIsDelete(false);
        order.setBrush(false);
        order.setSendPayRemind(false);
        order.setClient(channel);
        order.setCustomerId(designerId);

        for(CartItem item : cartItemList) {
            OrderItem orderItem = new OrderItem();
            orderItem.setSkuId(item.getSkuId());
            orderItem.setBarCode(item.getBarCode());
            orderItem.setItemNo(item.getId() + "");
            orderItem.setProductId(item.getProductId());
            orderItem.setCategoryId(item.getCategoryId());
            orderItem.setStorageId(item.getStorageId());
            orderItem.setCustomerId(item.getCustomerId());
            orderItem.setProductName(item.getProductName());
            orderItem.setOrderState(order.getOrderState());
            orderItem.setStoreStrategy(StoreStrategy.NormalStrategy);
            orderItem.setNumber(item.getNumber());
            orderItem.setMainPicture(item.getMainPicture());
            orderItem.setCurUnitPrice(item.getCurUnitPrice());
            orderItem.setTotalPrice(item.getTotalPrice());
            orderItem.setAppraise(false);
            order.getOrderItemList().add(orderItem);
        }

        order.setTotalMoney(order.calcTotalMoney());

        return order;
    }

    @Transactional(readOnly = true)
    public boolean needToPay(List<Integer> orderIds) {
        return !orderIds.stream().map(this::getOrderById).allMatch(order -> order.getTotalMoney().getAmount() == 0);
    }

}
