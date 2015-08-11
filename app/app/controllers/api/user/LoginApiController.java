package controllers.api.user;

import api.response.user.LoginResult;
import api.response.user.RefreshTokenResult;
import common.exceptions.AppBusinessException;
import common.exceptions.AppException;
import common.exceptions.ErrorCode;
import common.utils.JsonUtils;
import common.utils.ParamUtils;
import controllers.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import play.data.Form;
import play.mvc.Result;
import services.api.user.UserApiService;
import services.api.user.UserTokenProvider;
import usercenter.domain.SmsSender;
import usercenter.dtos.LoginForm;
import usercenter.dtos.RegisterForm;
import usercenter.services.UserService;

import java.util.Optional;

@org.springframework.stereotype.Controller
public class LoginApiController extends BaseController {

    @Autowired
    UserApiService userApiService;

    @Autowired
    UserService userService;

    @Autowired
    UserTokenProvider userTokenProvider;



    public Result login() {

        Form<LoginForm> loginForm = bindAndCheckForm(LoginForm.class);

        LoginResult result = userApiService.login(loginForm.get());
        return ok(JsonUtils.object2Node(result));

    }

    public Result register() {

        Form<RegisterForm> registerForm = bindAndCheckForm(RegisterForm.class);

        LoginResult result = userApiService.register(registerForm.get(), request().remoteAddress());

        return created(JsonUtils.object2Node(result));
    }

    public Result logout() {
        String accessToken = ParamUtils.getByKey(request(), "accessToken");

        userApiService.logout(accessToken);

        return noContent();
    }

    public Result requestPhoneCode() throws AppException {
        String phone = ParamUtils.getByKey(request(), "phone");
        SmsSender smsSender = new SmsSender(phone, SmsSender.Usage.REGISTER);

        smsSender.sendPhoneVerificationMessage();

        return noContent();
    }

    /**
     * 判断用户名或手机是否为存在
     * @param username
     * @param phone
     * @return
     */
    public Result isUserExist(String username, String phone) {
        if(StringUtils.isBlank(username) && StringUtils.isBlank(phone)) {
            throw new AppBusinessException(ErrorCode.InvalidArgument, "用户名和手机不能同时为空");
        }
        boolean exists = false;
        if(StringUtils.isNotBlank(username)) {
            exists = userService.isUsernameExist(username, Optional.empty());
        }
        if(!exists && StringUtils.isNotBlank(phone)) {
            exists = userService.isPhoneExist(phone, Optional.empty());
        }
        return exists ? conflict() : noContent();
    }

    /**
     * 刷新accessToken有效期
     * @return
     */
    public Result refreshToken() {

        String refreshToken = ParamUtils.getByKey(request(), "refreshToken");

        Optional<RefreshTokenResult> refreshTokenResult = userTokenProvider.refreshToken(refreshToken);
        if(!refreshTokenResult.isPresent()) {
            throw new AppBusinessException(ErrorCode.InvalidRefreshToken);
        }

        return ok(JsonUtils.object2Node(refreshTokenResult.get()));
    }


}