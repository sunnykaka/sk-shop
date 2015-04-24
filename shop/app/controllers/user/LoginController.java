package controllers.user;

import common.exceptions.AppBusinessException;
import common.utils.JsonResult;
import common.utils.play.PlayForm;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import play.cache.Cache;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import usercenter.dtos.RegisterForm;
import usercenter.services.UserService;
import views.html.index;

import java.util.Optional;

@org.springframework.stereotype.Controller
public class LoginController extends Controller {

    @Autowired
    UserService userService;

    public Result registerPage() {

        return ok(index.render());

    }

    public Result register() {

        Form<RegisterForm> registerForm = PlayForm.form(RegisterForm.class).bindFromRequest();

        if(!registerForm.hasErrors()) {
            try {
                userService.register(registerForm.get(), request().remoteAddress());
                return redirect(controllers.user.routes.LoginController.registerPage());

            } catch (AppBusinessException e) {
                registerForm.reject("errors", e.getMessage());
            }
        }

        System.out.println("error");
        flash("errors", registerForm.errorsAsJson().toString());
        return redirect(controllers.user.routes.LoginController.registerPage());

    }

    public Result loginPage() {

        return ok(index.render());

    }

    public Result login() {

        return redirect(controllers.routes.Application.index());

    }


    public Result requestPhoneCode(String phone) {
        String code = userService.generatePhoneVerificationCode(phone);
        if(StringUtils.isBlank(code)) {
            return ok(new JsonResult(false).toNode());
        } else {
            //TODO 发送短信
            return ok(new JsonResult(true).toNode());
        }
    }

    public Result isPhoneExist(String phone) {
        boolean exists = userService.isPhoneExist(phone, Optional.empty());
        return ok(new JsonResult(exists).toNode());
    }

    public Result isUsernameExist(String username) {
        boolean exists = userService.isUsernameExist(username, Optional.empty());
        return ok(new JsonResult(exists).toNode());
    }



}