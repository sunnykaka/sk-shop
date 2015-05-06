package controllers.user;

import common.exceptions.AppBusinessException;
import common.utils.JsonResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import usercenter.cache.RecoverCache;
import usercenter.cache.SecurityCache;
import usercenter.dtos.CodeForm;
import usercenter.dtos.PswForm;
import usercenter.dtos.RecoverCodeForm;
import usercenter.models.User;
import usercenter.services.UserService;
import views.html.user.recoverIndex;
import views.html.user.recoverPsw;
import views.html.user.recoverSMS;

/**
 * 找回密码
 *
 * Created by zhb on 15-5-5.
 */
@org.springframework.stereotype.Controller
public class RecoverController extends Controller {

    @Autowired
    private UserService userService;

    /**
     * 忘记密码首页
     *
     * @return
     */
    public Result recoverIndex() {

        return ok(recoverIndex.render());

    }

    /**
     * 验证号码
     *
     * @return
     */
    public Result recoverCheckPhone(){

        Form<RecoverCodeForm> recoverForm = Form.form(RecoverCodeForm.class).bindFromRequest();
        if(!recoverForm.hasErrors()) {
            try {
                RecoverCodeForm recoverCodeForm = recoverForm.get();

                User user = userService.findByPhone(recoverCodeForm.getPhone());
                if(null == user){
                    throw new AppBusinessException("用户不存在");
                }

                Object imageCodeInSession = Http.Context.current().session().get(ImageController.SESSION_KEY_IMAGECODE);
                if (null != imageCodeInSession) {
                    if(recoverCodeForm.getImageCode().equals(imageCodeInSession.toString())){
                        Http.Context.current().session().remove(ImageController.SESSION_KEY_IMAGECODE);
                        flash("phone",recoverCodeForm.getPhone());
                        return ok(new JsonResult(true, null, routes.RecoverController.recoverSMSHtml().url()).toNode());
                    }
                }
                throw new AppBusinessException("验证码错误");

            } catch (AppBusinessException e) {
                recoverForm.reject("errors", e.getMessage());
            }
        }

        return ok(new JsonResult(false, recoverForm.errorsAsJson().toString()).toNode());

    }

    /**
     * 短信页面
     *
     * @return
     */
    public Result recoverSMSHtml(){

        String phone = flash("phone");

//        String phoneToken = SecurityCache.getToken(RecoverCache.SECURITY_TOKEN_PHONE_KEY, user.getPhone());
//        if(StringUtils.isEmpty(phoneToken) || !user.getPhone().equals(phoneToken)){
//            play.Logger.info("未经过验证页面，直接跳转");
//            return redirect(routes.MySecurityController.changePhoneIndex().url());
//        }

        return ok(recoverSMS.render());
    }

    /**
     * 下发短信
     *
     * @return
     */
    public Result recoverSendSMS(){
        return ok();
    }

    /**
     * 处理短信验证
     *
     * @return
     */
    public Result recoverCheckSMS(){
        Form<CodeForm> codeForm = Form.form(CodeForm.class).bindFromRequest();
        if(!codeForm.hasErrors()) {
            try {
                CodeForm code = codeForm.get();

                //页面关联token
                //SecurityCache.setToken(SecurityCache.SECURITY_TOKEN_PHONE_KEY, user.getPhone(), user.getPhone());
                return ok(new JsonResult(true, null, routes.RecoverController.recoverPswHtml().url()).toNode());

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
    public Result recoverPswHtml(){
        return ok(recoverPsw.render());
    }

    /**
     * 执行修改密码
     *
     * @return
     */
    public Result recoverPswDo(){
        Form<PswForm> pswForm = Form.form(PswForm.class).bindFromRequest();
        if(!pswForm.hasErrors()) {
            try {
                PswForm psw = pswForm.get();

                //页面关联token
                //SecurityCache.setToken(SecurityCache.SECURITY_TOKEN_PHONE_KEY, user.getPhone(), user.getPhone());
                return ok(new JsonResult(true, null, routes.RecoverController.recoverPswOk().url()).toNode());

            } catch (AppBusinessException e) {
                pswForm.reject("errors", e.getMessage());
            }
        }

        return ok(new JsonResult(false, pswForm.errorsAsJson().toString()).toNode());
    }

    public Result recoverPswOk(){
        return ok();
    }



}
