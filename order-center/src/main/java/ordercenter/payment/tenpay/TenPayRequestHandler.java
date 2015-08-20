package ordercenter.payment.tenpay;

import common.utils.DateUtils;
import ordercenter.models.Trade;
import ordercenter.payment.PayRequestHandler;
import ordercenter.payment.constants.PayChannel;

import java.util.Map;
import java.util.TreeMap;

/**
 * 财付通(tenpay) 请求处理类
 * User: lidujun
 * Date: 2015-04-29
 */
public class TenPayRequestHandler extends PayRequestHandler {

    @Override
    protected String getPaymentURL() {
        return TenpayUtils.PAY_GATEWAY;
    }

    @Override
    protected void doSign(Map<String, String> params) {
        //签名结果与签名方式加入请求提交参数组中
        params.put("sign", TenpayUtils.buildMysign(params));
    }

    @Override
    protected Map<String, String> buildPayParam(Trade trade) {
        //************订单参数***************//
        //请与贵网站订单系统中的唯一订单号匹配
        String out_trade_no = trade.getTradeNo();

        //订单名称，显示在收银台里的“商品名称”里，显示在支付宝的交易管理的“商品名称”的列表里。
        String subject = "尚客-" + out_trade_no;

        //订单总金额，显示在财付通收银台里的“应付总额”里 ,以分为单位
        long total_fee = trade.getPayTotalFee().getCent();

        Map<String, String> sParaTemp = new TreeMap<String, String>();
        sParaTemp.put("sign_type", TenpayUtils.SIGN_TYPE);
        sParaTemp.put("service_version", TenpayUtils.SERVICE_VERSION);
        sParaTemp.put("input_charset", TenpayUtils.INPUT_CHARSET);

        //sign
        sParaTemp.put("sign_key_index", "1");

        if(trade.getOuterPlatformType().equalsIgnoreCase(PayChannel.WXSM.toString())) {
            sParaTemp.put("bank_type", "WXSM");
        } else {
            sParaTemp.put("bank_type", "DEFAULT");
        }

        sParaTemp.put("body", subject);

        sParaTemp.put("partner", TenpayUtils.PARTNER);
        sParaTemp.put("out_trade_no", out_trade_no);
        sParaTemp.put("fee_type", "CNY");
        sParaTemp.put("total_fee", String.valueOf(total_fee));

//        sParaTemp.put("spbill_create_ip", payInfoWrapper.getBuyerIP());//TODO 暂时先屏蔽掉

        sParaTemp.put("time_start", DateUtils.printDateTime(DateUtils.current(), DateUtils.SIMPLE_DATE_TIME_FORMAT_STR));

        sParaTemp.put("transport_fee", "0");
        sParaTemp.put("product_fee", String.valueOf(total_fee));

        sParaTemp.put("trade_way", "1");

        return sParaTemp;
    }

}
