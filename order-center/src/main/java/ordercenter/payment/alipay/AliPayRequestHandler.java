package ordercenter.payment.alipay;

import common.utils.Money;
import ordercenter.payment.PayInfoWrapper;
import ordercenter.payment.PayRequestHandler;
import play.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 *阿里支付请求处理类
 * User: lidujun
 * Date: 2015-04-28
 */
public class AliPayRequestHandler extends PayRequestHandler {

    @Override
    protected String getPaymentURL() {
        return  AlipayUtil.ALIPAY_GATEWAY_NEW;
    }

    @Override
    protected void doSign(Map<String, String> params) {
        //生成签名结果
        String mysign = AlipayUtil.buildMysign(params);
        //签名结果与签名方式加入请求提交参数组中
        params.put("sign", mysign);
        params.put("sign_type", AlipayUtil.sign_type);
    }

    @Override
    protected Map<String, String> buildPayParam(PayInfoWrapper payInfoWrapper) {
        if (!(payInfoWrapper.isAlipay() || payInfoWrapper.isWXSM() || payInfoWrapper.isBank())) {
            throw new RuntimeException("不支持的支付类型");
        }

        //设置支付参数
        Map<String, String> params = new HashMap<String, String>();
        params.put("service", "create_forex_trade");
        params.put("partner", AlipayUtil.partner);
        params.put("_input_charset", AlipayUtil.input_charset);
        params.put("sendFormat", "normal");

        //尚客系统中的交易号
        String out_trade_no = payInfoWrapper.getTradeNo();
        params.put("out_trade_no", out_trade_no);
        //订单名称，显示在支付宝收银台里的“商品名称”里，显示在支付宝的交易管理的“商品名称”的列表里。
        String subject = "尚客-购物编号-" + out_trade_no;
        params.put("subject", subject);
        params.put("body", subject);

        Money money = Money.valueOfCent(payInfoWrapper.getTotalFee());
        String rmbFee = money.toString();
        params.put("currency","USD");
        params.put("rmb_fee", rmbFee);
        Logger.info("------------我们系统中人民币-------: " + rmbFee);

        if(payInfoWrapper.isBank()) {
            params.put("specified_pay_channel", payInfoWrapper.getDefaultbank());
        }
        return params;
    }
}
