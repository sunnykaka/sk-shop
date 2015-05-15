package ordercenter.services;

import common.services.GeneralDao;
import ordercenter.constants.TradeType;
import ordercenter.models.Order;
import ordercenter.models.OrderStateHistory;
import ordercenter.models.Trade;
import ordercenter.models.TradeOrder;
import ordercenter.payment.PayInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import play.Logger;

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
    GeneralDao generalDao;

    @Autowired
    OrderService orderService;

    @Autowired
    private CartService cartService;

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
    public boolean existTradeInfo(String tradeNo, String outerTradeNo) {
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
            tradeOrder.setOrderNo(order.getOrderNo());
            tradeOrder.setTradeType(TradeType.BuyProduct);
            tradeOrder.setPayFlag(false);
            this.createTradeOrder(tradeOrder);

            //更新订单
            String jpql = "update Order o set o.accountType=:accountType, o.payType=:payType, o.payBank=:payBank where o.orderNo=:orderNo";
            Map<String, Object> params = new HashMap<>();
            params.put("accountType", order.getAccountType());
            params.put("payType", order.getPayType());
            params.put("payBank", order.getPayBank());
            generalDao.update(jpql, params);

            //创建状态历史
            OrderStateHistory orderStateHistory = new OrderStateHistory(order);
            generalDao.persist(orderStateHistory);
        }
    }

}
