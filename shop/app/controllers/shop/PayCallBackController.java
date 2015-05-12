package controllers.shop;

import ordercenter.payment.CallBackResult;
import ordercenter.payment.PayResponseHandler;
import ordercenter.payment.constants.ResponseType;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * 支付通用控制器类
* User: lidujun
* Date: 2015-05-08
*/
@org.springframework.stereotype.Controller
public class PayCallBackController extends Controller {


    //@RequestMapping(value = "/trade/return")
    //@RenderHeaderFooter
    public Result normalReturn() {
        log();
        PayResponseHandler handler = new PayResponseHandler(request());
        CallBackResult result = handler.handleCallback(ResponseType.RETURN);
        //model.addAllAttributes(result.getData());

        String flagStr = result.skipToNextProcess();
        if(flagStr.equalsIgnoreCase(OrderPayCallback.PAY_SUCCESS)) {

        }if(flagStr.equalsIgnoreCase(OrderPayCallback.PAY_FAIL)) {

        }
        return null;
    }

    private void log() {
        if (Logger.isInfoEnabled())
            Logger.info("支付平台返回的数据 : " + request().body().asFormUrlEncoded());
    }

//    //@RequestMapping(value = "/trade/notify")
//    public Result notify() throws IOException {
//        log();
//
//        PayResponseHandler handler = new PayResponseHandler(request());
//        CallBackResult result = handler.handleCallback(ResponseType.NOTIFY);
//
////        if(result.success()) {
////            return ok(new JsonResult(false,"支付成功！").toNode());
////        } else {
////            return ok(new JsonResult(false,"支付失败！").toNode());
////        }
//    }
}
