package controllers.user;

import common.exceptions.AppException;
import common.utils.JsonResult;
import play.mvc.Controller;
import play.mvc.Result;
import usercenter.domain.SmsSender;

/**
 * 通用下发手机短信
 * <p>
 * Created by zhb on 15-5-6.
 */
@org.springframework.stereotype.Controller
public class SendSMSController extends Controller {

    public Result sendSMS(String phone) {

        SmsSender smsSender = new SmsSender(phone, SmsSender.Usage.BIND);
        try {
            smsSender.sendPhoneVerificationMessage();
        } catch (AppException e) {
            return ok(new JsonResult(false, e.getMessage()).toNode());
        }

        return ok(new JsonResult(true).toNode());
    }

}
