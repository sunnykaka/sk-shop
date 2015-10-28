package ordercenter.services;

import common.exceptions.AppBusinessException;
import common.services.GeneralDao;
import common.utils.DateUtils;
import common.utils.JsonResult;
import common.utils.Money;
import ordercenter.constants.BizType;
import ordercenter.constants.OrderState;
import ordercenter.constants.TradePayType;
import ordercenter.constants.TradeType;
import ordercenter.models.*;
import ordercenter.payment.constants.PayBank;
import ordercenter.payment.constants.PayMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import play.Logger;
import usercenter.constants.MarketChannel;
import usercenter.models.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 交易Service
 * User: lidujun
 * Date: 2015-04-29
 */
@Service
@Transactional
public class TradeService {

    private static final Logger.ALogger tradeLogger = Logger.of("tradeRequest");

    @Autowired
    private GeneralDao generalDao;

    @Autowired
    private OrderService orderService;


    /**
     * 创建交易记录
     *
     * @param trade
     */
    public void createOrUpdateTrade(Trade trade) {
        Logger.info("--------TradeService createOrUpdateTrade begin exe-----------" + trade);
        generalDao.persist(trade);
    }

    /**
     * 通过tradeNo获取交易
     *
     * @param tradeNo 交易号
     * @return
     */
    @Transactional(readOnly = true)
    public Trade getTradeByTradeNo(String tradeNo) {
        Logger.info("--------TradeService getTradeByTradeNo begin exe-----------" + tradeNo);

        String jpql = "select v from Trade v where 1=1 ";
        Map<String, Object> queryParams = new HashMap<>();
        jpql += " and v.tradeNo = :tradeNo ";
        queryParams.put("tradeNo", tradeNo);

        Trade trade = null;
        List<Trade> itemList = generalDao.query(jpql, Optional.ofNullable(null), queryParams);
        if (itemList != null && itemList.size() > 0) {
            trade = itemList.get(0);
        }
        return trade;
    }

    /**
     * 通过tradeNo获取交易
     *
     * @param tradeNo      交易号
     * @param outerTradeNo 第三方平台交易号
     * @return
     */
    @Transactional(readOnly = true)
    public Trade getTradeByTradeNo(String tradeNo, String outerTradeNo) {
        Logger.info("--------TradeService getTradeByTradeNo begin exe-----------" + tradeNo + " : " + outerTradeNo);

        String jpql = "select v from Trade v where 1=1 ";
        Map<String, Object> queryParams = new HashMap<>();
        jpql += " and v.tradeNo = :tradeNo ";
        queryParams.put("tradeNo", tradeNo);
        jpql += " and v.outerTradeNo = :outerTradeNo ";
        queryParams.put("outerTradeNo", outerTradeNo);

        Trade trade = null;
        List<Trade> itemList = generalDao.query(jpql, Optional.ofNullable(null), queryParams);
        if (itemList != null && itemList.size() > 0) {
            trade = itemList.get(0);
        }
        return trade;
    }

    /**
     * 判断是否已经存在此交易记录
     *
     * @param tradeNo
     * @param outerTradeNo
     * @return
     */
    public boolean existTrade(String tradeNo, String outerTradeNo) {
        Logger.info("--------TradeService existTradeInfo begin exe-----------" + tradeNo + " : " + outerTradeNo);
        Trade trade = this.getTradeByTradeNo(tradeNo, outerTradeNo);
        return trade != null;
    }


    //////////////////////交易订单/////////////////////////////////

    /**
     * 创建交易记录
     *
     * @param tradeOrder
     */
    public void createTradeOrder(TradeOrder tradeOrder) {
        Logger.info("--------TradeService createTradeOrder begin exe-----------" + tradeOrder);
        generalDao.persist(tradeOrder);
    }

    /**
     * 通过tradeNo获取TradeOrder列表
     *
     * @param tradeNo 交易号
     * @return
     */
    @Transactional(readOnly = true)
    public List<TradeOrder> getTradeOrdeByTradeNo(String tradeNo) {
        Logger.info("--------TradeService getTradeOrdeByTradeNo begin exe-----------" + tradeNo);

        String jpql = "select v from TradeOrder v where 1=1 ";
        Map<String, Object> queryParams = new HashMap<>();
        jpql += " and v.tradeNo = :tradeNo ";
        queryParams.put("tradeNo", tradeNo);

        return generalDao.query(jpql, Optional.ofNullable(null), queryParams);
    }

