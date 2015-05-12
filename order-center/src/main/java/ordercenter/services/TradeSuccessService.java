package ordercenter.services;

import common.services.GeneralDao;
import common.utils.DateUtils;
import ordercenter.constants.OrderState;
import ordercenter.constants.TradeType;
import ordercenter.models.Order;
import ordercenter.models.OrderItem;
import ordercenter.models.OrderStateHistory;
import ordercenter.models.Trade;
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
        this.updateTradeOrderPaySuccessful(trade);

        long orderNo = 0l;
        try {
            orderNo = Long.parseLong(trade.getOrderNo());
        } catch (NumberFormatException e) {
           // throw new OrderNoTransactionalException ( "没有此订单[" + orderNo + "]" );
        }

        Order order = orderService.getOrderByOrderNo(orderNo);
        if(order == null) {
            // throw new OrderNoTransactionalException ( "没有此订单[" + orderNo + "]" );
        }
        this.createOrderStateHistory(new OrderStateHistory(order));

        OrderState oldState = order.getOrderState();
        // 订单状态只能从 创建 到 付款成功
        order.setMustPreviousState(OrderState.Create);
        order.setOrderState(OrderState.Pay);

        if (updateOrderStateByStrictState(order.getId(), order.getOrderState(), order.getMustPreviousState()) != 1 ) {
//                        throw new OrderTransactionalException ( "订单[" + orderNo + "]不能从当前状态[" + oldState.serviceDesc ( ) + "]更新为["
//                                + order.getOrderState ( )
//                                .serviceDesc ( ) + "](只能从[" + order.getMustPreviousState ( )
//                                .serviceDesc ( ) + "]变更)" );
        }

        List<OrderItem> orderItemList = orderService.queryOrderItemsByOrderId(order.getId());
        if(orderItemList != null && orderItemList.size() > 0) {
            for(OrderItem orderItem : orderItemList) {
                this.updateOrderItemStateByStrictState(orderItem.getId(), order.getOrderState(), order.getMustPreviousState());
                // 操作库存
                StoreStrategyServiceFactory.getStoreStrategyServiceImpl(orderItem.getStoreStrategy())
                        .operateStorageWhenPayOrder(this, orderItem.getSkuId(), orderItem.getNumber());




                //统计付款成功数.
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




            }
        } else {
            //没有订单项
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

        String jpql = "update tradeOrder o set o.payFlag=:payFlag, o.tradeType='BuyProduct',updateTime=:updateTime ";
        Map<String, Object> params = new HashMap<>();
        params.put("payFlag", 1); //ldj
        params.put("tradeType", TradeType.BuyProduct);
        params.put("tradeType", DateUtils.current());
        jpql += " where o.tradeNo=:tradeNo";
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

        String jpql = "update Order o set o.orderState=:orderState, o.updateTime =:modifyDate ";
        Map<String, Object> params = new HashMap<>();
        params.put("orderState", orderState);
        params.put("modifyDate", curTime);

        if("Pay".equals(orderState.getName())) {
            jpql += ", o.payDate =:payDate ";
            params.put("payDate", curTime);
        } else if("Cancel".equals(orderState.getName()) || "Close".equals(orderState.getName()) || "Success".equals(orderState.getName())) {
            jpql += ", o.endDate =:endDate ";
            params.put("endDate", curTime);
        }
        jpql += " where o.id=:orderId and o.orderState =:previousState";
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
