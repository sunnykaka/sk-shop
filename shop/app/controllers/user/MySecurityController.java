package controllers.user;

import common.constants.MessageJobSource;
import common.exceptions.AppBusinessException;
import common.services.MessageJobService;
import common.utils.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import usercenter.cache.SecurityCache;
import usercenter.domain.SmsSender;
import usercenter.dtos.CodeForm;
import usercenter.dtos.ChangePswForm;
import usercenter.dtos.PhoneCodeForm;
import usercenter.dtos.PswForm;
import usercenter.models.User;
import usercenter.services.UserService;
import usercenter.utils.SessionUtils;
import utils.secure.SecuredAction;
import views.html.user.*;

import java.util.HashMap;
import java.util.regex.Pattern;


/**
 * 安全信息管理
 * <p>
 * Created by zhb on 15-4-29.
 */
@org.springframework.stereotype.Controller
public class MySecurityController extends Controller {

    public static final int RANDOM_DEFAULT_SIZE = 16;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageJobService messageJobService;


    @SecuredAction
    public Result index() {

        User user = SessionUtils.currentUser();
        User newUser = userService.getById(user.getId());
        user.setPassword(newUser.getPassword());

        return ok(mySecurityIndex.render(user));

    }

    /**
     * 绑定手机号码
     *
     * @return
     */
    @SecuredAction
    public Result bindPhoneIndex() {

        return ok(bindPhoneIndex.render("",new HashMap<String,java.util.List<play.data.validation.ValidationError>>()));

    }

    /**
     * 处理绑定手机
     *
     * @return
     */
    @SecuredAction
    public Result bindPhoneDo() {

        User user = SessionUtils.currentUser();
        Form<PhoneCodeForm> phoneCodeForm = Form.form(PhoneCodeForm.class).bindFromRequest();

        if (!phoneCodeForm.hasErrors()) {
            try {

                SessionUtils.updateCurrentUser(userService.updatePhone(user, phoneCodeForm.get(), request().remoteAddress()));

                return ok(bindPhoneDo.render());

            } catch (AppBusinessException e) {
                phoneCodeForm.reject("verificationCode", e.getMessage());
            }
        }

        return ok(bindPhoneIndex.render(ParamUtils.getByKey(request(),"phone"),phoneCodeForm.errors()));

    }

//    /**
//     * 设置密码首页
//     *
//     * @return
//     */
//    @SecuredAction
//    public Result newPasswordIndex() {
//
//        return ok(newPassword.render(new HashMap<String,java.util.List<play.data.validation.ValidationError>>()));
//    }
//
//    /**
//     * 设置新密码
//     *
//     * @return
//     */
//    @SecuredAction
//    public Result newPasswordDo() {
//        User user = SessionUtils.currentUser();
//
//        Form<PswForm> passwordForm = Form.form(PswForm.class).bindFromRequest();
//
//        if (!passwordForm.hasErrors()) {
//            try {
//
//                userService.updatePassword(user, passwordForm.get());
//                return ok(newPasswordDo.render());
//
//            } catch (AppBusinessException e) {
//                passwordForm.reject("rePassword", e.getMessage());
//            }
//        }
//
//        return ok(newPassword.render(passwordForm.errors()));
//    }

    /**
     * 修改手机号码首页
     *
     * @return
     */
    @SecuredAction
    public Result changePhoneIndex() {

        User user = SessionUtils.currentUser();

        return ok(changePhoneIndex.render(user));

    }

    /**
     * 执行验证
     *
     * @return
     */
    @SecuredAction
    public Result changePhoneNew() {
        User user = SessionUtils.currentUser();

        Form<CodeForm> codeForm = Form.form(CodeForm.class).bindFromRequest();

        if (!codeForm.hasErrors()) {
            try {
                CodeForm code = codeForm.get();

                if (!new SmsSender(user.getPhone(), request().remoteAddress(), SmsSender.Usage.BIND).verifyCode(code.getVerificationCode())) {
                    throw new AppBusinessException("校验码验证失败");
                }

                //页面关联token
                String token = RandomStringUtils.randomAscii(RANDOM_DEFAULT_SIZE);
                flash(SecurityCache.SECURITY_TOKEN_PHONE_KEY, token);
                SecurityCache.setToken(SecurityCache.SECURITY_TOKEN_PHONE_KEY, user.getPhone(), token);
                return ok(new JsonResult(true, null, routes.MySecurityController.changePhoneDo().url()).toNode());

            } catch (AppBusinessException e) {
                codeForm.reject("verificationCode", e.getMessage());
            }
        }

        return ok(new JsonResult(false, FormUtils.showErrorInfo(codeForm.errors())).toNode());

    }

