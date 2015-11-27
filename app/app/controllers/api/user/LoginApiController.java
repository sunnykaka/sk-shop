package controllers.api.user;

import api.response.user.LoginResult;
import api.response.user.RefreshTokenResult;
import common.exceptions.AppBusinessException;
import common.exceptions.AppException;
import common.exceptions.ErrorCode;
import common.utils.FormUtils;
import common.utils.JsonUtils;
import common.utils.ParamUtils;
import common.utils.RegExpUtils;
import controllers.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import play.Logger;
import play.data.Form;
import play.mvc.Http;
import play.mvc.Result;
import services.api.user.UserApiService;
import services.api.user.UserTokenProvider;
import usercenter.cache.RecoverCache;
import usercenter.domain.SmsSender;
import usercenter.dtos.*;
import usercenter.models.User;
import usercenter.services.UserService;

import java.util.Optional;
import java.util.regex.Pattern;

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
        SmsSender smsSender = new SmsSender(phone, request().remoteAddress(), SmsSender.Usage.REGISTER);

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

    /**
     * 检查手机号码，并下发短信
     *
     * @return
     * @throws AppException
     */
    public Result recoverCheckPhone() throws AppException {

        String phone = getStringPhone(request());

        User user = userService.findByPhone(phone);

        if(null == user){
            throw new AppBusinessException(ErrorCode.UsernameExist,"用户不存在");
        }

        SmsSender smsSender = new SmsSender(phone, request().remoteAddress(), SmsSender.Usage.REGISTER);

        Logger.info("send message in recoverCheckPhone");

        smsSender.sendPhoneVerificationMessage();

        return noContent();

    }

    /**
     * 验证短信
     *
     * @return
     */
    public Result recoverCheckSMS(){

        Form<PhoneCodeForm> phoneCodeForm = Form.form(PhoneCodeForm.class).bindFromRequest();
        if(!phoneCodeForm.hasErrors()) {
            try {
                PhoneCodeForm phoneCode = phoneCodeForm.get();

                if(!new SmsSender(phoneCode.getPhone(), request().remoteAddress(), SmsSender.Usage.REGISTER).verifyCode(phoneCode.getVerificationCode())) {
                    throw new AppBusinessException(ErrorCode.VerifyCodeError);
                }

                RecoverCache.setToken(RecoverCache.SECURITY_TOKEN_PHONE_KEY,phoneCode.getPhone(),phoneCode.getPhone());
                return noContent();

            } catch (AppBusinessException e) {
                phoneCodeForm.reject("errors", e.getMessage());
            }
        }

        throw new AppBusinessException(ErrorCode.InvalidArgument, FormUtils.showErrorInfo(phoneCodeForm.errors()));

    }

    /**
     * 执行修改密码
     *
     * @return
     */
    public Result recoverPswDo(){
        Form<RecoverPswForm> pswForm = Form.form(RecoverPswForm.class).bindFromRequest();
        if(!pswForm.hasErrors()) {
            try {
                RecoverPswForm psw = pswForm.get();

                String phone = RecoverCache.getToken(RecoverCache.SECURITY_TOKEN_PHONE_KEY,psw.getPhone());
                if(StringUtils.isEmpty(psw.getPhone())){
                    throw new AppBusinessException(ErrorCode.BadRequest,"校验用户失败，请重新走流程");
                }
                if(!psw.getPhone().equals(phone)){
                    throw new AppBusinessException(ErrorCode.BadRequest,"校验用户失败，请重新走流程");
                }

                User user = userService.findByPhone(psw.getPhone());
                if(null == user){
                    throw new AppBusinessException(ErrorCode.BadRequest,"校验用户失败，请重新走流程");
                }

                userService.updatePassword(user, psw);

                return noContent();

            } catch (AppBusinessException e) {
                pswForm.reject("errors", e.getMessage());
            }
        }

        throw new AppBusinessException(ErrorCode.InvalidArgument, FormUtils.showErrorInfo(pswForm.errors()));
    }

    private String getStringPhone(Http.Request request){

        String phone = ParamUtils.getByKey(request, "phone");

        phone = StringUtils.trim(phone);

        if (StringUtils.isEmpty(phone)) {
            throw new AppBusinessException(ErrorCode.InvalidArgument,"手机不能为空");
        }

        if(!Pattern.matches(RegExpUtils.PHONE_REG_EXP,phone)){
            throw new AppBusinessException(ErrorCode.InvalidArgument,"请输入正确的手机号码");
        }

        return phone;

    }


}