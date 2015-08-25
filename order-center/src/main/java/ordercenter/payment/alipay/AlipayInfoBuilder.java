package ordercenter.payment.alipay;

import common.utils.Money;
import common.utils.ParamUtils;
import ordercenter.models.Trade;
import ordercenter.payment.BackInfoBuilder;
import ordercenter.payment.constants.PayChannel;
import ordercenter.payment.constants.PayMethod;
import org.joda.time.DateTime;
import play.Logger;
import play.mvc.Http.Request;

import java.util.HashMap;
import java.util.Map;

/**
 * 阿里支付 Builder
 * User: lidujun
 * Date: 2015-04-29
 */
public class AlipayInfoBuilder implements BackInfoBuilder {


    /**
     * 开启自己的日志
     */
    private final Logger.ALogger logger = Logger.of("alipay-trade");


    /**
     * 添加了业务方式和银行
     */
    @Override
    public Trade buildFromRequest(Request request) {
        String order_no = ParamUtils.getByKey(request, "out_trade_no");  //获取订单号
        Trade tradeInfo = new Trade();
        tradeInfo.setTradeNo(order_no);
        Money money =Money.valueOf(ParamUtils.getByKey(request, "total_fee"));
        tradeInfo.setPayTotalFee(money);
        tradeInfo.setGmtCreateTime(new DateTime());
        tradeInfo.setTradeStatus(ParamUtils.getByKey(request, "trade_status"));
        tradeInfo.setOuterBuyerAccount(ParamUtils.getByKey(request, "buyer_email"));
        tradeInfo.setOuterTradeNo(ParamUtils.getByKey(request, "trade_no"));
        tradeInfo.setOuterPlatformType(PayChannel.Alipay.getValue());
        String extra_common_param = ParamUtils.getByKey(request, "extra_common_param");//阿里的支付回传参数
        String[] split = extra_common_param.split("\\|");

        logger.warn("支付宝回传的 extra_common_param 参数是: " + extra_common_param);

        /**
         * 修改原因：添加一个业务类型（是订单还是优惠券）
         * 修改人：Json.zhu
         * 修改时间：2013.12.09
         */
        if (split.length > 1)
            tradeInfo.setBizType(split[1]);  //设置业务方式，是order还是coupon
        /**
         * 修改原因：request.getParameter("defaultbank")无法正确设置值，
         * 修改人：Json.zhu
         * 修改时间：2013.12.09
         */
        if (split.length > 2)
            tradeInfo.setDefaultbank(split[2]);
        //tradeInfo.setDefaultbank(request.getParameter("defaultbank"));//设置银行

        String bank_seq_no = ParamUtils.getByKey(request, "bank_seq_no");
        tradeInfo.setPayMethod(bank_seq_no == null ? PayMethod.directPay.toString() : PayMethod.bankPay.toString());
        tradeInfo.setOuterBuyerId(ParamUtils.getByKey(request, "buyer_id"));
        tradeInfo.setNotifyId(ParamUtils.getByKey(request, "notify_id"));
        tradeInfo.setNotifyType(ParamUtils.getByKey(request, "notify_type"));
        return tradeInfo;
    }

//    @Override
//    public Map<String, String> buildParam(Request request) {
//        //获取支付宝GET过来反馈信息
//        Map<String, String> params = new HashMap<String, String>();
//        Map<String, String[]> requestParams = request.queryString();
//        if (requestParams == null || requestParams.size() == 0) {
//            if ("POST".equalsIgnoreCase(request.method())) {
//                requestParams = request.body().asFormUrlEncoded();
//            }
//        }
//
//        if (requestParams != null) {
//            for (Object oName : requestParams.keySet()) {
//                String name = (String) oName;
//                String[] values = (String[]) requestParams.get(name);
//                String valueStr = "";
//                for (int i = 0; i < values.length; i++) {
//                    valueStr = (i == values.length - 1) ? valueStr + values[i]
//                            : valueStr + values[i] + ",";
//                }
//                params.put(name, valueStr);
//            }
//        }
//        return params;
//    }

    /**
     * 从request提取参数，变成Map方便做校验。
     * @param request
     * @return
     */
    public Map<String, String> buildParam(Request request) {
        //获取支付宝GET过来反馈信息
        Map<String, String> params = new HashMap<String, String>();
        Map requestParams = request.queryString();
        for (Object oName : requestParams.keySet()) {
            String name = (String) oName;
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        return params;
    }
}
