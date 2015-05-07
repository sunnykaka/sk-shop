package controllers.user;

import common.exceptions.AppBusinessException;
import common.utils.JsonResult;
import common.utils.RegExpUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import usercenter.domain.SmsSender;
import usercenter.dtos.LoginForm;
import usercenter.dtos.RegisterForm;
import usercenter.models.User;
import usercenter.services.UserService;
import usercenter.utils.SessionUtils;
import views.html.user.login;
import views.html.user.register;

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
                User user = userService.register(registerForm.get(), request().remoteAddress());
                userService.loginByRegister(user);
                String originalUrl = SessionUtils.getOriginalUrlOrDefault(controllers.routes.Application.myOrder().url());
                return ok(new JsonResult(true, null, originalUrl).toNode());

            } catch (AppBusinessException e) {
                registerForm.reject("errors", e.getMessage());
            }
        }

        return ok(new JsonResult(false, registerForm.errorsAsJson().toString()).toNode());

    }

    public Result loginPage() {

        return ok(login.render());

    }

    public Result login() {

        Form<LoginForm> loginForm = Form.form(LoginForm.class).bindFromRequest();

        if(!loginForm.hasErrors()) {
            try {
                userService.login(loginForm.get());
                String originalUrl = SessionUtils.getOriginalUrlOrDefault(controllers.routes.Application.myOrder().url());
                return ok(new JsonResult(true, null, originalUrl).toNode());

            } catch (AppBusinessException e) {
                loginForm.reject("errors", e.getMessage());
            }
        }

        return ok(new JsonResult(false, loginForm.errorsAsJson().toString()).toNode());

    }

    public Result logout() {

        User user = SessionUtils.currentUser();
        if(user != null) {
            userService.logout(user);
        }

        return redirect(routes.LoginController.loginPage());

    }



    public Result requestPhoneCode(String phone) {
        if(!RegExpUtils.isPhone(phone)) {
            return ok(new JsonResult(false, "手机号不能为空").toNode());
        }
        SmsSender smsSender = new SmsSender(phone, SmsSender.Usage.REGISTER);
        String code = smsSender.generatePhoneVerificationCode();
        if(!StringUtils.isBlank(code)) {
            if(smsSender.sendMessage(views.html.template.sms.userRegisterCode.render(code))) {
                return ok(new JsonResult(true).toNode());
            }
        }

        return ok(new JsonResult(false, "验证码发送失败").toNode());

    }

    public Result isPhoneExist(String phone) {
        boolean exists = userService.isPhoneExist(phone, Optional.empty());
        return ok(new JsonResult(exists, exists ? "手机已被使用" : "").toNode());
    }

    public Result isUsernameExist(String username) {
        boolean exists = userService.isUsernameExist(username, Optional.empty());
        return ok(new JsonResult(exists, exists ? "用户名已存在" : "").toNode());
    }



}