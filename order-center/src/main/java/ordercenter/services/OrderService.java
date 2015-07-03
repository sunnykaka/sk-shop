package ordercenter.services;

import common.exceptions.AppBusinessException;
import common.services.GeneralDao;
import common.utils.DateUtils;
import common.utils.page.Page;
import ordercenter.constants.CancelOrderType;
import ordercenter.constants.OrderState;
import ordercenter.models.*;
import ordercenter.util.OrderNumberUtil;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import play.Logger;
import productcenter.constants.StoreStrategy;
import productcenter.services.SkuAndStorageService;
import usercenter.models.User;
import usercenter.models.address.Address;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 订单Service
 * User: lidujun
 * Date: 2015-04-29
 */
@Service
@Transactional
public class OrderService {

    @Autowired
    GeneralDao generalDao;

    @Autowired
    private CartService cartService;

    @Autowired
    private BackGoodsService backGoodsService;

    @Autowired
    private SkuAndStorageService skuAndStorageService;

    /**
     * 判断是否可以退货申请
     *
     * @param order
     * @return
     */
    public boolean isBackGoods(Order order){
        int days = Days.daysBetween(order.getCreateTime(), new DateTime(order.getMilliDate())).getDays();
        if(order.getOrderState().getName().equals(OrderState.Receiving.getName()) && days <= 7 ){

            List<BackGoods> backGoodsList = backGoodsService.getBackGoodsByOrderId(order.getId());

            for(BackGoods backGoods:backGoodsList){
                if(!backGoods.getBackState().checkCanNotCancelForUser()){
                    return false;
                }
            }
            return true;
        }else{
            return false;
        }
    }

    /**
     * 创建购订单
     * @param order
     */
    public void createOrder(Order order) {
        Logger.info("--------OrderService createCart begin exe-----------" + order);
        generalDao.persist(order);
    }

    /**
     * 更新订单
     * @param order
     */
    public void updateOrder(Order order) {
        Logger.info("--------OrderService updateCart begin exe-----------" + order);
        generalDao.merge(order);
    }

    /**
     * 通过订单号更新订单支付机构信息
     * @param orderNo
     * @param orgName
     */
    public void updateOrderPayOrg(Long orderNo, String payType, String orgName) {
        Logger.info("--------OrderService updateCart begin exe-----------" + orderNo + " : " + orgName);

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
        Logger.info("--------OrderService updateOrderState begin exe-----------" + orderId + " : " + orderState + " : " + previousState);

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
        jpql += " where o.id=:orderId and o.orderState =:previousState ";
        params.put("orderId", orderId);
        params.put("previousState", previousState);

        return generalDao.update(jpql, params);
    }

    /**
     * 通过订单id删除订单
     * @param orderId
     */
    public void deleteOrder(int orderId) {
        Logger.info("--------OrderService deleteCart real delete  begin exe-----------" + orderId);
        generalDao.removeById(Order.class, orderId);
    }