    @SecuredAction
    public Result checkPhone(String phone) {

        phone = StringUtils.trim(phone);

        if (StringUtils.isEmpty(phone)) {
            return ok(new JsonResult(false, "手机号码不能为空").toNode());
        }

        if (!Pattern.matches("^[1][\\d]{10}", phone)) {
            return ok(new JsonResult(false, "请输入正确的格式").toNode());
        }

        User user = SessionUtils.currentUser();
        if (phone.equals(user.getPhone())) {
            return ok(new JsonResult(false, "请输入与旧手机号码不一样的号码").toNode());
        } else {

            User userPhone = userService.findByPhone(phone);
            if (null != userPhone) {
                return ok(new JsonResult(false, "输入的号码已被注册").toNode());
            }

            return ok(new JsonResult(true, null).toNode());
        }

    }


    /**
     * 输入新手机页面
     *
     * @return
     */
    @SecuredAction
    public Result changePhoneDo() {

        return ok(changePhoneNew.render(flash()));

    }

    /**
     * 执行修改
     *
     * @return
     */
    @SecuredAction
    public Result changePhoneEnd() {

        User user = SessionUtils.currentUser();

        String tokenPage = ParamUtils.getByKey(request(), "token");
        String token = SecurityCache.getToken(SecurityCache.SECURITY_TOKEN_PHONE_KEY, user.getPhone());

        Form<PhoneCodeForm> phoneCodeForm = Form.form(PhoneCodeForm.class).bindFromRequest();

        if (!phoneCodeForm.hasErrors()) {
            try {
                if (StringUtils.isEmpty(tokenPage) || StringUtils.isEmpty(token)) {
                    throw new AppBusinessException("身份验证已失效，无法继续操作");
                }
                if (!tokenPage.equals(token)) {
                    throw new AppBusinessException("身份验证已失效，无法继续操作");
                }

                SessionUtils.updateCurrentUser(userService.updatePhone(user, phoneCodeForm.get(), request().remoteAddress()));
                SecurityCache.removeToken(SecurityCache.SECURITY_TOKEN_PHONE_KEY, user.getPhone());
                return ok(new JsonResult(true, null, routes.MySecurityController.changePhoneOk().url()).toNode());

            } catch (AppBusinessException e) {
                phoneCodeForm.reject("phone", e.getMessage());
            }
        }

        return ok(new JsonResult(false, FormUtils.showErrorInfo(phoneCodeForm.errors())).toNode());

    }

    /**
     * 完成页面
     *
     * @return
     */
    @SecuredAction
    public Result changePhoneOk() {

        return ok(changePhoneDo.render());

    }

    /**
     * 修改密码首页
     *
     * @return
     */
    @SecuredAction
    public Result changePasswordIndex() {

        User user = SessionUtils.currentUser();

        return ok(changePasswordIndex.render(user));

    }

    /**
     * 执行验证
     *
     * @return
     */
    @SecuredAction
    public Result changePasswordNew() {
        User user = SessionUtils.currentUser();

        Form<CodeForm> codeForm = Form.form(CodeForm.class).bindFromRequest();

        if (!codeForm.hasErrors()) {
            try {
                CodeForm code = codeForm.get();
                if (!new SmsSender(user.getPhone(), request().remoteAddress(), SmsSender.Usage.BIND).verifyCode(code.getVerificationCode())) {
                    throw new AppBusinessException("校验码验证失败");
                }

                //页面关联token
                String token = RandomStringUtils.randomAscii(RANDOM_DEFAULT_SIZE);
                flash(SecurityCache.SECURITY_TOKEN_PSW_CHANGE_KEY, token);
                SecurityCache.setToken(SecurityCache.SECURITY_TOKEN_PSW_CHANGE_KEY, user.getPhone(), token);
                return ok(new JsonResult(true, null, routes.MySecurityController.changePasswordDo().url()).toNode());

            } catch (AppBusinessException e) {
                codeForm.reject("verificationCode", e.getMessage());
            }
        }

        return ok(new JsonResult(false, FormUtils.showErrorInfo(codeForm.errors())).toNode());

    }

    /**
     * 修改密码页面
     *
     * @return
     */
    @SecuredAction
    public Result changePasswordDo() {

        return ok(changePasswordNew.render(flash()));

    }

    /**
     * 执行修改
     *
     * @return
     */
    @SecuredAction
    public Result changePasswordEnd() {
        User user = SessionUtils.currentUser();

        String tokenPage = ParamUtils.getByKey(request(), "token");
        String token = SecurityCache.getToken(SecurityCache.SECURITY_TOKEN_PSW_CHANGE_KEY, user.getPhone());

        Form<ChangePswForm> passwordForm = Form.form(ChangePswForm.class).bindFromRequest();

        if (!passwordForm.hasErrors()) {
            try {

                if (StringUtils.isEmpty(tokenPage) || StringUtils.isEmpty(token)) {
                    throw new AppBusinessException("身份验证已失效，无法继续操作");
                }
                if (!tokenPage.equals(token)) {
                    throw new AppBusinessException("身份验证已失效，无法继续操作");
                }

                userService.updatePassword(user, passwordForm.get());
                SecurityCache.removeToken(SecurityCache.SECURITY_TOKEN_PSW_CHANGE_KEY, user.getPhone());
                return ok(new JsonResult(true, null, routes.MySecurityController.changePasswordOk().url()).toNode());

            } catch (AppBusinessException e) {
                passwordForm.reject("password", e.getMessage());
            }
        }

        return ok(new JsonResult(false, FormUtils.showErrorInfo(passwordForm.errors())).toNode());
    }

