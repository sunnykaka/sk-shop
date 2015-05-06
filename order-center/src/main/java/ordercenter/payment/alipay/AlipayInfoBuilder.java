package ordercenter.payment.alipay;

import common.utils.DateUtils;
import common.utils.Money;
import ordercenter.payment.BackInfoBuilder;
import ordercenter.payment.constants.PayMethod;
import ordercenter.payment.constants.PayType;
import ordercenter.payment.models.TradeInfo;
import play.Logger;

import javax.servlet.http.HttpServletRequest;
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
     * 修改人：Json
     * 修改时间：2013.12.06，18.20
     * why：添加了业务方式和银行
     */
    public TradeInfo buildFromRequest(HttpServletRequest request) {
        String order_no = request.getParameter("out_trade_no");  //获取订单号
        TradeInfo tradeInfo = new TradeInfo();
        tradeInfo.setTradeNo(order_no);
        Money money = Money.valueOf(request.getParameter("total_fee"));
        tradeInfo.setPayTotalFee(money);
        tradeInfo.setGmtCreateTime(DateUtils.current());
        tradeInfo.setTradeStatus(request.getParameter("trade_status"));
        tradeInfo.setOuterBuyerAccount(request.getParameter("buyer_email"));
        tradeInfo.setOuterTradeNo(request.getParameter("trade_no"));
        tradeInfo.setOuterPlatformType(PayType.Alipay.getValue());
        String extra_common_param = request.getParameter("extra_common_param");//阿里的支付回传参数
        String[] split = extra_common_param.split("\\|");

        Logger.warn("支付宝回传的 extra_common_param 参数是: " + extra_common_param);

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

        String bank_seq_no = request.getParameter("bank_seq_no");
        tradeInfo.setPayMethod(bank_seq_no == null ? PayMethod.directPay.toString() : PayMethod.bankPay.toString());
        tradeInfo.setOuterBuyerId(request.getParameter("buyer_id"));
        tradeInfo.setNotifyId(request.getParameter("notify_id"));
        tradeInfo.setNotifyType(request.getParameter("notify_type"));
        return tradeInfo;
    }

    @Override
    public Map<String, String> buildParam(HttpServletRequest request) {
        //获取支付宝GET过来反馈信息
        Map<String, String> params = new HashMap<String, String>();
        Map requestParams = request.getParameterMap();
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
