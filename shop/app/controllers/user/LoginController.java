package controllers.user;

import com.google.common.base.Stopwatch;
import common.exceptions.AppBusinessException;
import common.exceptions.AppException;
import common.utils.FormUtils;
import common.utils.JsonResult;
import ordercenter.services.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import usercenter.constants.AccountType;
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

import java.util.Arrays;
import java.util.Optional;

@org.springframework.stereotype.Controller
public class LoginController extends Controller {

    @Autowired
    UserService userService;

    @Autowired
    VoucherService voucherService;

    public Result registerPage() {

        return ok(register.render());

    }

    public Result register() {

        Form<RegisterForm> registerForm = Form.form(RegisterForm.class).bindFromRequest();

        if(!registerForm.hasErrors()) {
            try {
                registerForm.get().setChannel(MarketChannel.WEB.getValue());
                User user = userService.register(registerForm.get(), request().remoteAddress());
                try {
                    //发放注册代金券
                    voucherService.requestForRegister(user.getId(), 1);
                } catch (Exception e) {
                    Logger.error("用户注册的时候请求代金券失败", e);
                }
                userService.loginByRegister(user.getId(), true);
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

    public Result requestPhoneCode(String phone, String code) {

        if(!SmsSender.SECURITY_CODE.equals(code)) {
            //客戶端沒有传递简单的校验码，可能是恶意请求，直接返回true，迷惑对面
            return ok(new JsonResult(true).toNode());
        }

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

        return handleCallbackResults(AccountType.WeiXin, new String[]{code, state, request().remoteAddress()});
    }


    public Result weiboLogin() {

        return redirect(new WeiboLogin().redirectToConnectUrl());
    }

    public Result weiboLoginCallback(String code, String state, String error, String error_code) {

        Logger.debug(String.format("微博回调参数: code[%s], state[%s], error[%s], error_code[%s]", code, state, error, error_code));

        return handleCallbackResults(AccountType.Sina, new String[]{code, state, error, error_code, request().remoteAddress()});

    }

    public Result qqLogin() {

        return redirect(new QQLogin().redirectToConnectUrl());
    }

    public Result qqLoginCallback(String code, String state, String msg) {

        Logger.debug(String.format("QQ回调参数: code[%s], state[%s], msg[%s]", code, state, msg));

        return handleCallbackResults(AccountType.QQ, new String[]{code, state, msg, request().remoteAddress()});

    }

    /**
     * 处理第三方登录回调结果
     * @param accountType
     * @param args
     * @return
     */
    private Result handleCallbackResults(AccountType accountType, String[] args) {

        Stopwatch stopwatch = Stopwatch.createStarted();

        try {
            Object[] results;
            switch (accountType) {
                case WeiXin: {
                    results = new WeixinLogin().handleCallback(args[0], args[1], args[2]);
                    break;
                }
                case Sina: {
                    results = new WeiboLogin().handleCallback(args[0], args[1], args[2], args[3], args[4]);
                    break;
                }
                case QQ: {
                    results = new QQLogin().handleCallback(args[0], args[1], args[2], args[3]);
                    break;
                }
                default: {
                    Logger.error(String.format("未知的第三方登录回调结果: %s, 参数: %s", accountType, Arrays.toString(args)));
                    return redirect(routes.LoginController.loginPage());
                }
            }

            User user = (User)results[0];
            boolean isCreate = (Boolean)results[1];
            if(isCreate) {
                try {
                    //发放注册代金券
                    voucherService.requestForRegister(user.getId(), 1);
                } catch (Exception e) {
                    Logger.error("用户注册的时候请求代金券失败", e);
                }
            }
            userService.loginByRegister(user.getId(), true);
            String originalUrl = SessionUtils.getOriginalUrlOrDefault(controllers.routes.Application.index().url());
            return redirect(originalUrl);

        } catch (AppBusinessException e) {

            Logger.error("第三方登录失败", e);
            return redirect(routes.LoginController.loginPage());

        } finally {
            stopwatch.stop();
            Logger.debug(accountType.toString() + "回调任务运行结束, 耗时: " + stopwatch.toString());
        }


    }



}