    /**
     * 通过订单id获取订单
     * @param orderId
     * @return
     */
    @Transactional(readOnly = true)
    public Order getOrderById(int orderId) {
        Logger.info("--------OrderService getCart begin exe-----------" + orderId);
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
        Logger.info("--------OrderService getOrderByUserId begin exe-----------" + userId + "&type:" + querytType);
        OrderState[] type_1 = {OrderState.Confirm,OrderState.Print,OrderState.Verify,OrderState.Send};
        OrderState[] type_2 = {OrderState.Receiving,OrderState.Success};

        String jpql = "select o from Order o join o.orderItemList oi where 1=1 ";
        Map<String, Object> queryParams = new HashMap<>();
        jpql += " and o.userId = :userId ";
        queryParams.put("userId", userId);

        if(querytType == 1){
            jpql += " and o.orderState in (:type0, :type1, :type2, :type3) ";
            for(int i=0; i<type_1.length; i++) {
                queryParams.put("type" + i, type_1[i]);
            }
        }else if(querytType == 2){
            jpql += " and o.orderState in (:type0, :type1) ";
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
        Logger.info("--------OrderService getCart begin exe-----------" + orderId);

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
     * @param orderId
     * @param userId
     */
    public void cancelOrder(int orderId, int userId, int type) { //ldj  后续可能会改
        Order order = getOrderById(orderId, userId);
        if (null == order) {
            throw new AppBusinessException("取消订单失败，无法查询订单");
        }

        // TODO 取消订单
        try {
            int count = this.updateOrderStateByStrictState(order.getId(), OrderState.Cancel,order.getOrderState());
            if(count == 1) {
                for (OrderItem oi : order.getOrderItemList()) {
                    this.updateOrderItemStateByStrictState(oi.getId(), OrderState.Cancel, oi.getOrderState());
                    //增加库存
                    skuAndStorageService.addSkuStock(oi.getSkuId(), oi.getNumber());
                }
                this.createOrderStateHistory(new OrderStateHistory(order, OrderState.Cancel.getLogMsg(), CancelOrderType.getName(type)));
            }
        } catch (AppBusinessException a) {
            throw new AppBusinessException("取消订单失败");
        }
    }

    /**
     *定时器定时取消订单（现在是指定1小时）
     */
    public void timerAutoCancelOrderProcess() {
        Logger.info("--------OrderService timerAutoCancelOrderProcess begin exe-----------");
        List<Order> orderList = this.getAllOrderListByOrderState(OrderState.Create);
        if(orderList != null && orderList.size() > 0) {
            for(Order order : orderList) {
                if(!order.getOrderState().equals(OrderState.Create)) {
                    continue;
                }

                if(DateUtils.current().getMillis() - order.getCreateTime().getMillis() < 3600000) {
                    continue;
                }

                int count = this.updateOrderStateByStrictState(order.getId(), OrderState.Cancel,order.getOrderState());
                if(count == 1) {
                    List<OrderItem> orderItemList = this.queryOrderItemsByOrderId(order.getId());
                    for(OrderItem orderItem : orderItemList) {
                        this.updateOrderItemStateByStrictState(orderItem.getId(), OrderState.Cancel, orderItem.getOrderState());
                        //增加库存
                        skuAndStorageService.addSkuStock(orderItem.getSkuId(), orderItem.getNumber());
                    }
                    this.createOrderStateHistory(new OrderStateHistory(order, OrderState.Cancel.getLogMsg(), CancelOrderType.Sys.getName()));
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
        // TODO 确认收货
        try {
            this.updateOrderStateByStrictState(order.getId(), OrderState.Receiving, order.getOrderState());
            for (OrderItem oi : order.getOrderItemList()) {
                this.updateOrderItemStateByStrictState(oi.getId(), OrderState.Receiving, oi.getOrderState());
            }
            this.createOrderStateHistory(new OrderStateHistory(order, OrderState.Receiving.getLogMsg()));
        } catch (AppBusinessException a) {
            throw new AppBusinessException("确认收货失败");
        }
    }


    //////////////////////////////订单项/////////////////////////////////////////////
    /**
     * 创建订单项
     * @param orderItem
     */
    public void createOrderItem(OrderItem orderItem) {
        Logger.info("--------OrderService createOrderItem begin exe-----------" + orderItem);
        generalDao.persist(orderItem);
    }

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
     * 更新订单项
     * @param orderItem
     */
    public void updateOrderItem(OrderItem orderItem) {
        Logger.info("--------OrderService updateOrderItem begin exe-----------" + orderItem);
        generalDao.merge(orderItem);
    }

    /**
     * 更新订单项状态
     * @param orderItemId
     * @param orderState
     * @param previousState
     * @return
     */
    public int updateOrderItemStateByStrictState(int orderItemId, OrderState orderState, OrderState previousState) {
        Logger.info("--------OrderService updateOrderItemStateByStrictState begin exe-----------" + orderItemId + " : " + orderState + " : " + previousState);

        DateTime curTime = DateUtils.current();

        String jpql = "update OrderItem o set o.orderState=:orderState, o.updateTime =:updateTime ";
        Map<String, Object> params = new HashMap<>();
        params.put("orderState", orderState);
        params.put("updateTime", curTime);

        jpql += " where o.id=:orderItemId ";
        params.put("orderItemId", orderItemId);

        if(previousState != null && StringUtils.isNotEmpty(previousState.getName())) {
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
        Logger.info("--------OrderService queryOrderItemsByOrderId begin exe-----------" + orderId);

        String jpql = "select o from OrderItem o where 1=1 and o.orderId=:orderId";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("orderId", orderId);
        return generalDao.query(jpql, Optional.<Page<OrderItem>>empty(), queryParams);
    }

    /**
     * 通过订单项id删除订单项
     *
     * @param id
     */
    public void deleteOrderItemById(int id) {
        play.Logger.info("--------OrderService deleteOrderItemById real delete  begin exe-----------" + id);
        generalDao.removeById(OrderItem.class, id);
    }

    /**
     * 通过orderId来删除订单下的所有订单项
     * @param orderId
     */
    public void deleteOrderItemByOrderId(int orderId) {
        play.Logger.info("--------OrderService deleteOrderById real delete  begin exe-----------" + orderId);

        String jpql = "delete from OrderItem v where orderId=:orderId ";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("orderId", orderId);
        generalDao.update(jpql, queryParams);
    }

    ///////////////////////////订单状态历史////////////////////////////////////////////////////////////
    /**
     * 创建订单状态历史
     */
    public void createOrderStateHistory(OrderStateHistory orderStateHistory) {
        play.Logger.info("--------TradeService createOrderStateHistory begin exe-----------" + orderStateHistory);
        generalDao.persist(orderStateHistory);
    }

    /**
     * 通过订单id获取订单状态历史记录（订单跟踪）
     * @param orderId
     * @return
     */
    public List<OrderStateHistory> getOrderStateHistoryByOrderId(int orderId){
        Logger.info("--------OrderService queryOrderItemsByOrderId begin exe-----------" + orderId);

        String jpql = "select o from OrderStateHistory o where 1=1 and o.orderId=:orderId";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("orderId", orderId);
        return generalDao.query(jpql, Optional.<Page<OrderStateHistory>>empty(), queryParams);
    }

    //////////////////////////////订单物流-邮寄地址/////////////////////////////////////////////
    /**
     * 创建订单物流-邮寄地址
     * @param logistics
     */
    public void createLogistics(Logistics logistics) {
        Logger.info("--------OrderService createLogistics begin exe-----------" + logistics);
        generalDao.persist(logistics);
    }

    /**
     * 通过订单获取物流-邮寄地址
     * @param logisticsId
     * @return
     */
    @Transactional(readOnly = true)
    public Logistics getLogisticsById(int logisticsId) {
        play.Logger.info("--------OrderService getLogisticsById begin exe-----------" + logisticsId);
        return generalDao.get(Logistics.class, logisticsId);
    }

    @Transactional(readOnly = true)
    public Logistics getLogisticsByOrderId(int orderId) {
        play.Logger.info("--------OrderService getLogisticsById begin exe-----------" + orderId);

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


   public int submitOrderProcess(String selItems, boolean isPromptlyPay, User user, Cart cart, Address address) {
       int orderId = 0;
       //创建订单
       Order order = new Order();
       order.setOrderNo(OrderNumberUtil.getOrderNo()); //生成订单号
       order.setAccountType(user.getAccountType());
       order.setUserId(user.getId());
       order.setUserName(user.getUserName());
       order.setTotalMoney(cart.getTotalMoney());
       order.setOrderState(OrderState.Create);

       DateTime curTime = DateUtils.current();
       order.setCreateTime(curTime);
       order.setMilliDate(curTime.getMillis());
       order.setIsDelete(false);
       order.setBrush(false);
       this.createOrder(order);

       orderId = order.getId();

       //创建订单项目
       List<CartItem> cartItemList = cart.getCartItemList();
       for(CartItem item : cartItemList) {
           OrderItem orderItem = new OrderItem();
           orderItem.setOrderId(orderId);
           orderItem.setSkuId(item.getSkuId());
           orderItem.setBarCode(item.getBarCode());
           orderItem.setItemNo(item.getId() + "");
           orderItem.setProductId(item.getProductId());
           orderItem.setCategoryId(item.getCategoryId());
           orderItem.setStorageId(item.getStorageId());
           orderItem.setCustomerId(item.getCustomerId());
           orderItem.setProductName(item.getProductName());
           orderItem.setOrderState(order.getOrderState());
           orderItem.setStoreStrategy(StoreStrategy.PayStrategy);
           orderItem.setNumber(item.getNumber());
           orderItem.setMainPicture(item.getMainPicture());
           orderItem.setCurUnitPrice(item.getCurUnitPrice());
           orderItem.setTotalPrice(item.getTotalPrice());
           orderItem.setAppraise(false);
           this.createOrderItem(orderItem);

           //扣减库存
           skuAndStorageService.minusSkuStock(orderItem.getSkuId(), orderItem.getNumber());
       }

       //创建订单状态历史
       this.createOrderStateHistory(new OrderStateHistory(order));

       //创建订单物流-邮寄地址
       Logistics logistics = new Logistics(address);
       logistics.setOrderId(orderId);
       this.createLogistics(logistics);

       //非立即购买，需要清除用户购物车项
       if(!isPromptlyPay) {
           cartService.deleteSelectCartItemBySelIds(cart.getId(),selItems);
       }
       return orderId;
   }

}
