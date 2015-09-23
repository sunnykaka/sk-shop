package controllers.api.shop.payment.tenpay;


import common.utils.play.BaseGlobal;
import controllers.api.shop.payment.AppPayRequestHandler;
import ordercenter.models.Trade;
import ordercenter.services.TradeService;
import org.springframework.util.Assert;
import play.Logger;

import java.util.Map;
import java.util.TreeMap;

/**
 * 腾讯微信app支付，只实现了使用到的PayRequestHandler方法
 * User: lidujun
 * Date: 2015-09-07
 */
public class AppWeiXinPayRequestHandler extends AppPayRequestHandler {

    /**
     * 默认的notify url
     */
    private String DEFAULT_NOTIFY_URL_KEY = "payment.weiXinnotifyUrl";

    /**
     * 根据交易信息构建支付信息的map
     *
     * @param trade
     * @return
     */
    @Override
    public Map<String, String> buildPayInfo(Trade trade) {
        Map<String, String> payParams = buildPayParam(trade);
        //对所有参数追加签名
        doSign(payParams);

        //在发送支付请求之前创建交易记录，在支付返回结果后，再修改交易状态
        TradeService tradeService = BaseGlobal.ctx.getBean(TradeService.class);
        tradeService.createOrUpdateTrade(trade);

        String paramStr = TenAppWeiXinPayUtils.map2Xml(payParams); //xml字符

        String url = String.format(getPaymentURL());

        byte[] buf = TenPayHttpClientUtil.httpPost(url, paramStr);

        String content = new String(buf);
        Logger.info("预支付返回内容: " + content);

        Map<String,String> xmlMap= TenAppWeiXinPayUtils.xml2map(content);
        if(xmlMap != null &&  "SUCCESS".equalsIgnoreCase(xmlMap.get("return_code"))
                &&  "SUCCESS".equalsIgnoreCase(xmlMap.get("result_code"))) {
            //签名验证
            if(TenAppWeiXinPayUtils.verify(xmlMap)) {
                Map retMap = TenAppWeiXinPayUtils.buildAppSign(xmlMap);
                retMap.put("out_trade_no", trade.getTradeNo());
                return retMap;
            } else {
                return null;
            }
        }
        return null;
    }

    @Override
    protected String getPaymentURL() {
        return TenAppWeiXinPayUtils.PAY_GATEWAY;
    }

    @Override
    protected void doSign(Map<String, String> params) {
        //签名结果与签名方式加入请求提交参数组中
        params.put("sign", TenAppWeiXinPayUtils.buildSign(params));
    }

    @Override
    protected Map<String, String> buildPayParam(Trade trade) {
        Map<String, String> param = new TreeMap();
        //公众账号ID
        param.put("appid", TenAppWeiXinPayUtils.APPID);
        // 商户号
        param.put("mch_id", TenAppWeiXinPayUtils.MCH_ID);

        //商客交易号
        String out_trade_no = trade.getTradeNo();
        //订单名称，显示在支付宝收银台里的“商品名称”里，显示在支付宝的交易管理的“商品名称”的列表里。
        String subject = "sk-" + out_trade_no;
        //商品描述
        param.put("body", subject);

        //商户订单号
        param.put("out_trade_no", out_trade_no);

        //货币类型
        param.put("fee_type", "CNY");

        //订单总金额，显示在支付宝收银台里的“应付总额”里 ,以分为单位
        long total_fee = trade.getPayTotalFee().getCent();
        param.put("total_fee", String.valueOf(total_fee));

        //终端IP
        param.put("spbill_create_ip", trade.getClientIp());

        //交易类型
        param.put("trade_type", "APP");

        //随机字符串
        param.put("nonce_str", TenAppWeiXinPayUtils.genNonceStr());

        //notify处理的Url
        String notifyUrl = cfg.getString(DEFAULT_NOTIFY_URL_KEY);
        Assert.notNull(notifyUrl, "必须要在common.conf中配置notifyUrl");
        param.put("notify_url", notifyUrl);

        return param;
    }
}
