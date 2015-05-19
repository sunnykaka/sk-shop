package ordercenter.payment;

import org.springframework.util.Assert;
import play.Configuration;
import play.Logger;
import play.Play;

import java.util.Map;

 /**
 *阿里支付请求处理抽象类
 * User: lidujun
 * Date: 2015-04-29
 */
public abstract class PayRequestHandler {

     /**
     * 默认的reutrn url
     */
     protected static final String DEFAULT_RETURN_URL_KEY = "payment.returnUrl";

     /**
     *  默认的notify url
     */
     protected static final String DEFAULT_NOTIFY_URL_KEY = "payment.notifyUrl";

     protected static Configuration cfg = Play.application().configuration();

     /**
      * 跳转到支付界面格式字符串
      * @param payInfoWrapper
      * @return
      */
    public final String forwardToPay(PayInfoWrapper payInfoWrapper) {
        Map<String, String> params = buildPayParam(payInfoWrapper);
        //返回处理的Url
        String returnUrl = cfg.getString(DEFAULT_RETURN_URL_KEY);
        //notify处理的Url
        String notifyUrl = cfg.getString(DEFAULT_NOTIFY_URL_KEY);
        Assert.notNull(returnUrl, "必须要在common.conf中配置returnUrl");
        Assert.notNull(notifyUrl, "必须要在common.conf中配置notifyUrl");
        params.put("return_url", returnUrl);
        params.put("notify_url", notifyUrl);
        //签名，并追加签名参数
        doSign(params);
        return buildPaymentString(params);
    }

     /**
      * 构建付款格式字符串
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
      * @param payInfoWrapper
      * @return
      */
    protected abstract Map<String, String> buildPayParam(PayInfoWrapper payInfoWrapper);

     /**
      * 付款url地址
      * @return
      */
    protected abstract String getPaymentURL();

     /**
      * 登陆
      * @param params
      */
    protected abstract void doSign(Map<String, String> params);

}