    /**
     * 通过订单ID查询支付交易信息
     *
     * @param orderId
     * @return
     */
    @Transactional(readOnly = true)
    public Trade getTradeOrdeByOrderId(int orderId) {

        //select o2. from Trade o2 exists(select 1 from TradeOrder o1 where o2.tradeNo=o1.tradeNo and o1.orderId=:orderId)

        String jpql = "select o2 from Trade o2 where exists(select 1 from TradeOrder o1 where o2.tradeNo=o1.tradeNo and o1.orderId=:orderId)";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("orderId", orderId);

        List<Trade> tradeList = generalDao.query(jpql, Optional.ofNullable(null), queryParams);
        Trade trade = null;
        if (tradeList != null && tradeList.size() > 0) {
            trade = tradeList.get(0);
        }
        return trade;
    }

    /**
     * 支付交易处理方法
     *
     * @param tradeNo
     * @param orderList
     */
    public void submitTradeOrderProcess(String tradeNo, List<Order> orderList, PayMethod payMethodEnum) {
        Logger.info("--------TradeService submitTradeOrderProcess begin exe-----------" + tradeNo);
        for (Order order : orderList) {
            //创建交易订单信息
            TradeOrder tradeOrder = new TradeOrder();
            tradeOrder.setTradeNo(tradeNo);
            tradeOrder.setOrderId(order.getId());
            tradeOrder.setOrderNo(order.getOrderNo());
            tradeOrder.setPayMethod(payMethodEnum);
            tradeOrder.setPayFlag(false);
            tradeOrder.setTradeType(TradeType.BuyProduct);
            tradeOrder.setPayTotalFee(order.getTotalMoney());
            tradeOrder.setBizType(BizType.Order);
            tradeOrder.setDefaultPayOrg(order.getPayBank().toString());
            tradeOrder.setIsDelete(false);
            this.createTradeOrder(tradeOrder);
        }
    }

    /**
     * 支付成功后修改tradeOrder
     *
     * @param trade
     * @return
     */
    public int updateTradeOrderPaySuccessful(Trade trade) {
        Logger.info("--------TradeService updateTradeOrderPaySuccessful begin exe-----------" + trade);

        String jpql = "update TradeOrder o set o.payFlag=:payFlag,o.updateTime=:updateTime where o.tradeNo=:tradeNo ";
        Map<String, Object> params = new HashMap<>();
        params.put("payFlag", true);
        params.put("updateTime", DateUtils.current());
        params.put("tradeNo", trade.getTradeNo());

        return generalDao.update(jpql, params);
    }

    ////////////////////////SkuTradeResult////////////////////////////

    /**
     * 创建SkuTradeResult
     *
     * @param skuTradeResult
     */
    public void createSkuTradeResult(SkuTradeResult skuTradeResult) {
        Logger.info("--------TradeService createSkuTradeResult begin exe-----------" + skuTradeResult);
        generalDao.persist(skuTradeResult);
    }

    /**
     * 更新SkuTradeResult
     *
     * @param skuTradeResult
     */
    public void updateSkuTradeResult(SkuTradeResult skuTradeResult) {
        play.Logger.info("--------TradeService updateCart begin exe-----------" + skuTradeResult);
        generalDao.merge(skuTradeResult);
    }

    /**
     * 通过SkuId获取SkuTradeResult
     *
     * @param skuId
     * @return
     */
    @Transactional(readOnly = true)
    public SkuTradeResult getSkuTradeResultBySkuId(int skuId) {
        Logger.info("--------TradeService getSkuTradeResultBySkuId begin exe-----------" + skuId);

        String jpql = "select v from SkuTradeResult v where 1=1 ";
        Map<String, Object> queryParams = new HashMap<>();
        jpql += " and v.skuId = :skuId ";
        queryParams.put("skuId", skuId);

        SkuTradeResult skuTradeResult = null;
        List<SkuTradeResult> itemList = generalDao.query(jpql, Optional.ofNullable(null), queryParams);
        if (itemList != null && itemList.size() > 0) {
            skuTradeResult = itemList.get(0);
        }
        return skuTradeResult;
    }

    ///////////////////////////////交易（支付）成功后续处理///////////////////////////////////////////

