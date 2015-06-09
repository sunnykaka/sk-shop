package ordercenter.payment.alipay;

import common.utils.Money;
import ordercenter.payment.PayInfoWrapper;
import ordercenter.payment.PayRequestHandler;
import ordercenter.payment.constants.PayMethod;

import java.util.HashMap;
import java.util.Map;

/**
 *阿里支付请求处理类
 * User: lidujun
 * Date: 2015-04-28
 */
public class AliPayRequestHandler extends PayRequestHandler {

    public static final int PAYMENT_TYPE_BUY_PRODUCT = 1;    //商品购买

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
        //************订单参数***************//
        //请与贵网站订单系统中的唯一订单号匹配
        String out_trade_no = payInfoWrapper.getTradeNo();

        //订单名称，显示在支付宝收银台里的“商品名称”里，显示在支付宝的交易管理的“商品名称”的列表里。
        String subject = "尚客-购物编号-" + out_trade_no;

        //************/订单参数***************//

        //扩展功能参数——默认支付方式//

        //把请求参数打包成数组
        Map<String, String> sParaTemp = new HashMap<String, String>();
        sParaTemp.put("payment_type", String.valueOf(PAYMENT_TYPE_BUY_PRODUCT));
        sParaTemp.put("out_trade_no", out_trade_no);
        sParaTemp.put("subject", subject);
        sParaTemp.put("body", subject);

        sParaTemp.put("currency","USD");

        //完成分到圆的转换
        Money money = Money.valueOfCent(payInfoWrapper.getTotalFee());
        sParaTemp.put("total_fee", money.toString());
        sParaTemp.put("partner", AlipayUtil.partner);
        sParaTemp.put("seller_email", AlipayUtil.seller_email);
        sParaTemp.put("service", "create_forex_trade");
        sParaTemp.put("_input_charset", AlipayUtil.input_charset);
        //回调class
        sParaTemp.put("extra_common_param",payInfoWrapper.getCallBackClass()+"|"+payInfoWrapper.getBizType()+"|"+payInfoWrapper.getDefaultbank());

        if (payInfoWrapper.isAlipay()) {
            sParaTemp.put("paymethod", PayMethod.directPay.toString());
        } else if (payInfoWrapper.isBank()) {
            sParaTemp.put("defaultbank", payInfoWrapper.getDefaultbank());
            sParaTemp.put("paymethod", PayMethod.bankPay.toString());
        } else {
            throw new RuntimeException("不支持的支付类型");
        }

        return sParaTemp;
    }
}
