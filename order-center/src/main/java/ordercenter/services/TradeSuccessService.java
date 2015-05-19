package ordercenter.services;

import common.services.GeneralDao;
import common.utils.DateUtils;
import ordercenter.constants.OrderState;
import ordercenter.models.*;
import ordercenter.payment.models.SkuTradeResult;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import play.Logger;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 交易成功后处理Service
 * User: lidujun
 * Date: 2015-05-11
 * 有些东西需要在此冗余
 */
@Service
@Transactional
public class TradeSuccessService {

    @Autowired
    GeneralDao generalDao;

    @Autowired
    OrderService orderService;

    @Autowired
    private TradeService tradeService;

    public void changeOrderStateWhenPaySuccess(Trade trade) {
        String errPrefix = "需要运营人员手动确认：第三方交易成功，但是后续操作异常,交易号为[" + trade.getTradeNo() + "]-";
        try {
            this.updateTradeOrderPaySuccessful(trade);
        } catch (Exception e) {
            Logger.error(errPrefix + "更新交易" + trade.getTradeNo() + "失败！", e);
        }

        List<TradeOrder> tradeOrderList = tradeService.getTradeOrdeByTradeNo(trade.getTradeNo());
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
                        this.createOrderStateHistory(new OrderStateHistory(order));
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
                        this.updateOrderStateByStrictState(order.getId(), order.getOrderState(), order.getMustPreviousState());
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
                                this.updateOrderItemStateByStrictState(orderItem.getId(), order.getOrderState(), order.getMustPreviousState());
                            } catch (Exception e) {
                                Logger.error(errPrefix + "订单[" + orderNo + "]订单项id[" + orderItem.getId() + "]从当前状态[" + oldState.getValue() + "]更新为["
                                        + order.getOrderState().getValue() + "]发生异常", e);
                            }

                            //扣减库存
                            try {
                                StoreStrategyServiceFactory.getStoreStrategyServiceImpl(orderItem.getStoreStrategy())
                                        .operateStorageWhenPayOrder(this, orderItem.getSkuId(), orderItem.getNumber());
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

    /**
     * 减去sku库存数
     * @param skuId
     * @param number
     * @return
     */
    public boolean minusSkuStock(int skuId, int number) {
        play.Logger.info("------SkuAndStorageService minusSkuStock begin exe-----------" + skuId + ":" + number);
        EntityManager em = generalDao.getEm();
        Query query = em.createNativeQuery("update SkuStorage set stockQuantity = stockQuantity - ? where skuId=?").setParameter(1, number).setParameter(2, skuId);
        int count = query.executeUpdate();
        return count == 1;
    }

    /**
     * 增加sku库存数
     * @param skuId
     * @param number
     * @return
     */
    public boolean addSkuStock(int skuId, int number) {
        play.Logger.info("------SkuAndStorageService addSkuStock begin exe-----------" + skuId + ":" + number);
        EntityManager em = generalDao.getEm();
        Query query = em.createNativeQuery("update SkuStorage set stockQuantity = stockQuantity + ? where skuId=?").setParameter(1, number).setParameter(2, skuId);
        int count = query.executeUpdate();
        return count == 1;
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
     * 创建状态历史
     */
    public void createOrderStateHistory(OrderStateHistory orderStateHistory) {
        play.Logger.info("--------TradeSuccessService createOrderStateHistory begin exe-----------" + orderStateHistory);
        generalDao.persist(orderStateHistory);
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

}