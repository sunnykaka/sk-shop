package ordercenter.payment.tenpay;

import common.utils.Money;
import common.utils.ParamUtils;
import ordercenter.models.Trade;
import ordercenter.payment.BackInfoBuilder;
import ordercenter.payment.constants.PayType;
import org.joda.time.DateTime;
import play.mvc.Http.Request;

import java.util.Map;
import java.util.TreeMap;

/**
 * 财付通(tenpay) Builder
 * User: lidujun
 * Date: 2015-04-29
 */
public class TenpayInfoBuilder implements BackInfoBuilder {

    @Override
    public Trade buildFromRequest(Request request) {


        Trade tradeInfo = new Trade();
        tradeInfo.setOuterTradeNo(ParamUtils.getByKey(request, "transaction_id"));

        String trade_no = ParamUtils.getByKey(request,"out_trade_no");  //获取订单号
        tradeInfo.setTradeNo(trade_no);
        tradeInfo.setGmtCreateTime(new DateTime());

        //0-success
        tradeInfo.setTradeStatus(ParamUtils.getByKey(request, "trade_state"));
        tradeInfo.setPayCurrency(ParamUtils.getByKey(request, "fee_type"));
        tradeInfo.setPayRetTotalFee(ParamUtils.getByKey(request, "total_fee"));
        tradeInfo.setPayTotalFee(Money.valueOf(ParamUtils.getByKey(request, "rmb_total_fee"))); //
        tradeInfo.setNotifyId(ParamUtils.getByKey(request, "notify_id"));










        tradeInfo.setOuterBuyerAccount(ParamUtils.getByKey(request, "buyer_alias"));

        tradeInfo.setOuterPlatformType(PayType.TenPay.getValue());

        tradeInfo.setNotifyType("redirect");
        return tradeInfo;
    }

    @Override
    public Map<String, String> buildParam(Request request) {
        Map<String, String> params = new TreeMap<String, String>();
        Map requestParams = request.body().asFormUrlEncoded();
        for (Object oName : requestParams.keySet()) {
            String name = (String) oName;
            String[] values = (String[]) requestParams.get(name);
            if (values != null && values.length > 0)
                params.put(name, values[0]);
        }
        return params;
    }
}
