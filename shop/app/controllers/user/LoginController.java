package controllers.user;

import common.exceptions.AppBusinessException;
import common.utils.JsonResult;
import common.utils.RegExpUtils;
import usercenter.utils.SessionUtils;
import common.utils.play.PlayForm;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import usercenter.dtos.LoginForm;
import usercenter.dtos.RegisterForm;
import usercenter.models.User;
import usercenter.services.UserService;
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

        Form<RegisterForm> registerForm = PlayForm.form(RegisterForm.class).bindFromRequest();

        if(!registerForm.hasErrors()) {
            try {
                userService.register(registerForm.get(), request().remoteAddress());
                return ok(new JsonResult(true, null, routes.LoginController.loginPage().url()).toNode());
//                return redirect(controllers.routes.Application.myOrder());

            } catch (AppBusinessException e) {
                registerForm.reject("errors", e.getMessage());
            }
        }

        return ok(new JsonResult(false, registerForm.errorsAsJson().toString()).toNode());
//        flash("errors", registerForm.errorsAsJson().toString());
//        return redirect(controllers.user.routes.LoginController.registerPage());

    }

    public Result loginPage() {

        return ok(login.render());

    }

    public Result login() {

        Form<LoginForm> loginForm = PlayForm.form(LoginForm.class).bindFromRequest();

        if(!loginForm.hasErrors()) {
            try {
                User user = userService.login(loginForm.get());
                SessionUtils.setCurrentUser(user, "true".equalsIgnoreCase(loginForm.get().getRememberMe()));
                return ok(new JsonResult(true, null, controllers.routes.Application.myOrder().url()).toNode());
//                return redirect(controllers.routes.Application.myOrder());

            } catch (AppBusinessException e) {
                loginForm.reject("errors", e.getMessage());
            }
        }

        return ok(new JsonResult(false, loginForm.errorsAsJson().toString()).toNode());
//        flash("errors", loginForm.errorsAsJson().toString());
//        return redirect(controllers.user.routes.LoginController.loginPage());

    }


    public Result requestPhoneCode(String phone) {
        if(!RegExpUtils.isPhone(phone)) {
            return ok(new JsonResult(false, "手机号不能为空").toNode());
        }
        String code = userService.generatePhoneVerificationCode(phone);
        if(StringUtils.isBlank(code)) {
            return ok(new JsonResult(false, "验证码发送失败").toNode());
        } else {
            //TODO 发送短信,记录发送次数
            play.Logger.debug(String.format("手机%s验证码%s", phone, code));
            return ok(new JsonResult(true).toNode());
        }
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