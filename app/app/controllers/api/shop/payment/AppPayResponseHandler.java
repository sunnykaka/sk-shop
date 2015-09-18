package controllers.api.shop.payment;

import common.utils.DateUtils;
import common.utils.play.BaseGlobal;
import ordercenter.constants.BizType;
import ordercenter.models.Trade;
import ordercenter.payment.CallBackResult;
import ordercenter.payment.PayCallback;
import ordercenter.payment.constants.ResponseType;
import ordercenter.payment.constants.TradeStatus;
import ordercenter.services.TradeService;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.StaleObjectStateException;
import org.joda.time.DateTime;
import play.Logger;

import java.util.Map;

/**
 * App支付成功处理类(此处现在只考虑了微信支付)
 * User: lidujun
 * Date: 2015-09-11
 */
public class AppPayResponseHandler {

    /**
     * 交易信息
     */
    private Trade trade;
    /**
     * 送过去的交易回调处理类
     */
    private Class<PayCallback> callBackHandlerClass;

    /**
     * 交易服务
     */
    private TradeService tradeService = BaseGlobal.ctx.getBean(TradeService.class);

    /**
     * 交易在第三方支付平台返回来之前的状态
     */
    private TradeStatus statusBeforeBack;

    /**
     * 其它方式初始化数据
     * @param param
     */
    public AppPayResponseHandler(Map<String, String> param) {
        //先从回调参数中取出交易号，并查出对应出交易记录
        String tradeNo = param.get("out_trade_no");
        if (StringUtils.isBlank(tradeNo)) {
            throw new IllegalArgumentException("交易不存在");
        }
        trade = tradeService.getTradeByTradeNo(tradeNo);
        if (trade == null) {
            throw new IllegalArgumentException("交易不存在");
        }
        statusBeforeBack = TradeStatus.valueOf(trade.getTradeStatus());

        //trade.setNotifyId("");
        trade.setNotifyType(param.get("trade_type")); //trade_type
        trade.setDefaultbank(param.get("bank_type"));
        trade.setTradeGmtCreateTime(DateUtils.parse(param.get("time_end"), DateUtils.SIMPLE_DATE_TIME_FORMAT_STR));
        //trade.setOuterBuyerAccount(""); 财付通没有这个字段，无法返回
        //trade.setOuterBuyerId("");

        // 第三方平台交易号
        trade.setOuterTradeNo(param.get("transaction_id"));

        trade.setGmtModifyTime(new DateTime());
        trade.setPayCurrency(param.get("fee_type"));
        trade.setPayRetTotalFee(param.get("total_fee"));

        //查询订单
        Map<String, String> payResult = AppTenpayUtils.searchPayResult(trade);
        if(payResult != null && "SUCCESS".equalsIgnoreCase(payResult.get("trade_state"))) {
            trade.setTradeStatus(TradeStatus.TRADE_SUCCESS.name());
        }
        this.callBackHandlerClass = BizType.valueOf(trade.getBizType()).getHandlerClass();
    }

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
}
