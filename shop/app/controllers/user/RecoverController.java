package controllers.user;

import common.exceptions.AppBusinessException;
import common.utils.FormUtils;
import common.utils.JsonResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import usercenter.cache.RecoverCache;
import usercenter.cache.SecurityCache;
import usercenter.domain.SmsSender;
import usercenter.dtos.CodeForm;
import usercenter.dtos.PhoneCodeForm;
import usercenter.dtos.PswForm;
import usercenter.dtos.RecoverCodeForm;
import usercenter.models.User;
import usercenter.services.UserService;
import views.html.user.recoverIndex;
import views.html.user.recoverOk;
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

        //String errorPhone = recoverForm.error("phone").message();

        return ok(new JsonResult(false, FormUtils.showErrorInfo(recoverForm.errors())).toNode());

    }

    /**
     * 短信页面
     *
     * @return
     */
    public Result recoverSMSHtml(){
        return ok(recoverSMS.render(flash()));
    }

    /**
     * 处理短信验证
     *
     * @return
     */
    public Result recoverCheckSMS(){
        Form<PhoneCodeForm> phoneCodeForm = Form.form(PhoneCodeForm.class).bindFromRequest();
        if(!phoneCodeForm.hasErrors()) {
            try {
                PhoneCodeForm phoneCode = phoneCodeForm.get();

                if(!new SmsSender(phoneCode.getPhone(), SmsSender.Usage.REGISTER).verifyCode(phoneCode.getVerificationCode())) {
                    throw new AppBusinessException("校验码验证失败");
                }

                flash("phoneCode",phoneCode.getPhone());
                RecoverCache.setToken(RecoverCache.SECURITY_TOKEN_PHONE_KEY,phoneCode.getPhone(),phoneCode.getPhone());
                return ok(new JsonResult(true, null, routes.RecoverController.recoverPswHtml().url()).toNode());

            } catch (AppBusinessException e) {
                phoneCodeForm.reject("errors", e.getMessage());
            }
        }

        return ok(new JsonResult(false, FormUtils.showErrorInfo(phoneCodeForm.errors())).toNode());
    }

    /**
     * 修改密码页面
     *
     * @return
     */
    public Result recoverPswHtml(){

        return ok(recoverPsw.render(flash()));
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

                String phone = RecoverCache.getToken(RecoverCache.SECURITY_TOKEN_PHONE_KEY,psw.getPhone());
                if(StringUtils.isEmpty(psw.getPhone())){
                    throw new AppBusinessException("校验用户失败，请重新走流程");
                }
                if(!psw.getPhone().equals(phone)){
                    throw new AppBusinessException("校验用户失败，请重新走流程");
                }

                User user = userService.findByPhone(psw.getPhone());
                if(null == user){
                    throw new AppBusinessException("校验用户失败，请重新走流程");
                }

                userService.updatePassword(user,psw);

                RecoverCache.removeToken(RecoverCache.SECURITY_TOKEN_PHONE_KEY, psw.getPhone());
                return ok(new JsonResult(true, null, routes.RecoverController.recoverPswOk().url()).toNode());

            } catch (AppBusinessException e) {
                pswForm.reject("errors", e.getMessage());
            }
        }

        return ok(new JsonResult(false, FormUtils.showErrorInfo(pswForm.errors())).toNode());
    }

    public Result recoverPswOk(){
        return ok(recoverOk.render());
    }



}
