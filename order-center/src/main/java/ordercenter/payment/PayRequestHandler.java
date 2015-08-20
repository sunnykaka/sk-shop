package ordercenter.payment;

import common.utils.play.BaseGlobal;
import ordercenter.models.Trade;
import ordercenter.services.TradeService;
import org.springframework.util.Assert;
import play.Configuration;
import play.Logger;
import play.Play;

import java.util.Map;

/**
 * 支付请求处理抽象类
 * User: lidujun
 * Date: 2015-04-29
 */
public abstract class PayRequestHandler {
    /**
     * 读取play配置文件
     */
    protected static Configuration cfg = Play.application().configuration();

    /**
     * 默认的reutrn url
     */
    protected static final String DEFAULT_RETURN_URL_KEY = "payment.returnUrl";

    /**
     * 默认的notify url
     */
    protected static final String DEFAULT_NOTIFY_URL_KEY = "payment.notifyUrl";

    /**
     * 跳转到支付界面格式字符串
     *
     * @param trade
     * @return
     */
    public final String forwardToPay(Trade trade) {

        /***
         * 构建支付请求参数，分2部分完成：
         * 1.各支付渠道（支付宝，财付通）
         * 2.return和notify的回调地址
         */
        Map<String, String> payParams = buildPayParam(trade);
        //返回处理的Url
        String returnUrl = cfg.getString(DEFAULT_RETURN_URL_KEY);
        Assert.notNull(returnUrl, "必须要在common.conf中配置returnUrl");
        //notify处理的Url
        String notifyUrl = cfg.getString(DEFAULT_NOTIFY_URL_KEY);
        Assert.notNull(notifyUrl, "必须要在common.conf中配置notifyUrl");
        payParams.put("return_url", returnUrl);
        payParams.put("notify_url", notifyUrl);

        /**
         * 对所有参数追加签名
         */
        doSign(payParams);

        /**
         * 在发送支付请求之前创建交易记录，在支付返回结果后，再修改交易状态
         */
        TradeService tradeService = BaseGlobal.ctx.getBean(TradeService.class);
        tradeService.createOrUpdateTrade(trade);

        return buildPaymentString(payParams);
    }


    /**
     * 构建付款格式form表单字符串
     *
     * @param params
     * @return
     */
    protected String buildPaymentString(Map<String, String> params) {
        StringBuilder sbHtml = new StringBuilder();
        sbHtml.append("<form id=\"payForm\" name=\"payForm\" action=\"" + getPaymentURL() + "\" "
                + "method=\"get\">");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sbHtml.append("<input type=\"hidden\" name=\"" + entry.getKey() + "\" value=\"" + entry.getValue() + "\"/>");
        }
        sbHtml.append("<input type=\"submit\" value=\"" + "确认" + "\" style=\"display:none;\"></form>");
        sbHtml.append("<script>document.forms['payForm'].submit();</script>");
        if (Logger.isInfoEnabled())
            Logger.info("向 (" + getPaymentURL() + ") 发送请求 : " + params);
        return sbHtml.toString();
    }

    /**
     * 构建付款参数
     *
     * @param trade
     * @return
     */
    protected abstract Map<String, String> buildPayParam(Trade trade);

    /**
     * 付款url地址
     *
     * @return
     */
    protected abstract String getPaymentURL();

    /**
     * 登陆
     *
     * @param params
     */
    protected abstract void doSign(Map<String, String> params);

}