    /**
     * 支付成功后续处理
     *
     * @param trade
     */
    public void paySuccessAfterProccess(Trade trade) {
        String tradeNo = trade.getTradeNo();
        String errPrefix = "需要运营人员手动确认：第三方交易成功，但是后续操作异常,交易号为[" + tradeNo + "]-";
        try {
            this.updateTradeOrderPaySuccessful(trade);
        } catch (Exception e) {
            Logger.error(errPrefix + "更新交易" + trade.getTradeNo() + "失败！", e);
        }


        List<TradeOrder> tradeOrderList = trade.getTradeOrder();
        /**
         * 将tradeOrder放在里面，方便页面上进行展示
         */
        trade.setTradeOrder(tradeOrderList);
        /**
         * 不需要对TradeOrder是否有数据进行校验，因为Trade已经校验过，
         * 循环对所有的订单状态进行更新即可。
         */
        for (TradeOrder tradeOrder : tradeOrderList) {
            Order preOrder = orderService.getOrderById(tradeOrder.getOrderId());

//            Order order = new Order();
//            order.setId(preOrder.getId());
//            order.setOrderState(preOrder.getOrderState());
//            order.setUserName(preOrder.getUserName());
//
//            long orderNo = order.getOrderNo();
//
//            //更新订单
//            OrderState oldState = order.getOrderState();
//            // 订单状态只能从 创建 到 付款成功

            /**
             * 将订单状态从已创建，更改为已付款
             */
            try {
                orderService.updateOrderStateByStrictState(preOrder.getId(), OrderState.Pay, OrderState.Create);
            } catch (Exception e) {
                Logger.error("将订单状态从已创建改更为已经付款时失败，订单id:=" + preOrder.getId(), e);
            }

            preOrder.setMustPreviousState(OrderState.Create);
            preOrder.setOrderState(OrderState.Pay);  //支付成功


            //记录订单状态历史信息
            try {
                orderService.createOrderStateHistory(new OrderStateHistory(preOrder));
            } catch (Exception e) {
                Logger.error("创建订单状态变更历史失败", e);
            }


            //更新订单项
            List<OrderItem> orderItemList = orderService.queryOrderItemsByOrderId(preOrder.getId());
            if (orderItemList == null || orderItemList.size() < 0) {
                Logger.error(errPrefix + "第三方支付成功，返回系统后找不到支付订单的相关内容");
            } else {
                for (OrderItem orderItem : orderItemList) {
                    try {
                        orderService.updateOrderItemStateByStrictState(orderItem.getId(), OrderState.Pay, OrderState.Create);
                    } catch (Exception e) {
                        Logger.error("更新订单项状态失败", e);
                    }

                    //统计付款成功数(这个统计数据其实很不精确)
                    try {
                        SkuTradeResult tradeResult = this.getSkuTradeResultBySkuId(orderItem.getSkuId());
                        if (tradeResult == null) {
                            SkuTradeResult skuTradeResult = new SkuTradeResult();
                            skuTradeResult.setProductId(orderItem.getProductId());
                            skuTradeResult.setSkuId(orderItem.getSkuId());
                            skuTradeResult.setPayNumber(orderItem.getNumber());
                            this.createSkuTradeResult(skuTradeResult);
                        } else {
                            // 数量累加
                            tradeResult.appendedPayNumber(orderItem.getNumber());
                            this.updateSkuTradeResult(tradeResult);
                        }
                    } catch (Exception e) {
                        Logger.error("更新Sku销售统计数量失败", e);
                    }
                }
            }
        }
    }


    public Trade submitTradeOrder(User user, List<Integer> orderIds, PayBank payBank, MarketChannel channel, String ip) {

        List<Order> orders = orderIds.stream().map(orderService::getOrderById).collect(Collectors.toList());
        for(Order order : orders) {

            checkOrderStateToPay(order);

            order.setAccountType(user.getAccountType());
            order.setPayType(TradePayType.OnLine);
            order.setPayBank(payBank);
            order.setUpdateTime(DateUtils.current());

            generalDao.persist(order);
        }

        tradeLogger.info("校验订单信息通过，开始创建交易相关信息");

        /**
         * 1.计算出订单总金额
         * 2.创建交易
         * 3.保存tradeOrder信息
         * 4.创建trade,将交易的表单刷到页面
         * 5.跳转去第三方支付平台付款
         */
        long totalFee = orders.stream().map(Order::getTotalMoney).reduce(Money.valueOf(0d), Money::add).getCent();

        Trade trade = Trade.TradeBuilder.createNewTrade(Money.valueOfCent(totalFee), BizType.Order, payBank, ip, channel);

        submitTradeOrderProcess(trade.getTradeNo(), orders, payBank.getPayMethod());

        return trade;
    }

    private void checkOrderStateToPay(Order order) {

        if (!order.getOrderState().waitPay(TradePayType.OnLine)) {

            tradeLogger.error(String.format("订单[orderNo=%s]支付出现异常，订单状态[%s]",
                    String.valueOf(order.getOrderNo()), order.getOrderState()));

            if (order.getOrderState().getName().equals(OrderState.Cancel.getName())) {
                throw new AppBusinessException("订单支付出现异常，该订单已取消，请重新下单！");
            } else {
                throw new AppBusinessException("订单支付出现异常，该订单已支付，请勿重复支付！");
            }
        }


    }
}
