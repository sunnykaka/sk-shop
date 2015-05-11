package controllers.user;

import common.exceptions.AppBusinessException;
import common.utils.EmailUtils;
import common.utils.JsonResult;
import common.utils.ParamUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import usercenter.cache.SecurityCache;
import usercenter.dtos.CodeForm;
import usercenter.dtos.ChangePswForm;
import usercenter.dtos.PhoneCodeForm;
import usercenter.models.User;
import usercenter.services.UserService;
import utils.secure.SecuredAction;
import views.html.user.*;


/**
 * 安全信息管理
 *
 * Created by zhb on 15-4-29.
 */
@org.springframework.stereotype.Controller
public class MySecurityController extends Controller {

    public static final int test_userId = 14329;

    @Autowired
    private UserService userService;

    /**
     * 修改手机号码首页
     *
     * @return
     */
    @SecuredAction
    public Result changePhoneIndex(){

        User user = userService.getById(test_userId);

        return ok(changePhoneIndex.render(user));

    }

    /**
     * 执行验证
     *
     * @return
     */
    public Result changePhoneNew(){

        Form<CodeForm> codeForm = Form.form(CodeForm.class).bindFromRequest();

        if(!codeForm.hasErrors()) {
            try {
                CodeForm code = codeForm.get();
                User user = userService.getById(test_userId);
                //TODO 校验验证码是否正确

                //页面关联token
                SecurityCache.setToken(SecurityCache.SECURITY_TOKEN_PHONE_KEY, user.getPhone(), user.getPhone());
                return ok(new JsonResult(true, null, routes.MySecurityController.changePhoneDo().url()).toNode());

            } catch (AppBusinessException e) {
                codeForm.reject("errors", e.getMessage());
            }
        }

        return ok(new JsonResult(false, codeForm.errorsAsJson().toString()).toNode());

    }


    /**
     * 输入新手机页面
     *
     * @return
     */
    public Result changePhoneDo(){

        User user = userService.getById(test_userId);
        String phoneToken = SecurityCache.getToken(SecurityCache.SECURITY_TOKEN_PHONE_KEY, user.getPhone());
        if(StringUtils.isEmpty(phoneToken) || !user.getPhone().equals(phoneToken)){
            play.Logger.info("未经过验证页面，直接跳转");
            return redirect(routes.MySecurityController.changePhoneIndex().url());
        }

        return ok(changePhoneNew.render(user));

    }

    /**
     * 执行修改
     *
     * @return
     */
    public Result changePhoneEnd(){

        User user = userService.getById(test_userId);

        Form<PhoneCodeForm> phoneCodeForm = Form.form(PhoneCodeForm.class).bindFromRequest();

        if(!phoneCodeForm.hasErrors()) {
            try {
                userService.updatePhone(user,phoneCodeForm.get());
                return ok(new JsonResult(true, null, routes.MySecurityController.changePhoneOk().url()).toNode());

            } catch (AppBusinessException e) {
                phoneCodeForm.reject("errors", e.getMessage());
            }
        }

        return ok(new JsonResult(false, phoneCodeForm.errorsAsJson().toString()).toNode());

    }

    /**
     * 完成页面
     *
     * @return
     */
    public Result changePhoneOk(){

        User user = userService.getById(test_userId);
        SecurityCache.removeToken(SecurityCache.SECURITY_TOKEN_PHONE_KEY, user.getPhone());

        return ok(changePhoneDo.render(user));

    }

    /**
     * 修改密码首页
     *
     * @return
     */
    public Result changePasswordIndex(){

        User user = userService.getById(test_userId);

        //userService.updatePassword(user,"123456");

        return ok(changePasswordIndex.render(user));

    }

    /**
     * 执行验证
     *
     * @return
     */
    public Result changePasswordNew(){

        Form<CodeForm> codeForm = Form.form(CodeForm.class).bindFromRequest();

        if(!codeForm.hasErrors()) {
            try {
                CodeForm code = codeForm.get();
                User user = userService.getById(test_userId);
                //TODO 校验验证码是否正确

                //页面关联token
                SecurityCache.setToken(SecurityCache.SECURITY_TOKEN_PHONE_KEY, user.getPhone(), user.getPhone());
                return ok(new JsonResult(true, null, routes.MySecurityController.changePasswordDo().url()).toNode());

            } catch (AppBusinessException e) {
                codeForm.reject("errors", e.getMessage());
            }
        }

        return ok(new JsonResult(false, codeForm.errorsAsJson().toString()).toNode());

    }

