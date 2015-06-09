package ordercenter.payment.alipay;

import common.utils.Money;
import ordercenter.payment.PayInfoWrapper;
import ordercenter.payment.PayRequestHandler;
import ordercenter.payment.constants.PayMethod;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 *阿里支付请求处理类
 * User: lidujun
 * Date: 2015-04-28
 */
public class AliPayRequestHandler extends PayRequestHandler {

    /**
     * 汇率文件保存位置
     */
    private final String RATES_FILES_KEY = "payment.ratesFile";

    /**
     *  支付类型为：商品购买
     */
    private final int PAYMENT_TYPE_BUY_PRODUCT = 1;

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
        String ratesFilePath = cfg.getString(RATES_FILES_KEY);
        Assert.notNull(ratesFilePath, "必须要在common.conf中配置ratesFile");

        //下载汇率
        try {
            AlipayUtil.downloadRates(ratesFilePath);
        } catch (Exception e) {
            throw new RuntimeException("下载汇率文件出错");
        }

        //读取当前人民币对应美元汇率值
        double rate = 0;
        try {
            rate = AlipayUtil.parseRates(ratesFilePath, "USD");
        } catch (Exception e) {
            throw new RuntimeException("读取当前人民币对应美元汇率值出错");
        }

        if(rate == 0) {
            throw new RuntimeException("读取当前人民币对应美元汇率值出错，汇率不可能为0");
        }

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

        //完成分到元的转换
        Money money = Money.valueOfCent(payInfoWrapper.getTotalFee());
        String yuan = money.toString();
        System.out.println("-----我们系统中人民币-------: " + yuan);
        BigDecimal cnyB = BigDecimal.valueOf(Double.parseDouble(yuan));
        BigDecimal usdB = cnyB.divide(new BigDecimal(rate),2, BigDecimal.ROUND_HALF_UP);

        sParaTemp.put("total_fee", usdB.toString());
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
