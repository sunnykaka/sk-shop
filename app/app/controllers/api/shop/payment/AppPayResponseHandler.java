package controllers.api.shop.payment;

import common.utils.play.BaseGlobal;
import ordercenter.models.Trade;
import ordercenter.payment.CallBackResult;
import ordercenter.payment.PayCallback;
import ordercenter.payment.constants.ResponseType;
import ordercenter.payment.constants.TradeStatus;
import ordercenter.services.TradeService;
import org.hibernate.StaleObjectStateException;
import play.Logger;

/**
 * App支付成功处理类(此处现在只考虑了微信支付)
 * User: lidujun
 * Date: 2015-09-11
 */
public class AppPayResponseHandler {

    /**
     * 送过去的交易回调处理类
     */
    protected Class<PayCallback> callBackHandlerClass;

    /**
     * 交易信息
     */
    protected Trade trade;

    /**
     * 交易在第三方支付平台返回来之前的状态
     */
    protected TradeStatus statusBeforeBack;

    /**
     * 交易服务
     */
    protected TradeService tradeService = BaseGlobal.ctx.getBean(TradeService.class);

    /**
     * 回调业务处理
     *
     * @param type 处理类型
     * @return 处理返回结果
     */
    public CallBackResult handleCallback(ResponseType type) {
        Logger.info("本次交易对象：" + trade);

        //初始化相关变量
        CallBackResult result;
        PayCallback callBackHandler;

        //实例化回调处理类，并准备好相关参数
        try {
            callBackHandler = callBackHandlerClass.newInstance();
            result = callBackHandler.initResult(this.trade, type);
        } catch (Exception e) {
            Logger.error("初始化回调处理类失败");
            throw new IllegalArgumentException("初始化回调处理类失败", e);
        }

        if (!trade.isSuccess()) {
            result.setResult(false);
            return result;
        }

        Logger.info("交易状态更新前为：" + statusBeforeBack);

        if (statusBeforeBack.equals(TradeStatus.INIT)) {
            //更新trade信息
            try {
                tradeService.createOrUpdateTrade(trade);
                Logger.info("更新交易状态完成，交易状态从【" + statusBeforeBack + "】更新为【" + trade.getTradeStatus() + "】");
                boolean bizHandResult = callBackHandler.doAfterBack(this.trade, type, result);
                Logger.info("本次业务处理结果为：" + bizHandResult);
            } catch (StaleObjectStateException e) {//并发异常不需要处理
                Logger.info("发生并发情况，notify通知同时请求过来");
            }
        }
        result.setResult(true);
        return result;
    }


    public Class<PayCallback> getCallBackHandlerClass() {
        return callBackHandlerClass;
    }

    public void setCallBackHandlerClass(Class<PayCallback> callBackHandlerClass) {
        this.callBackHandlerClass = callBackHandlerClass;
    }

    public Trade getTrade() {
        return trade;
    }

    public void setTrade(Trade trade) {
        this.trade = trade;
    }

    public TradeStatus getStatusBeforeBack() {
        return statusBeforeBack;
    }

    public void setStatusBeforeBack(TradeStatus statusBeforeBack) {
        this.statusBeforeBack = statusBeforeBack;
    }
}
