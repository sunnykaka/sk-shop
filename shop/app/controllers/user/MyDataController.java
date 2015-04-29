package controllers.user;

import common.utils.JsonResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import usercenter.models.UserData;
import usercenter.services.UserDataService;
import views.html.user.myData;

/**
 * 用户数据修改
 *
 * Created by zhb on 15-4-29.
 */
@org.springframework.stereotype.Controller
public class MyDataController extends Controller {

    public static final int test_userId = 14435;

    @Autowired
    private UserDataService userDataService;

    /**
     * 资料首页
     *
     * @return
     */
    public Result index(){

        UserData userData = userDataService.findByUserId(test_userId);
        userData.splitBirthday();

        return ok(myData.render(userData));

    }

    /**
     * 修改资料
     *
     * @return
     */
    public Result update(){

        Form<UserData> userDataForm = Form.form(UserData.class).bindFromRequest();
        UserData userData = userDataForm.get();

        if( null == userData.getId()){
            return ok(new JsonResult(false,"修改失败").toNode());
        }

        UserData oldUserData = userDataService.findById(userData.getId());
        if(null == oldUserData){
            return ok(new JsonResult(false,"修改失败").toNode());
        }
        if(oldUserData.getUserId() != test_userId){
            return ok(new JsonResult(false,"修改失败").toNode());
        }

        oldUserData.setName(StringUtils.trim(userData.getName()));
        oldUserData.setSex(userData.getSex());
        oldUserData.setProvince(userData.getProvince());
        oldUserData.setCity(userData.getCity());
        oldUserData.setArea(userData.getArea());
        oldUserData.setLocation(StringUtils.trim(userData.getLocation()));

        userData.mergerBirthday();
        oldUserData.setBirthday(userData.getBirthday());

        userDataService.updateUserDate(oldUserData);
        oldUserData.splitBirthday();

        return ok(new JsonResult(true,"修改成功", oldUserData).toNode());

    }


}
