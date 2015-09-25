package controllers.api.user;

import api.response.user.UserDataDto;
import api.response.user.UserDateHomeDto;
import common.utils.JsonUtils;
import controllers.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import play.mvc.Result;
import usercenter.models.User;
import usercenter.models.UserData;
import usercenter.services.UserDataService;
import utils.secure.SecuredAction;

/**
 * 用户数据修改
 *
 * Created by zhb on 15-4-29.
 */
@org.springframework.stereotype.Controller
public class MyDataApiController extends BaseController {

    @Autowired
    private UserDataService userDataService;

    /**
     * 资料首页
     *
     * @return
     */
    @SecuredAction
    public Result index(){

        User user = this.currentUser();

        UserData userData = userDataService.findByUserId(user.getId());
        userData.splitBirthday();

        return ok(JsonUtils.object2Node(UserDateHomeDto.build(userData)));

    }


}
