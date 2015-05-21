package ordercenter.services;

import common.services.GeneralDao;
import common.utils.DateUtils;
import ordercenter.constants.OrderState;
import ordercenter.constants.TradeType;
import ordercenter.models.*;
import ordercenter.payment.PayInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import play.Logger;
import productcenter.services.SkuAndStorageService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 交易Service
 * User: lidujun
 * Date: 2015-04-29
 */
@Service
@Transactional
public class TradeService {

    @Autowired
    private GeneralDao generalDao;

    @Autowired
    private OrderService orderService;

    @Autowired
    private SkuAndStorageService skuAndStorageService;

    /**
     * 创建交易记录
     * @param trade
     */
    public void createTrade(Trade trade) {
        Logger.info("--------TradeService createTrade begin exe-----------" + trade);
        generalDao.persist(trade);
    }

    /**
     * 通过tradeNo获取交易
     * @param tradeNo 交易号
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
        if(itemList != null && itemList.size() > 0) {
            trade = itemList.get(0);
        }
        return trade;
    }

    /**
     * 判断是否已经存在此交易记录
     * @param tradeNo
     * @param outerTradeNo
     * @return
     */
    public boolean existTrade(String tradeNo, String outerTradeNo) {
        Logger.info("--------TradeService existTradeInfo begin exe-----------" + tradeNo + " : " + outerTradeNo);
        Trade trade = this.getTradeByTradeNo(tradeNo, outerTradeNo);
        return   trade != null;
    }


    //////////////////////交易订单/////////////////////////////////
    /**
     * 创建交易记录
     * @param tradeOrder
     */
    public void createTradeOrder(TradeOrder tradeOrder) {
        Logger.info("--------TradeService createTradeOrder begin exe-----------" + tradeOrder);
        generalDao.persist(tradeOrder);
    }


    /**
     * 通过tradeNo获取TradeOrder列表
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
     * 支付交易处理方法
     * @param payInfoWrapper
     * @param orderList
     */
    public void submitTradeOrderProcess(PayInfoWrapper payInfoWrapper, List<Order> orderList) {
        String tradeNo = payInfoWrapper.getTradeNo();
        for(Order order : orderList) {
            //创建交易订单信息
            TradeOrder tradeOrder = new TradeOrder();
            tradeOrder.setTradeNo(tradeNo);
            tradeOrder.setOrderId(order.getId());
            tradeOrder.setOrderNo(order.getOrderNo());
            tradeOrder.setTradeType(TradeType.BuyProduct);
            tradeOrder.setPayFlag(false);
            this.createTradeOrder(tradeOrder);

            //更新订单
            String jpql = "update Order o set o.accountType=:accountType, o.payType=:payType, o.payBank=:payBank, o.updateTime=:modifyDate where o.id=:orderId";
            Map<String, Object> params = new HashMap<>();
            params.put("accountType", order.getAccountType());
            params.put("payType", order.getPayType());
            params.put("payBank", order.getPayBank());
            params.put("modifyDate", DateUtils.current());
            params.put("orderId", order.getId());
            generalDao.update(jpql, params);
        }
    }

    /**
     * 支付成功后修改tradeOrder
     * @param trade
     * @return
     */
    public int updateTradeOrderPaySuccessful(Trade trade) {
        Logger.info("--------TradeSuccessService updateTradeOrderPaySuccessful begin exe-----------" + trade);

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
     * @param skuTradeResult
     */
    public void createSkuTradeResult(SkuTradeResult skuTradeResult) {
        Logger.info("--------TradeService createSkuTradeResult begin exe-----------" + skuTradeResult);
        generalDao.persist(skuTradeResult);
    }

    /**
     * 更新SkuTradeResult
     * @param skuTradeResult
     */
    public void updateSkuTradeResult(SkuTradeResult skuTradeResult) {
        play.Logger.info("--------TradeService updateCart begin exe-----------" + skuTradeResult);
        generalDao.merge(skuTradeResult);
    }

    /**
     * 通过SkuId获取SkuTradeResult
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
        if(itemList != null && itemList.size() > 0) {
            skuTradeResult = itemList.get(0);
        }
        return skuTradeResult;
    }

    ///////////////////////////////交易（支付）成功后续处理///////////////////////////////////////////
    /**
     * 支付成功后续处理
     * @param trade
     */
    public void paySuccessAfterProccess(Trade trade) {
        String errPrefix = "需要运营人员手动确认：第三方交易成功，但是后续操作异常,交易号为[" + trade.getTradeNo() + "]-";
        try {
            this.updateTradeOrderPaySuccessful(trade);
        } catch (Exception e) {
            Logger.error(errPrefix + "更新交易" + trade.getTradeNo() + "失败！", e);
        }

        List<TradeOrder> tradeOrderList = this.getTradeOrdeByTradeNo(trade.getTradeNo());
        if(tradeOrderList == null || tradeOrderList.size() == 0) {
            Logger.error(errPrefix + "系统中找不到交易信息！");
        } else {
            for(TradeOrder tradeOrder : tradeOrderList) {
                Order order = orderService.getOrderById(tradeOrder.getOrderId());
                if(order == null || order.getId() <= 0) {
                    Logger.error(errPrefix + "系统中找不到订单，订单id[" + tradeOrder.getOrderId() + "]");
                } else {
                    long orderNo = order.getOrderNo();

                    //记录订单状态历史信息
                    try {
                        orderService.createOrderStateHistory(new OrderStateHistory(order));
                    } catch (Exception e) {
                        Logger.error(errPrefix + "订单[" + orderNo + "]订单id[" + order.getId() + "]记录订单状态历史信息发生异常：", e);
                    }

                    //更新订单
                    OrderState oldState = order.getOrderState();
                    // 订单状态只能从 创建 到 付款成功
                    order.setMustPreviousState(OrderState.Create);
                    order.setOrderState(OrderState.Pay);  //支付成功

                    //更新订单状态
                    try {
                        orderService.updateOrderStateByStrictState(order.getId(), order.getOrderState(), order.getMustPreviousState());
                    } catch (Exception e) {
                        Logger.error(errPrefix + "订单[" + orderNo + "]订单id[" + order.getId() + "]从当前状态[" + oldState.getValue()
                                + "]更新为[" + order.getOrderState().getValue() + "]发生异常：", e);
                    }

                    //更新订单项
                    List<OrderItem> orderItemList = orderService.queryOrderItemsByOrderId(order.getId());
                    if(orderItemList == null || orderItemList.size() < 0) {
                        Logger.error(errPrefix + "第三方支付成功，返回系统后找不到支付订单的相关内容");
                    } else {
                        for(OrderItem orderItem : orderItemList) {
                            try {
                                orderService.updateOrderItemStateByStrictState(orderItem.getId(), order.getOrderState(), order.getMustPreviousState());
                            } catch (Exception e) {
                                Logger.error(errPrefix + "订单[" + orderNo + "]订单项id[" + orderItem.getId() + "]从当前状态[" + oldState.getValue() + "]更新为["
                                        + order.getOrderState().getValue() + "]发生异常", e);
                            }

                            //扣减库存
                            try {
                                skuAndStorageService.minusSkuStock(orderItem.getSkuId(), orderItem.getNumber());
                            } catch (Exception e) {
                                Logger.error(errPrefix + "订单[" + orderNo + "]订单项id[" + orderItem.getId() + "]扣减库存发生异常", e);
                            }

                            //统计付款成功数
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
                                Logger.error(errPrefix + "订单[" + orderNo + "]订单项id[" + orderItem.getId() + "]统计付款成功数发生异常", e);
                            }
                        }
                    }
                }
            }
        }
    }
}