    /**
     * 修改密码页面
     *
     * @return
     */
    public Result changePasswordDo(){

        User user = userService.getById(test_userId);
        String phoneToken = SecurityCache.getToken(SecurityCache.SECURITY_TOKEN_PHONE_KEY, user.getPhone());
        if(StringUtils.isEmpty(phoneToken) || !user.getPhone().equals(phoneToken)){
            play.Logger.info("未经过验证页面，直接跳转");
            return redirect(routes.MySecurityController.changePasswordIndex().url());
        }

        return ok(changePasswordNew.render(user));

    }

    /**
     * 执行修改
     *
     * @return
     */
    public Result changePasswordEnd(){
        User user = userService.getById(test_userId);

        Form<ChangePswForm> passwordForm = Form.form(ChangePswForm.class).bindFromRequest();

        if(!passwordForm.hasErrors()) {
            try {
                userService.updatePassword(user,passwordForm.get());
                return ok(new JsonResult(true, null, routes.MySecurityController.changePasswordOk().url()).toNode());

            } catch (AppBusinessException e) {
                passwordForm.reject("errors", e.getMessage());
            }
        }

        return ok(new JsonResult(false, passwordForm.errorsAsJson().toString()).toNode());
    }

    /**
     * 修改成功页面
     *
     * @return
     */
    public Result changePasswordOk(){
        User user = userService.getById(test_userId);
        SecurityCache.removeToken(SecurityCache.SECURITY_TOKEN_PHONE_KEY, user.getPhone());

        return ok(changePasswordDo.render());
    }

    /**
     * 修改邮箱首页
     *
     * @return
     */
    public Result changeEmailIndex(){

        User user = userService.getById(test_userId);

        //TODO test send
        //Email.sendEmail("zhenhaobin@163.com","测试发送邮件", index.render(user).toString());

        return ok(changeEmailIndex.render(user));

    }

    /**
     * 验证、验证码
     *
     * @return
     */
    public Result changeEmailNew(){

        Form<CodeForm> codeForm = Form.form(CodeForm.class).bindFromRequest();

        if(!codeForm.hasErrors()) {
            try {
                CodeForm code = codeForm.get();
                User user = userService.getById(test_userId);
                //TODO 校验验证码是否正确

                //页面关联token
                SecurityCache.setToken(SecurityCache.SECURITY_TOKEN_PHONE_KEY, user.getPhone(), user.getPhone());
                return ok(new JsonResult(true, null, routes.MySecurityController.changeEmailDo().url()).toNode());

            } catch (AppBusinessException e) {
                codeForm.reject("errors", e.getMessage());
            }
        }

        return ok(new JsonResult(false, codeForm.errorsAsJson().toString()).toNode());

    }

    /**
     * 修改邮箱页面
     *
     * @return
     */
    public Result changeEmailDo(){

        User user = userService.getById(test_userId);
        String phoneToken = SecurityCache.getToken(SecurityCache.SECURITY_TOKEN_PHONE_KEY, user.getPhone());
        if(StringUtils.isEmpty(phoneToken) || !user.getPhone().equals(phoneToken)){
            play.Logger.info("未经过验证页面，直接跳转");
            return redirect(routes.MySecurityController.changeEmailIndex().url());
        }

        return ok(changeEmailNew.render(user));

    }

    /**
     * 下发邮件到新邮箱
     *
     * @return
     */
    public Result changeEmailEnd(){

        User user = userService.getById(test_userId);

        String email = ParamUtils.getByKey(request(),"email");
        if(StringUtils.isEmpty(email)){
            return ok(new JsonResult(false,"邮箱地址为空").toNode());
        }
        email = StringUtils.trim(email);
        User emailUser = userService.findByEmail(email);
        if(null != emailUser){
            return ok(new JsonResult(false,"该邮箱地址已被注册").toNode());
        }

        //生成token
        SecurityCache.setToken(SecurityCache.SECURITY_TOKEN_EMAIL_CHANGE_KEY,user.getEmail(),email);
        EmailUtils.sendEmail(email, Messages.get("send.email.activity.title"),views.html.template.email.activity.render(user).toString());

        return ok(new JsonResult(true,"邮件已发送").toNode());

    }

