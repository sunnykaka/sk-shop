package controllers.user;

import com.google.common.base.Stopwatch;
import common.exceptions.AppBusinessException;
import common.exceptions.AppException;
import common.utils.FormUtils;
import common.utils.JsonResult;
import common.utils.RegExpUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import usercenter.constants.MarketChannel;
import usercenter.domain.QQLogin;
import usercenter.domain.SmsSender;
import usercenter.domain.WeiboLogin;
import usercenter.domain.WeixinLogin;
import usercenter.dtos.LoginForm;
import usercenter.dtos.RegisterForm;
import usercenter.models.User;
import usercenter.services.UserService;
import usercenter.utils.SessionUtils;
import views.html.user.login;
import views.html.user.register;
import views.html.user.openIDCallback;

import java.util.Optional;

@org.springframework.stereotype.Controller
public class LoginController extends Controller {

    @Autowired
    UserService userService;

    public Result registerPage() {

        return ok(register.render());

    }

    public Result register() {

        Form<RegisterForm> registerForm = Form.form(RegisterForm.class).bindFromRequest();

        if(!registerForm.hasErrors()) {
            try {
                registerForm.get().setChannel(MarketChannel.WEB.getValue());
                User user = userService.register(registerForm.get(), request().remoteAddress());
                userService.loginByRegister(user, true);
                String originalUrl = SessionUtils.getOriginalUrlOrDefault(controllers.routes.Application.index().url());
                return ok(new JsonResult(true, null, originalUrl).toNode());

            } catch (AppBusinessException e) {
                registerForm.reject("errors", e.getMessage());
            }
        }

        return ok(new JsonResult(false, FormUtils.showErrorInfo(registerForm.errors())).toNode());

    }

    public Result loginPage() {

        return ok(login.render());

    }

    public Result login() {

        Form<LoginForm> loginForm = Form.form(LoginForm.class).bindFromRequest();

        if(!loginForm.hasErrors()) {
            try {
                loginForm.get().setChannel(MarketChannel.WEB.getValue());
                userService.login(loginForm.get());
                String originalUrl = SessionUtils.getOriginalUrlOrDefault(controllers.routes.Application.index().url());
                return ok(new JsonResult(true, null, originalUrl).toNode());

            } catch (AppBusinessException e) {
                loginForm.reject("errors", e.getMessage());
            }
        }

        return ok(new JsonResult(false, FormUtils.showErrorInfo(loginForm.errors())).toNode());

    }

    public Result logout() {

        User user = SessionUtils.currentUser();
        if(user != null) {
            userService.logout(user);
        }

        return redirect(routes.LoginController.loginPage());

    }

    public Result requestPhoneCode(String phone) {

        Logger.debug(String.format("发送短信请求IP[%s], phone[%s]", request().remoteAddress(), phone));

        SmsSender smsSender = new SmsSender(phone, request().remoteAddress(), SmsSender.Usage.REGISTER);
        try {
            smsSender.sendPhoneVerificationMessage();
        } catch (AppException e) {
            return ok(new JsonResult(false, e.getMessage()).toNode());
        }

        return ok(new JsonResult(true).toNode());
    }

    public Result isPhoneExist(String phone) {
        boolean exists = userService.isPhoneExist(phone, Optional.empty());
        return ok(new JsonResult(exists, exists ? "手机已被使用" : "").toNode());
    }

    public Result isUsernameExist(String username) {
        boolean exists = userService.isUsernameExist(username, Optional.empty());
        return ok(new JsonResult(exists, exists ? "用户名已存在" : "").toNode());
    }

    public Result weixinLogin() {

        return redirect(new WeixinLogin().redirectToConnectUrl());
    }

    public Result weixinLoginCallback(String code, String state) {

        Logger.debug(String.format("微信回调参数: code[%s], state[%s]", code, state));
        Stopwatch stopwatch = Stopwatch.createStarted();

        try {
            User user = new WeixinLogin().handleCallback(code, state, request().remoteAddress());
            userService.loginByRegister(user, true);
            String originalUrl = SessionUtils.getOriginalUrlOrDefault(controllers.routes.Application.index().url());
            return redirect(originalUrl);

        } catch (AppBusinessException e) {
            Logger.error("第三方登录失败", e);
            return redirect(routes.LoginController.loginPage());

        } finally {
                stopwatch.stop();
                Logger.info("微信回调任务运行结束, 耗时: " + stopwatch.toString());
            }
        }

    public Result weiboLogin() {

        return redirect(new WeiboLogin().redirectToConnectUrl());
    }

    public Result weiboLoginCallback(String code, String state, String error, String error_code) {

        Logger.debug(String.format("微博回调参数: code[%s], state[%s], error[%s], error_code[%s]", code, state, error, error_code));

        try {
            User user = new WeiboLogin().handleCallback(code, state, error, error_code, request().remoteAddress());
            userService.loginByRegister(user, true);
            String originalUrl = SessionUtils.getOriginalUrlOrDefault(controllers.routes.Application.index().url());

            return redirect(originalUrl);
        } catch (AppBusinessException e) {
            return redirect(routes.LoginController.loginPage());
        }
    }

    public Result qqLogin() {

        return redirect(new QQLogin().redirectToConnectUrl());
    }

    public Result qqLoginCallback(String code, String state, String msg) {

        try{
            Logger.debug(String.format("QQ回调参数: code[%s], state[%s], msg[%s]", code, state, msg));

            User user = new QQLogin().handleCallback(code, state, msg, request().remoteAddress());
            userService.loginByRegister(user, true);
            String originalUrl = SessionUtils.getOriginalUrlOrDefault(controllers.routes.Application.index().url());
            return redirect(originalUrl);

        } catch (AppBusinessException e) {
            Logger.error("第三方登录失败", e);
            return redirect(routes.LoginController.loginPage());
        }

    }




}