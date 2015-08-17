package ordercenter.payment.alipay;

import common.utils.DateUtils;
import common.utils.ParamUtils;
import ordercenter.models.Trade;
import ordercenter.payment.BackInfoBuilder;
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
        tradeInfo.setPayRetTotalFee(ParamUtils.getByKey(request, "total_fee"));
        tradeInfo.setPayCurrency(ParamUtils.getByKey(request, "currency"));
        tradeInfo.setGmtCreateTime(DateUtils.current());
        tradeInfo.setTradeGmtCreateTime(DateUtils.current());
        return tradeInfo;
    }

    @Override
    public Map<String, String> buildParam(Request request) {
        //获取支付宝GET过来反馈信息
        Map<String, String> params = new HashMap<String, String>();
        Map<String, String[]> requestParams = request.queryString();
        if(requestParams == null || requestParams.size() == 0) {
            if("POST".equalsIgnoreCase(request.method())){
                requestParams = request.body().asFormUrlEncoded();
            }
        }

        if(requestParams != null) {
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
        }
        return params;
    }
}
