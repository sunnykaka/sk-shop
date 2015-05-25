package controllers.user;

import common.exceptions.AppBusinessException;
import common.utils.JsonResult;
import common.utils.page.Page;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import usercenter.constants.DesignerPictureType;
import usercenter.models.Designer;
import usercenter.models.DesignerCollect;
import usercenter.models.DesignerPicture;
import usercenter.models.User;
import usercenter.services.DesignerCollectService;
import usercenter.services.DesignerService;
import usercenter.utils.SessionUtils;
import utils.secure.SecuredAction;
import views.html.user.designerFavorites;

import java.util.List;
import java.util.Optional;

/**
 * 关注设计师
 */
@org.springframework.stereotype.Controller
public class DesignerFavoritesController extends Controller {

    @Autowired
    private DesignerCollectService designerCollectService;

    @Autowired
    private DesignerService designerService;

    /**
     * 收藏商品列表
     *
     * @return
     */
    @SecuredAction
    public Result index(int pageNo, int pageSize) {

        User user = SessionUtils.currentUser();

        Page<DesignerCollect> page = new Page(pageNo,pageSize);
        List<DesignerCollect> pageProductCollcet = designerCollectService.getDesignerCollectList(Optional.of(page), user.getId());
        for(DesignerCollect designerCollect:pageProductCollcet){
            Designer designer = designerService.getDesignerById(designerCollect.getDesignerId());
            DesignerPicture designerPicture = designerService.getDesignerPicByType(designerCollect.getDesignerId(), DesignerPictureType.IndexLogoSmallPic);
            designerCollect.setDesignerName(designer.getName());
            designerCollect.setDesignerPic(designerPicture.getPictureUrl());
        }

        page.setResult(pageProductCollcet);

        return ok(designerFavorites.render(page));

    }

    /**
     * 删除我的收藏
     *
     * @param designerId
     * @return
     */
    @SecuredAction
    public Result del(int designerId) {

        User user = SessionUtils.currentUser();

        try {
            designerCollectService.deleteMyDesignerCollect(designerId, user.getId());
        }catch (AppBusinessException e){
            return ok(new JsonResult(false, "删除失败").toNode());
        }

        return ok(new JsonResult(true, "删除成功").toNode());

    }

    @SecuredAction
    public Result add(int designerId){

        User user = SessionUtils.currentUser();

        Designer designer = designerService.getDesignerById(designerId);
        if(null == designer){
            return ok(new JsonResult(false, "该设计师不存在").toNode());
        }

        DesignerCollect oldDesignerCollect = designerCollectService.getByDesignerId(designerId,user.getId());
        if(null != oldDesignerCollect){
            return ok(new JsonResult(false, "已关注该设计师").toNode());
        }

        DesignerCollect designerCollect = new DesignerCollect();
        designerCollect.setDesignerId(designerId);
        designerCollect.setUserId(user.getId());
        designerCollect.setCollectTime(DateTime.now());
        designerCollectService.createDesignerCollect(designerCollect);

        return ok(new JsonResult(true,"关注成功").toNode());

    }



}