package ordercenter.services;

import common.utils.play.BaseGlobal;
import ordercenter.models.Trade;
import ordercenter.models.TradeOrder;
import ordercenter.payment.CallBackResult;
import ordercenter.payment.PayCallback;
import ordercenter.payment.constants.ResponseType;
import play.Logger;

import java.util.List;

/**
 * 订单支付后回调类
 * User: lidujun
 * Date: 2015-05-08
 */
public class OrderPayCallbackProcess implements PayCallback {

    private TradeService tradeService = BaseGlobal.ctx.getBean(TradeService.class);

    @Override
    public CallBackResult initResult(Trade trade, ResponseType type) {
        Logger.info("「订单回调」开始初始化CallBackResult");
        CallBackResult result = new CallBackResult();
        result.addData("payTotalFee", trade.getPayTotalFee());

        List<TradeOrder> tradeOrderList = tradeService.getTradeOrdeByTradeNo(trade.getTradeNo());
        trade.setTradeOrder(tradeOrderList);

        result.addData("tradeOrderList", tradeOrderList);
        StringBuilder orderIdSb = new StringBuilder();
        for (TradeOrder tradeOrder : tradeOrderList) {
            if (orderIdSb.length() > 0) {
                orderIdSb.append("_");
            }
            orderIdSb.append(tradeOrder.getOrderId());
        }
        result.addData("errOrderIds", orderIdSb.toString());
        result.setResult(true);
        Logger.info("「订单回调」CallBackResult初始化Success");
        return result;
    }

    @Override
    public boolean doAfterBack(Trade tradeInfo, ResponseType type, CallBackResult result) {
        Logger.info("「订单回调」开始更新订单状态");
        try {

            tradeService.paySuccessAfterProccess(tradeInfo);
        } catch (Exception e) {
            Logger.error("「订单回调」订单支付成功，但是更新订单状态、扣减库存、记录支付成功等数据失败，需要运营人员确认一下：" + e.getMessage(), e);
        }
        return true;
    }

}
