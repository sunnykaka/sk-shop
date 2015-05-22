package controllers.user;

import common.exceptions.AppBusinessException;
import common.utils.EmailUtils;
import common.utils.JsonResult;
import common.utils.ParamUtils;
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
import usercenter.models.User;
import usercenter.services.UserService;
import usercenter.utils.SessionUtils;
import utils.secure.SecuredAction;
import views.html.user.*;

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

    @SecuredAction
    public Result index() {

        User user = SessionUtils.currentUser();

        return ok(mySecurityIndex.render(user));

    }

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

                if (!new SmsSender(user.getPhone(), SmsSender.Usage.BIND).verifyCode(code.getVerificationCode())) {
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

        return ok(new JsonResult(false, codeForm.errorsAsJson().toString()).toNode());

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

                userService.updatePhone(user, phoneCodeForm.get());
                SecurityCache.removeToken(SecurityCache.SECURITY_TOKEN_PHONE_KEY, user.getPhone());
                return ok(new JsonResult(true, null, routes.MySecurityController.changePhoneOk().url()).toNode());

            } catch (AppBusinessException e) {
                phoneCodeForm.reject("phone", e.getMessage());
            }
        }

        return ok(new JsonResult(false, phoneCodeForm.errorsAsJson().toString()).toNode());

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
                if (!new SmsSender(user.getPhone(), SmsSender.Usage.BIND).verifyCode(code.getVerificationCode())) {
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

        return ok(new JsonResult(false, codeForm.errorsAsJson().toString()).toNode());

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

        return ok(new JsonResult(false, passwordForm.errorsAsJson().toString()).toNode());
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
                if (!new SmsSender(user.getPhone(), SmsSender.Usage.BIND).verifyCode(code.getVerificationCode())) {
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

        return ok(new JsonResult(false, codeForm.errorsAsJson().toString()).toNode());

    }

    /**
     * 修改邮箱页面
     *
     * @return
     */
    @SecuredAction
    public Result changeEmailDo() {

        return ok(changeEmailNew.render(flash()));

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
        User emailUser = userService.findByEmail(email);
        if (null != emailUser) {
            return ok(new JsonResult(false, "该邮箱地址已被注册").toNode());
        }

        //生成token
        SecurityCache.setToken(SecurityCache.SECURITY_TOKEN_EMAIL_ACTIVITY_KEY, user.getEmail(), email);
        EmailUtils.sendEmail(email, Messages.get("send.email.activity.title"), views.html.template.email.activity.render(user).toString());

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
        userService.updateEmail(user, newEmail);
        SecurityCache.removeToken(SecurityCache.SECURITY_TOKEN_EMAIL_ACTIVITY_KEY, oldEmail);
        SecurityCache.removeToken(SecurityCache.SECURITY_TOKEN_EMAIL_ACTIVITY_KEY, user.getPhone());

        return ok(changeEmailDo.render("恭喜您，验证邮箱成功。"));

    }

//    /**
//     * 修改邮箱首页
//     *
//     * @return
//     */
//    public Result newEmailIndex(){
//
//        User user = userService.getById(test_userId);
//
//        //TODO test send
//        //Email.sendEmail("zhenhaobin@163.com","测试发送邮件", index.render(user).toString());
//
//        return ok(newEmailIndex.render(user));
//
//    }
//
//    /**
//     * 验证、验证码
//     *
//     * @return
//     */
//    public Result newEmailNew(){
//
//        Form<CodeForm> codeForm = Form.form(CodeForm.class).bindFromRequest();
//
//        if(!codeForm.hasErrors()) {
//            try {
//                CodeForm code = codeForm.get();
//                User user = userService.getById(test_userId);
//                //TODO 校验验证码是否正确
//
//                //页面关联token
//                SecurityCache.setToken(SecurityCache.SECURITY_TOKEN_PHONE_KEY, user.getPhone(), user.getPhone());
//                return ok(new JsonResult(true, null, routes.MySecurityController.newEmailDo().url()).toNode());
//
//            } catch (AppBusinessException e) {
//                codeForm.reject("errors", e.getMessage());
//            }
//        }
//
//        return ok(new JsonResult(false, codeForm.errorsAsJson().toString()).toNode());
//
//    }
//
//    /**
//     * 修改邮箱页面
//     *
//     * @return
//     */
//    public Result newEmailDo(){
//
//        User user = userService.getById(test_userId);
//        String phoneToken = SecurityCache.getToken(SecurityCache.SECURITY_TOKEN_PHONE_KEY, user.getPhone());
//        if(StringUtils.isEmpty(phoneToken) || !user.getPhone().equals(phoneToken)){
//            play.Logger.info("未经过验证页面，直接跳转");
//            return redirect(routes.MySecurityController.newEmailIndex().url());
//        }
//
//        return ok(newEmailNew.render(user));
//
//    }
//
//    /**
//     * 下发邮件到新邮箱
//     *
//     * @return
//     */
//    public Result newEmailEnd(){
//
//        User user = userService.getById(test_userId);
//
//        String email = ParamUtils.getByKey(request(),"email");
//        if(StringUtils.isEmpty(email)){
//            return ok(new JsonResult(false,"邮箱地址为空").toNode());
//        }
//        email = StringUtils.trim(email);
//        User emailUser = userService.findByEmail(email);
//        if(null != emailUser){
//            return ok(new JsonResult(false,"该邮箱地址已被注册").toNode());
//        }
//
//        //生成token
//        SecurityCache.setToken(SecurityCache.SECURITY_TOKEN_EMAIL_ACTIVITY_KEY,user.getEmail(),email);
//        EmailUtils.sendEmail(email, Messages.get("send.email.activity.title"),views.html.template.email.activity.render(user).toString());
//
//        return ok(new JsonResult(true,"邮件已发送").toNode());
//
//    }
//
//    /**
//     * 修改成功页面
//     *
//     * @return
//     */
//    public Result newEmailOk(int userId){
//
//        User user = userService.getById(userId);
//        if(null == user){
//            return ok(changeEmailDo.render("激活失败，请确认激活地址"));
//        }
//        String newEmail = SecurityCache.getToken(SecurityCache.SECURITY_TOKEN_EMAIL_ACTIVITY_KEY,user.getEmail());
//
//        if(StringUtils.isEmpty(newEmail)){
//            return ok(changeEmailDo.render("激活失败，激活地址已过期"));
//        }
//
//        User emailUser = userService.findByEmail(newEmail);
//        if(null != emailUser){
//            return ok(changeEmailDo.render("激活失败，该邮箱地址已被抢先激活"));
//        }
//        userService.updateEmail(user,newEmail);
//        SecurityCache.removeToken(SecurityCache.SECURITY_TOKEN_PHONE_KEY, user.getPhone());
//
//
//        return ok(newEmailDo.render("修改成功"));
//
//    }

}
