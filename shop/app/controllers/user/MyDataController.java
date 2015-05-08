package controllers.user;

import common.exceptions.AppBusinessException;
import common.utils.JsonResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import usercenter.dtos.UserDataForm;
import usercenter.models.User;
import usercenter.models.UserData;
import usercenter.services.UserDataService;
import usercenter.utils.SessionUtils;
import utils.secure.SecuredAction;
import views.html.user.myData;

/**
 * 用户数据修改
 *
 * Created by zhb on 15-4-29.
 */
@org.springframework.stereotype.Controller
public class MyDataController extends Controller {

    @Autowired
    private UserDataService userDataService;

    /**
     * 资料首页
     *
     * @return
     */
    @SecuredAction
    public Result index(){

        User user = SessionUtils.currentUser();

        UserData userData = userDataService.findByUserId(user.getId());
        userData.splitBirthday();

        return ok(myData.render(userData));

    }

    /**
     * 修改资料
     *
     * @return
     */
    @SecuredAction
    public Result update(){

        User user = SessionUtils.currentUser();

        Form<UserDataForm> userDataForm = Form.form(UserDataForm.class).bindFromRequest();

        if(!userDataForm.hasErrors()) {
            try {

                UserDataForm userDataF = userDataForm.get();
                UserData userData = userDataService.findByUserId(user.getId());
                if(null == userData){
                    return ok(new JsonResult(false,"修改失败").toNode());
                }

                userData.setName(StringUtils.trim(userDataF.getName()));
                userData.setSex(userDataF.getSex());
                userData.setProvince(userDataF.getProvince());
                userData.setCity(userDataF.getCity());
                userData.setArea(userDataF.getArea());
                userData.setLocation(StringUtils.trim(userDataF.getLocation()));
                userData.setBirthdayY(userDataF.getBirthdayY());
                userData.setBirthdayM(userDataF.getBirthdayM());
                userData.setBirthdayD(userDataF.getBirthdayD());

                userData.mergerBirthday();
                userData.setBirthday(userData.getBirthday());

                userDataService.updateUserDate(userData);
                userData.splitBirthday();

                return ok(new JsonResult(true,"修改成功", userData).toNode());

            } catch (AppBusinessException e) {
                userDataForm.reject("errors", e.getMessage());
            }
        }

        return ok(new JsonResult(false, userDataForm.errorsAsJson().toString()).toNode());
    }


}
