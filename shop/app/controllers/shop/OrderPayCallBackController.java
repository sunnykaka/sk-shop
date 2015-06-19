package controllers.shop;

import common.utils.JsonResult;
import ordercenter.models.Trade;
import ordercenter.payment.CallBackResult;
import ordercenter.payment.PayResponseHandler;
import ordercenter.payment.constants.ResponseType;
import ordercenter.services.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import utils.secure.SecuredAction;
import views.html.shop.payFail;
import views.html.shop.paySuccess;

import java.util.Map;

/**
 * 支付通用控制器类
* User: lidujun
* Date: 2015-05-12
*/
@org.springframework.stereotype.Controller
public class OrderPayCallBackController extends Controller {

    @Autowired
    TradeService tradeService;

    /**
     * 支付正常返回
     * @return
     */
    @SecuredAction
    public Result normalReturn() {
        Logger.info("支付平台返回的数据 : " + request().queryString());
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
    @SecuredAction
    public Result notifyReturn() {
        Logger.info("支付平台返回的数据 : " + request().queryString());
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
                return ok(new JsonResult(false,"未支付成功。若已付款，可能是银行反应延迟了，请重新检测或联系商城客服").toNode());
            } else {
                String state = traderade.getTradeStatus();
                //现在只有支付宝
                if("TRADE_FINISHED".equalsIgnoreCase(state)) {
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
}
