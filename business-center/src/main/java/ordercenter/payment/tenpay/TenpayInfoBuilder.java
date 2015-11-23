package ordercenter.payment.tenpay;


import ordercenter.models.Trade;
import play.mvc.Http;

/**
 * User: amos.zhou
 * Date: 13-10-24
 * Time: 上午10:51
 */
public class TenpayInfoBuilder  {

//    @Override
//    public Trade buildFromRequest(Http.Request request) {
//        String trade_no = request.getParameter("out_trade_no");  //获取订单号
//        TradeInfo tradeInfo = new TradeInfo();
//        tradeInfo.setTradeNo(trade_no);
//        tradeInfo.setPayTotalFee(Long.valueOf(request.getParameter("total_fee")));
//        tradeInfo.setGmtCreateTime(new Date());
//        tradeInfo.setTradeStatus(request.getParameter("trade_state"));
//        tradeInfo.setOuterBuyerAccount(request.getParameter("buyer_alias"));
//        tradeInfo.setOuterTradeNo(request.getParameter("transaction_id"));
//        tradeInfo.setOuterPlatformType(PayType.TenPay.getValue());
//        tradeInfo.setNotifyId(request.getParameter("notify_id"));
//        tradeInfo.setNotifyType("redirect");
//        return tradeInfo;
//    }
//
//    @Override
//    public Map<String, String> buildParam(HttpServletRequest request) {
//        Map<String, String> params = new TreeMap<String, String>();
//        Map requestParams = request.getParameterMap();
//        for (Object oName : requestParams.keySet()) {
//            String name = (String) oName;
//            String[] values = (String[]) requestParams.get(name);
//            if (values != null && values.length > 0)
//                params.put(name, values[0]);
//        }
//        return params;
//    }
}
