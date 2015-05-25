package controllers.shop;

import ordercenter.payment.CallBackResult;
import ordercenter.payment.PayResponseHandler;
import ordercenter.payment.constants.ResponseType;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.shop.paySuccess;
import views.html.shop.payFail;

import java.util.Map;

/**
 * 支付通用控制器类
* User: lidujun
* Date: 2015-05-12
*/
@org.springframework.stereotype.Controller
public class OrderPayCallBackController extends Controller {
    /**
     * 支付正常返回
     * @return
     */
    public Result normalReturn() {
        log();
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
        log();
        PayResponseHandler handler = new PayResponseHandler(request());
        CallBackResult result = handler.handleCallback(ResponseType.NOTIFY);
        Map<String, Object> resultMap = result.getData();
        if(result.success()) {
            return ok(paySuccess.render(resultMap));
        } else {
            return ok(payFail.render(resultMap));
        }
    }

    private void log() {
        if (Logger.isInfoEnabled())
            Logger.info("支付平台返回的数据 : " + request().body().asFormUrlEncoded());
    }
}
