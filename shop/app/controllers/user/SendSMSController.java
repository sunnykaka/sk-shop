package controllers.user;

import common.exceptions.AppException;
import common.utils.JsonResult;
import common.utils.RegExpUtils;
import javapns.devices.Device;
import javapns.devices.implementations.basic.BasicDevice;
import javapns.notification.AppleNotificationServerBasicImpl;
import javapns.notification.PushNotificationManager;
import javapns.notification.PushNotificationPayload;
import javapns.notification.PushedNotification;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import play.mvc.Controller;
import play.mvc.Result;
import services.IOSPushService;
import usercenter.domain.SmsSender;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用下发手机短信
 * <p>
 * Created by zhb on 15-5-6.
 */
@org.springframework.stereotype.Controller
public class SendSMSController extends Controller {

    @Autowired
    IOSPushService iosPushService;

    public Result sendSMS(String phone) {

        SmsSender smsSender = new SmsSender(phone, SmsSender.Usage.BIND);
        try {
            smsSender.sendPhoneVerificationMessage();
        } catch (AppException e) {
            return ok(new JsonResult(false, e.getMessage()).toNode());
        }

        return ok(new JsonResult(true).toNode());
    }

    public Result IOSPush() {

        iosPushService.push();

        return ok();
    }

}
