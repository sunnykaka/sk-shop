package controllers.shop;


import common.utils.play.BaseGlobal;
import ordercenter.models.Trade;
import ordercenter.models.TradeOrder;
import ordercenter.payment.CallBackResult;
import ordercenter.payment.PayCallback;
import ordercenter.payment.constants.ResponseType;
import ordercenter.services.TradeService;
import play.Logger;

import java.util.List;


/**
 * 订单支付后回调类
 * User: lidujun
 * Date: 2015-05-08
 */
public class OrderPayCallback implements PayCallback {

    @Override
    public CallBackResult initResult(Trade trade, ResponseType type){
        Logger.info("「订单回调」开始初始化CallBackResult");
        CallBackResult result = new CallBackResult();
        result.addData("payTotalFee", trade.getPayTotalFee());

        result.addData("orderNo", 0);
        result.addData("orderId", 0);
        TradeService tradeService = BaseGlobal.ctx.getBean(TradeService.class);
        List<TradeOrder> tradeOrderList = tradeService.getTradeOrdeByTradeNo(trade.getTradeNo());

        if(tradeOrderList == null || tradeOrderList.size() == 0) {
            Logger.error("「订单回调」订单支付成功，但是系统中找不到交易信息！，此交易号号为：" + trade.getTradeNo());
        } else {
            //现在只对一个订单进行支付
            TradeOrder tradeOrder = tradeOrderList.get(0);
            result.addData("orderNo", tradeOrder.getOrderNo());
            result.addData("orderId", tradeOrder.getOrderId());
        }
        Logger.info("「订单回调」CallBackResult初始化Success");
        return result;
    }

    @Override
    public boolean doAfterBack(Trade tradeInfo, ResponseType type, CallBackResult result) {
        Logger.info("「订单回调」开始更新订单状态");
        try {
            TradeService tradeService = BaseGlobal.ctx.getBean(TradeService.class);
            tradeService.paySuccessAfterProccess(tradeInfo);
        } catch (Exception e) {
            Logger.error("「订单回调」订单支付成功，但是更新订单状态、扣减库存、记录支付成功等数据失败，需要运营人员确认一下："  + e.getMessage(), e);
        }
        return true;
    }

}
