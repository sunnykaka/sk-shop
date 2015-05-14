package controllers.shop;


import ordercenter.models.Order;
import ordercenter.models.Trade;
import ordercenter.models.TradeOrder;
import ordercenter.payment.CallBackResult;
import ordercenter.payment.PayCallback;
import ordercenter.payment.constants.ResponseType;
import ordercenter.services.OrderService;
import ordercenter.services.TradeService;
import ordercenter.services.TradeSuccessService;
import org.springframework.beans.factory.annotation.Autowired;
import play.Logger;

import java.util.ArrayList;
import java.util.List;


/**
 * 订单支付后回调类
 * User: lidujun
 * Date: 2015-05-08
 */
public class OrderPayCallback implements PayCallback {

    public static final String PAY_SUCCESS = "/order/paySuccess";
    public static final String PAY_FAIL = "/order/payFail";

    @Autowired
    private OrderService orderService;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private TradeSuccessService tradeSuccessService;

    @Override
    public CallBackResult initResult(Trade tradeInfo, ResponseType type){
        Logger.info("「订单回调」开始初始化CallBackResult");
        CallBackResult result = new CallBackResult(PAY_SUCCESS, PAY_FAIL);

        //ldj 后续要优化
        List<TradeOrder> tradeOrderList = tradeService.getTradeOrdeByTradeNo(tradeInfo.getTradeNo());
        List<Order> orderList = new ArrayList<Order>();
        if(tradeOrderList != null && tradeOrderList.size() > 0) {
            for(TradeOrder t : tradeOrderList) {
                Order order = orderService.getOrderByOrderNo(t.getOrderNo());
                if(order != null) {
                    orderList.add(order);
                }
            }
        }
        result.addData("orders", orderList);
        result.addData("total_fee", tradeInfo.getPayTotalFee());
        Logger.info("「订单回调」CallBackResult初始化Success");
        return result;
    }

    @Override
    public boolean doAfterBack(Trade tradeInfo, ResponseType type, CallBackResult result) {
        Logger.info("「订单回调」开始更新订单状态");
        try {
            tradeSuccessService.changeOrderStateWhenPaySuccess(tradeInfo);

            //ldj 后续要优化
            List<TradeOrder> tradeOrderList = tradeService.getTradeOrdeByTradeNo(tradeInfo.getTradeNo());
            List<Order> orderList = new ArrayList<Order>();
            if(tradeOrderList != null && tradeOrderList.size() > 0) {
                for(TradeOrder t : tradeOrderList) {
                    Order order = orderService.getOrderByOrderNo(t.getOrderNo());
                    if(order != null) {
                        orderList.add(order);
                    }
                }
            }
            result.addData("orders", orderList);
        } catch (Exception e) {
            Logger.error("「订单回调」订单支付成功，但是更新订单状态、扣减库存、记录支付成功等数据失败，需要运营人员确认一下："  + e.getMessage(), e);
            return false;
        }
        return true;
    }

}
