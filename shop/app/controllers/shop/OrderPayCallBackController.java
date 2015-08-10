package controllers.shop;

import common.utils.JsonResult;
import common.utils.play.BaseGlobal;
import ordercenter.models.Trade;
import ordercenter.payment.CallBackResult;
import ordercenter.payment.PayResponseHandler;
import ordercenter.payment.alipay.AlipayUtil;
import ordercenter.payment.constants.ResponseType;
import ordercenter.services.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import utils.secure.SecuredAction;
import views.html.shop.payFail;
import views.html.shop.paySuccess;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * 支付通用控制器类
* User: lidujun
* Date: 2015-05-12
*/
@org.springframework.stereotype.Controller
public class OrderPayCallBackController extends Controller {

    @Autowired
    TradeService tradeService;

    private void recordLog(String way) {
        Logger.info(way + "支付平台返回的数据 : \n");
        Map<String, String[]> queryMap = request().queryString();
        if(queryMap != null) {
            Set<String> keySet = queryMap.keySet();
            for(String key : keySet) {
                String[] valueArr = queryMap.get(key);
                String value = "";
                if(valueArr != null) {
                    StringBuilder sb = new StringBuilder();
                    for(String str : valueArr) {
                        if(sb.length() > 0) {
                            sb.append(",");
                        }
                        sb.append(str);
                    }
                    value = sb.toString();
                }
                Logger.info(key + " = " + value + "\n");
            }
        }
    }

    /**
     * 支付正常返回
     * @return
     */
    public Result normalReturn() {
        recordLog("normalReturn");
        PayResponseHandler handler = new PayResponseHandler(request());
        CallBackResult result = handler.handleCallback(ResponseType.RETURN);

        Map<String, Object> resultMap = result.getData();

        if(result.success()) {
            return ok(paySuccess.render(resultMap));
        } else {
            return ok(payFail.render(resultMap));
        }
    }

    /**
     * 支付有通知信息返回
     * @return
     */
    public Result notifyReturn() {
        recordLog("notifyReturn");
        PayResponseHandler handler = new PayResponseHandler(request());
        CallBackResult result = handler.handleCallback(ResponseType.NOTIFY);
        Map<String, Object> resultMap = result.getData();
        if(result.success()) {
            return ok(paySuccess.render(resultMap));
        } else {
            return ok(payFail.render(resultMap));
        }
    }

    /**
     * 检查支付情况
     * @return
     */
    @SecuredAction
    public Result checkPayState(String tradeNo) {
        Logger.info("订单交易号: " + tradeNo);
        try {
            if(tradeNo == null || tradeNo.trim().length() == 0) {
                return ok(new JsonResult(false,"用户订单交易号不存在！").toNode());
            }
            Trade traderade = tradeService.getTradeByTradeNo(tradeNo);
            if(traderade == null) {
                return ok(new JsonResult(false,"未支付成功。若已付款，请联系商城客服人员").toNode());
            } else {
                String state = traderade.getTradeStatus();
                //现在只有支付宝和财富通
                if("TRADE_FINISHED".equalsIgnoreCase(state) || "0".equalsIgnoreCase(state)) {
                    return ok(new JsonResult(true,"支付成功").toNode());
                } else {
                    return ok(new JsonResult(false,"未支付成功，请重新支付").toNode());
                }
            }
        } catch (Exception e) {
            Logger.error("-----------检查支付情况出现异常，其订单交易号：" + tradeNo, e);
            return ok(new JsonResult(false,"检查支付情况出现异常，请联系商城客服人员！").toNode());
        }
    }

    /**
     * 生成测试支付成功回调url
     * @return
     */
    @SecuredAction
    public Result generateAliPayPayReturnUrl(String tradeNo) {
        Logger.info("订单交易号: " + tradeNo);
        try {
            if(!(BaseGlobal.isDev() || BaseGlobal.isTest())) {
                return ok(new JsonResult(false,"此功能不可用！").toNode());
            }

            if(tradeNo == null || tradeNo.trim().length() == 0) {
                return ok(new JsonResult(false,"用户订单交易号不存在！").toNode());
            }

            String prefixStr = "http://修改ip地址:修改端口/payOrder/normalReturn?";
            Map<String,String> params = new HashMap<String,String>();
            params.put("trade_no", UUID.randomUUID().toString());
            params.put("total_fee", "0.02");
            params.put("out_trade_no", tradeNo);
            params.put("trade_status", "TRADE_FINISHED");
            params.put("currency", "USD");

            String mysign = AlipayUtil.buildMysign(params);
            System.out.println("------------------------: " + mysign);

            StringBuilder retSb = new StringBuilder();
            retSb.append(prefixStr);
            retSb.append("sign=").append(mysign);
            retSb.append("&");
            retSb.append("trade_no=").append(params.get("trade_no"));
            retSb.append("&");
            retSb.append("total_fee=").append(params.get("total_fee"));
            retSb.append("&");
            retSb.append("sign_type=").append("MD5");
            retSb.append("&");
            retSb.append("out_trade_no=").append(params.get("out_trade_no"));
            retSb.append("&");
            retSb.append("trade_status=").append(params.get("trade_status"));
            retSb.append("&");
            retSb.append("currency=").append(params.get("currency"));

            return ok(new JsonResult(true,"支付宝测试回调地址：" + retSb.toString()).toNode());
        } catch (Exception e) {
            Logger.error("-----------产生支付宝测试回调地址出现异常，其订单交易号：" + tradeNo, e);
            return ok(new JsonResult(false,"产生支付宝测试回调地址出现异常，请重试！").toNode());
        }
    }
}
