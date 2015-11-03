package controllers.api.shop;

import cmscenter.dtos.HomePageDto;
import cmscenter.services.HomeFocusService;
import common.utils.JsonUtils;
import controllers.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import play.mvc.Result;

/**
 * Created by zhb on 2015/10/29.
 */
@org.springframework.stereotype.Controller
public class HomeFoucsApiController extends BaseController {

    @Autowired
    private HomeFocusService homeFoucsService;

    public Result homeShop() {

        return ok(JsonUtils.object2Node(homeFoucsService.build()));

    }

}