    /**
     * 修改成功页面
     *
     * @return
     */
    public Result changeEmailOk(int userId){

        User user = userService.getById(userId);
        if(null == user){
            return ok(changeEmailDo.render("激活失败，请确认激活地址"));
        }
        String oldEmail = user.getEmail();
        String newEmail = SecurityCache.getToken(SecurityCache.SECURITY_TOKEN_EMAIL_CHANGE_KEY,oldEmail);

        if(StringUtils.isEmpty(newEmail)){
            return ok(changeEmailDo.render("激活失败，激活地址已过期"));
        }

        User emailUser = userService.findByEmail(newEmail);
        if(null != emailUser){
            return ok(changeEmailDo.render("激活失败，该邮箱地址已被抢先激活"));
        }
        userService.updateEmail(user,newEmail);
        SecurityCache.removeToken(SecurityCache.SECURITY_TOKEN_EMAIL_CHANGE_KEY,oldEmail);
        SecurityCache.removeToken(SecurityCache.SECURITY_TOKEN_PHONE_KEY, user.getPhone());

        return ok(changeEmailDo.render("修改成功"));

    }

    /**
     * 修改邮箱首页
     *
     * @return
     */
    public Result newEmailIndex(){

        User user = userService.getById(test_userId);

        //TODO test send
        //Email.sendEmail("zhenhaobin@163.com","测试发送邮件", index.render(user).toString());

        return ok(newEmailIndex.render(user));

    }

    /**
     * 验证、验证码
     *
     * @return
     */
    public Result newEmailNew(){

        Form<CodeForm> codeForm = Form.form(CodeForm.class).bindFromRequest();

        if(!codeForm.hasErrors()) {
            try {
                CodeForm code = codeForm.get();
                User user = userService.getById(test_userId);
                //TODO 校验验证码是否正确

                //页面关联token
                SecurityCache.setToken(SecurityCache.SECURITY_TOKEN_PHONE_KEY, user.getPhone(), user.getPhone());
                return ok(new JsonResult(true, null, routes.MySecurityController.newEmailDo().url()).toNode());

            } catch (AppBusinessException e) {
                codeForm.reject("errors", e.getMessage());
            }
        }

        return ok(new JsonResult(false, codeForm.errorsAsJson().toString()).toNode());

    }

    /**
     * 修改邮箱页面
     *
     * @return
     */
    public Result newEmailDo(){

        User user = userService.getById(test_userId);
        String phoneToken = SecurityCache.getToken(SecurityCache.SECURITY_TOKEN_PHONE_KEY, user.getPhone());
        if(StringUtils.isEmpty(phoneToken) || !user.getPhone().equals(phoneToken)){
            play.Logger.info("未经过验证页面，直接跳转");
            return redirect(routes.MySecurityController.newEmailIndex().url());
        }

        return ok(newEmailNew.render(user));

    }

    /**
     * 下发邮件到新邮箱
     *
     * @return
     */
    public Result newEmailEnd(){

        User user = userService.getById(test_userId);

        String email = ParamUtils.getByKey(request(),"email");
        if(StringUtils.isEmpty(email)){
            return ok(new JsonResult(false,"邮箱地址为空").toNode());
        }
        email = StringUtils.trim(email);
        User emailUser = userService.findByEmail(email);
        if(null != emailUser){
            return ok(new JsonResult(false,"该邮箱地址已被注册").toNode());
        }

        //生成token
        SecurityCache.setToken(SecurityCache.SECURITY_TOKEN_EMAIL_ACTIVITY_KEY,user.getEmail(),email);
        EmailUtils.sendEmail(email, Messages.get("send.email.activity.title"),views.html.template.email.activity.render(user).toString());

        return ok(new JsonResult(true,"邮件已发送").toNode());

    }

    /**
     * 修改成功页面
     *
     * @return
     */
    public Result newEmailOk(int userId){

        User user = userService.getById(userId);
        if(null == user){
            return ok(changeEmailDo.render("激活失败，请确认激活地址"));
        }
        String newEmail = SecurityCache.getToken(SecurityCache.SECURITY_TOKEN_EMAIL_ACTIVITY_KEY,user.getEmail());

        if(StringUtils.isEmpty(newEmail)){
            return ok(changeEmailDo.render("激活失败，激活地址已过期"));
        }

        User emailUser = userService.findByEmail(newEmail);
        if(null != emailUser){
            return ok(changeEmailDo.render("激活失败，该邮箱地址已被抢先激活"));
        }
        userService.updateEmail(user,newEmail);
        SecurityCache.removeToken(SecurityCache.SECURITY_TOKEN_PHONE_KEY, user.getPhone());


        return ok(newEmailDo.render("修改成功"));

    }

}
