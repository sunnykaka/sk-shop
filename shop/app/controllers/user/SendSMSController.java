package controllers.user;

import common.utils.JsonResult;
import common.utils.RegExpUtils;
import org.apache.commons.lang3.StringUtils;
import play.mvc.Controller;
import play.mvc.Result;
import usercenter.domain.SmsSender;

/**
 * 通用下发手机短信
 *
 * Created by zhb on 15-5-6.
 */
@org.springframework.stereotype.Controller
public class SendSMSController extends Controller {

    public Result sendSMS(String phone) {
        if(!RegExpUtils.isPhone(phone)) {
            return ok(new JsonResult(false, "手机号不能为空").toNode());
        }
        SmsSender smsSender = new SmsSender(phone, SmsSender.Usage.BIND);
        String code = smsSender.generatePhoneVerificationCode();
        if(!StringUtils.isBlank(code)) {
            if(smsSender.sendMessage(views.html.template.sms.userBindCode.render(code))) {
                play.Logger.debug(String.format("手机%s验证码%s", phone, code));
                return ok(new JsonResult(true).toNode());
            }
        }

        return ok(new JsonResult(false, "验证码发送失败").toNode());

    }

}
