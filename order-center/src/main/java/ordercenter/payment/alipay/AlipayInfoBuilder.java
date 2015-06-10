package ordercenter.payment.alipay;

import common.utils.DateUtils;
import common.utils.Money;
import common.utils.ParamUtils;
import ordercenter.models.Trade;
import ordercenter.payment.BackInfoBuilder;
import ordercenter.payment.constants.PayMethod;
import ordercenter.payment.constants.PayType;
import play.mvc.Http.Request;

import java.util.HashMap;
import java.util.Map;

/**
 * 阿里支付 Builder
 * User: lidujun
 * Date: 2015-04-29
 */
public class AlipayInfoBuilder implements BackInfoBuilder {

    @Override
    /**
     * 添加了业务方式和银行
     */
    public Trade buildFromRequest(Request request) {
        Trade tradeInfo = new Trade();
        tradeInfo.setOuterPlatformType(PayType.Alipay.getValue());
        //尚客系统交易号
        tradeInfo.setTradeNo(ParamUtils.getByKey(request, "out_trade_no"));
        //支付宝系统交易号
        tradeInfo.setOuterTradeNo(ParamUtils.getByKey(request, "trade_no"));
        //交易状态
        tradeInfo.setTradeStatus(ParamUtils.getByKey(request, "trade_status"));



        //下面的参数现在有点问题
        Money money = Money.valueOf(ParamUtils.getByKey(request, "total_fee"));
        tradeInfo.setPayTotalFee(money);
        tradeInfo.setGmtCreateTime(DateUtils.current());

        tradeInfo.setOuterBuyerAccount(ParamUtils.getByKey(request, "buyer_email"));


//        String extra_common_param = ParamUtils.getByKey(request, "extra_common_param");//阿里的支付回传参数
//        String[] split = extra_common_param.split("\\|");
//
//        Logger.warn("支付宝回传的 extra_common_param 参数是: " + extra_common_param);
//
//        /**
//         * 修改原因：添加一个业务类型（是订单还是优惠券）
//         */
//        if (split.length > 1)
//            tradeInfo.setBizType(split[1]);  //设置业务方式，是order还是coupon
//        /**
//         * 设置银行，修改原因：request.getParameter("defaultbank")无法正确设置值，
//         */
//        if (split.length > 2)
//            tradeInfo.setDefaultbank(split[2]);


        String bank_seq_no = ParamUtils.getByKey(request, "bank_seq_no");
        tradeInfo.setPayMethod(bank_seq_no == null ? PayMethod.directPay.toString() : PayMethod.bankPay.toString());
        tradeInfo.setOuterBuyerId(ParamUtils.getByKey(request, "buyer_id"));
        tradeInfo.setNotifyId(ParamUtils.getByKey(request, "notify_id"));
        tradeInfo.setNotifyType(ParamUtils.getByKey(request,"notify_type"));



        return tradeInfo;
    }

    @Override
    public Map<String, String> buildParam(Request request) {
        //获取支付宝GET过来反馈信息
        Map<String, String> params = new HashMap<String, String>();
//        Map requestParams = request.body().asFormUrlEncoded();
//        for (Object oName : requestParams.keySet()) {
//            String name = (String) oName;
//            String[] values = (String[]) requestParams.get(name);
//            String valueStr = "";
//            for (int i = 0; i < values.length; i++) {
//                valueStr = (i == values.length - 1) ? valueStr + values[i]
//                        : valueStr + values[i] + ",";
//            }
//            params.put(name, valueStr);
//        } //ldj
        return params;
    }
}
