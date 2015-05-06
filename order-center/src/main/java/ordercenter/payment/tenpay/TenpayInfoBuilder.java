package ordercenter.payment.tenpay;

import common.utils.Money;
import ordercenter.payment.BackInfoBuilder;
import ordercenter.payment.constants.PayType;
import ordercenter.payment.models.TradeInfo;
import org.joda.time.DateTime;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.TreeMap;

/**
 * 财付通(tenpay) Builder
 * User: lidujun
 * Date: 2015-04-29
 */
public class TenpayInfoBuilder implements BackInfoBuilder {

    @Override
    public TradeInfo buildFromRequest(HttpServletRequest request) {
        String trade_no = request.getParameter("out_trade_no");  //获取订单号
        TradeInfo tradeInfo = new TradeInfo();
        tradeInfo.setTradeNo(trade_no);
        tradeInfo.setPayTotalFee(Money.valueOf(request.getParameter("total_fee")));
        tradeInfo.setGmtCreateTime(new DateTime());
        tradeInfo.setTradeStatus(request.getParameter("trade_state"));
        tradeInfo.setOuterBuyerAccount(request.getParameter("buyer_alias"));
        tradeInfo.setOuterTradeNo(request.getParameter("transaction_id"));
        tradeInfo.setOuterPlatformType(PayType.TenPay.getValue());
        tradeInfo.setNotifyId(request.getParameter("notify_id"));
        tradeInfo.setNotifyType("redirect");
        return tradeInfo;
    }

    @Override
    public Map<String, String> buildParam(HttpServletRequest request) {
        Map<String, String> params = new TreeMap<String, String>();
        Map requestParams = request.getParameterMap();
        for (Object oName : requestParams.keySet()) {
            String name = (String) oName;
            String[] values = (String[]) requestParams.get(name);
            if (values != null && values.length > 0)
                params.put(name, values[0]);
        }
        return params;
    }
}
