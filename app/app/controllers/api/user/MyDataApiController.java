package controllers.api.user;

import api.response.user.FavoritesDto;
import api.response.user.UserDataDto;
import api.response.user.UserDateHomeDto;
import cmscenter.models.CmsExhibitionFans;
import cmscenter.services.SkCmsService;
import common.exceptions.AppBusinessException;
import common.exceptions.ErrorCode;
import common.utils.JsonResult;
import common.utils.JsonUtils;
import common.utils.page.Page;
import controllers.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import play.mvc.Result;
import productcenter.models.Product;
import productcenter.models.ProductPicture;
import productcenter.services.ProductPictureService;
import productcenter.services.ProductService;
import usercenter.models.User;
import usercenter.models.UserData;
import usercenter.services.UserDataService;
import usercenter.utils.SessionUtils;
import utils.secure.SecuredAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 用户数据修改
 *
 * Created by zhb on 15-4-29.
 */
@org.springframework.stereotype.Controller
public class MyDataApiController extends BaseController {

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private SkCmsService skCmsService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductPictureService productPictureService;

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

    @SecuredAction
    public Result getMyReminder(boolean processed, int pageNo, int pageSize){
        User user = this.currentUser();
        Page<CmsExhibitionFans> page = new Page(pageNo,pageSize);
        Page<FavoritesDto> pageDto = new Page(pageNo,pageSize);

        List<CmsExhibitionFans> cmsExhibitionFansList = skCmsService.findCmsExhibitionFansByPhone(Optional.of(page), user.getPhone(), processed);
        List<FavoritesDto> favoritesDtos = new ArrayList<>();
        for(CmsExhibitionFans cef:cmsExhibitionFansList){
            Product product = productService.getProductById(cef.getExhibitionId());
            ProductPicture pp = productPictureService.getMinorProductPictureByProductId(cef.getExhibitionId());
            favoritesDtos.add(new FavoritesDto(cef.getExhibitionId(), pp == null ? "" : pp.getPictureUrl(),
                    product == null ? "" : product.getName()));
        }
        pageDto.setResult(favoritesDtos);
        return ok(JsonUtils.object2Node(pageDto));

    }

    @SecuredAction
    public Result setMyReminder(Integer productId) {
        User user = this.currentUser();
        Optional<Integer> userId = user == null ? Optional.empty() : Optional.of(user.getId());
        try {
            skCmsService.userLikeExhibition(productId, user.getPhone(), userId);
            return noContent();
        } catch (AppBusinessException e) {
            throw new AppBusinessException(ErrorCode.InvalidArgument, "订阅失败");
        }
    }


}
