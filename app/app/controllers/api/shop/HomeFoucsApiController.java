package controllers.api.shop;

import cmscenter.models.HomeNewProduct;
import cmscenter.services.HomeFocusService;
import common.utils.JsonUtils;
import common.utils.ParamUtils;
import common.utils.page.Page;
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

    public Result homeShop(int pageNo, int pageSize) {
        Page<HomeNewProduct> page = new Page(pageNo,pageSize);

        User user = this.currentUser();
        String deviceId = ParamUtils.getByKey(request(), "deviceId");

        if(pageNo <= 1){
            return ok(JsonUtils.object2Node(homeFoucsService.build(user, deviceId, page)));
        }else{
            return ok(JsonUtils.object2Node(homeFoucsService.buildProductList(page)));
        }

    }

}
