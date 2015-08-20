package ordercenter.payment.tenpay;

import common.utils.DateUtils;
import common.utils.Money;
import common.utils.ParamUtils;
import ordercenter.models.Trade;
import ordercenter.payment.BackInfoBuilder;
import ordercenter.payment.constants.PayChannel;
import play.mvc.Http.Request;

import java.util.Map;
import java.util.TreeMap;

/**
 * 财付通(tenpay) Builder
 * User: lidujun
 * Date: 2015-07-16
 */
public class TenpayInfoBuilder implements BackInfoBuilder {

    @Override
    public Trade buildFromRequest(Request request) {
        Trade tradeInfo = new Trade();
        tradeInfo.setOuterPlatformType(PayChannel.TenPay.getValue());

        //尚客系统交易号
        tradeInfo.setTradeNo(ParamUtils.getByKey(request, "out_trade_no"));
        //财付通系统交易号
        tradeInfo.setOuterTradeNo(ParamUtils.getByKey(request, "transaction_id"));

        //交易状态 0—成功
        tradeInfo.setTradeStatus(ParamUtils.getByKey(request, "trade_state"));

        tradeInfo.setPayCurrency(ParamUtils.getByKey(request, "fee_type"));
        tradeInfo.setPayRetTotalFee(ParamUtils.getByKey(request, "total_fee"));
        tradeInfo.setPayTotalFee(Money.valueOf(ParamUtils.getByKey(request, "total_fee")));
        tradeInfo.setNotifyId(ParamUtils.getByKey(request, "notify_id"));

        tradeInfo.setGmtCreateTime(DateUtils.current());
        tradeInfo.setTradeGmtCreateTime(DateUtils.current());

        tradeInfo.setNotifyType("redirect");
        return tradeInfo;
    }

    @Override
    public Map<String, String> buildParam(Request request) {
        Map<String, String> params = new TreeMap<String, String>();
        Map requestParams = request.queryString();
        if(requestParams == null || requestParams.size() == 0) {
            if("POST".equalsIgnoreCase(request.method())){
                requestParams = request.body().asFormUrlEncoded();
            }
        }

        for (Object oName : requestParams.keySet()) {
            String name = (String) oName;
            String[] values = (String[]) requestParams.get(name);
            if (values != null && values.length > 0)
                params.put(name, values[0]);
        }
        return params;
    }
}
