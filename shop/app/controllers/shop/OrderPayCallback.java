package controllers.shop;


import common.utils.play.BaseGlobal;
import ordercenter.models.Trade;
import ordercenter.payment.CallBackResult;
import ordercenter.payment.PayCallback;
import ordercenter.payment.constants.ResponseType;
import ordercenter.services.TradeService;
import play.Logger;


/**
 * 订单支付后回调类
 * User: lidujun
 * Date: 2015-05-08
 */
public class OrderPayCallback implements PayCallback {

    public static final String PAY_SUCCESS = "/order/paySuccess";
    public static final String PAY_FAIL = "/order/payFail";

    @Override
    public CallBackResult initResult(Trade tradeInfo, ResponseType type){
        Logger.info("「订单回调」开始初始化CallBackResult");
        CallBackResult result = new CallBackResult(PAY_SUCCESS, PAY_FAIL);
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
            return false;
        }
        return true;
    }

}
