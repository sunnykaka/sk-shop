package controllers.api.shop.payment.tenpay;

import common.utils.DateUtils;
import controllers.api.shop.payment.AppPayResponseHandler;
import ordercenter.constants.BizType;
import ordercenter.models.Trade;
import ordercenter.payment.PayCallback;
import ordercenter.payment.constants.TradeStatus;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.util.Map;

/**
 * App支付成功处理类(此处现在只考虑了微信支付)
 * User: lidujun
 * Date: 2015-09-11
 */
public class AppWeiXinPayResponseHandler extends AppPayResponseHandler {

    /**
     * 其它方式初始化数据
     * @param param
     */
    public AppWeiXinPayResponseHandler(Map<String, String> param) {
        //先从回调参数中取出交易号，并查出对应出交易记录
        String tradeNo = param.get("out_trade_no");
        if (StringUtils.isBlank(tradeNo)) {
            throw new IllegalArgumentException("交易不存在");
        }
        Trade trade = tradeService.getTradeByTradeNo(tradeNo);
        if (trade == null) {
            throw new IllegalArgumentException("交易不存在");
        }
        TradeStatus statusBeforeBack = TradeStatus.valueOf(trade.getTradeStatus());

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
        Map<String, String> payResult = TenAppWeiXinPayUtils.searchPayResult(trade);
        if(payResult != null && "SUCCESS".equalsIgnoreCase(payResult.get("trade_state"))) {
            trade.setTradeStatus(TradeStatus.TRADE_SUCCESS.name());
        }
        Class<PayCallback> callBackHandlerClass = BizType.valueOf(trade.getBizType()).getHandlerClass();

        this.setTrade(trade);
        this.setStatusBeforeBack(statusBeforeBack);
        this.setCallBackHandlerClass(callBackHandlerClass);
    }

}