    /**
     * 修改成功页面
     *
     * @return
     */
    @SecuredAction
    public Result changePasswordOk() {

        return ok(changePasswordDo.render());
    }

    /**
     * 修改邮箱首页
     *
     * @return
     */
    @SecuredAction
    public Result changeEmailIndex() {

        User user = SessionUtils.currentUser();

        return ok(changeEmailIndex.render(user));

    }

    /**
     * 验证、验证码
     *
     * @return
     */
    @SecuredAction
    public Result changeEmailNew() {
        User user = SessionUtils.currentUser();

        Form<CodeForm> codeForm = Form.form(CodeForm.class).bindFromRequest();

        if (!codeForm.hasErrors()) {
            try {
                CodeForm code = codeForm.get();
                if (!new SmsSender(user.getPhone(), request().remoteAddress(), SmsSender.Usage.BIND).verifyCode(code.getVerificationCode())) {
                    throw new AppBusinessException("校验码验证失败");
                }

                //页面关联token
                String token = RandomStringUtils.randomAscii(RANDOM_DEFAULT_SIZE);
                flash(SecurityCache.SECURITY_TOKEN_EMAIL_ACTIVITY_KEY, token);
                SecurityCache.setToken(SecurityCache.SECURITY_TOKEN_EMAIL_ACTIVITY_KEY, user.getPhone(), token);
                return ok(new JsonResult(true, null, routes.MySecurityController.changeEmailDo().url()).toNode());

            } catch (AppBusinessException e) {
                codeForm.reject("verificationCode", e.getMessage());
            }
        }

        return ok(new JsonResult(false, FormUtils.showErrorInfo(codeForm.errors())).toNode());

    }

    /**
     * 修改邮箱页面
     *
     * @return
     */
    @SecuredAction
    public Result changeEmailDo() {

        return ok(changeEmailNew.render(SessionUtils.currentUser(),flash()));

    }

    /**
     * 下发邮件到新邮箱
     *
     * @return
     */
    @SecuredAction
    public Result changeEmailEnd() {

        User user = SessionUtils.currentUser();

        String tokenPage = ParamUtils.getByKey(request(), "token");
        String token = SecurityCache.getToken(SecurityCache.SECURITY_TOKEN_EMAIL_ACTIVITY_KEY, user.getPhone());

        if (StringUtils.isEmpty(tokenPage) || StringUtils.isEmpty(token)) {
            return ok(new JsonResult(false, "身份验证已失效，无法继续操作").toNode());
        }
        if (!tokenPage.equals(token)) {
            return ok(new JsonResult(false, "身份验证已失效，无法继续操作").toNode());
        }

        String email = ParamUtils.getByKey(request(), "email");
        if (StringUtils.isEmpty(email)) {
            return ok(new JsonResult(false, "邮箱地址为空").toNode());
        }
        email = StringUtils.trim(email);
        if(!RegExpUtils.isEmail(email)){
            return ok(new JsonResult(false, "该邮箱地址格式不正确").toNode());
        }

        User emailUser = userService.findByEmail(email);
        if (null != emailUser) {
            return ok(new JsonResult(false, "该邮箱地址已被注册").toNode());
        }

        //生成token
        SecurityCache.setToken(SecurityCache.SECURITY_TOKEN_EMAIL_ACTIVITY_KEY, user.getEmail(), email);
        messageJobService.sendEmail(email, Messages.get("send.email.activity.title"),
                views.html.template.email.activity.render(user).toString(),
                MessageJobSource.CHANGE_EMAIL, null);

        return ok(new JsonResult(true, "邮件已发送").toNode());

    }

    /**
     * 修改成功页面
     *
     * @return
     */
    public Result changeEmailOk(int userId) {

        User user = userService.getById(userId);
        if (null == user) {
            return ok(changeEmailDo.render("激活失败，请确认激活地址"));
        }
        String oldEmail = user.getEmail();
        String newEmail = SecurityCache.getToken(SecurityCache.SECURITY_TOKEN_EMAIL_ACTIVITY_KEY, oldEmail);

        if (StringUtils.isEmpty(newEmail)) {
            return ok(changeEmailDo.render("激活失败，激活地址已过期"));
        }

        User emailUser = userService.findByEmail(newEmail);
        if (null != emailUser) {
            return ok(changeEmailDo.render("激活失败，该邮箱地址已被抢先激活"));
        }
        SessionUtils.updateCurrentUser(userService.updateEmail(user, newEmail));
        SecurityCache.removeToken(SecurityCache.SECURITY_TOKEN_EMAIL_ACTIVITY_KEY, oldEmail);
        SecurityCache.removeToken(SecurityCache.SECURITY_TOKEN_EMAIL_ACTIVITY_KEY, user.getPhone());

        return ok(changeEmailDo.render("恭喜您，验证邮箱成功。"));

    }


}
