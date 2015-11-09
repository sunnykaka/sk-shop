package controllers.api.shop;

import cmscenter.services.HomeFocusService;
import common.utils.JsonUtils;
import common.utils.ParamUtils;
import controllers.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import play.mvc.Result;
import usercenter.models.User;

/**
 * Created by zhb on 2015/10/29.
 */
@org.springframework.stereotype.Controller
public class HomeFoucsApiController extends BaseController {

    @Autowired
    private HomeFocusService homeFoucsService;

    public Result homeShop() {

        User user = this.currentUser();
        String deviceId = ParamUtils.getByKey(request(), "deviceId");

        return ok(JsonUtils.object2Node(homeFoucsService.build(user, deviceId)));

    }

}
