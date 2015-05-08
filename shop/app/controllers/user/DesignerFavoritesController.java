package controllers.user;

import common.utils.JsonResult;
import common.utils.page.Page;
import org.springframework.beans.factory.annotation.Autowired;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
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

    /** 每页显示 5 条数据 */
    public static final int DEFAULT_PAGE_SIZE = 5;

    /** 页数, 默认显示第 1 页 */
    private static final int DEFAULT_PAGE_NO = 1;

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
            DesignerPicture designerPicture = designerService.getDesignerPicByDesignerById(designerCollect.getDesignerId());
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

        designerCollectService.deleteMyDesignerCollect(designerId, user.getId());

        return redirect(routes.DesignerFavoritesController.index(DEFAULT_PAGE_NO, DEFAULT_PAGE_SIZE));

    }

    @SecuredAction
    public Result add(){

        User user = SessionUtils.currentUser();

        Form<DesignerCollect> DesignerCollectForm = Form.form(DesignerCollect.class).bindFromRequest();
        DesignerCollect designerCollect = DesignerCollectForm.get();

        Designer designer = designerService.getDesignerById(designerCollect.getDesignerId());
        if(null == designer){
            return ok(new JsonResult(false, "该设计师不存在").toNode());
        }

        DesignerCollect oldDesignerCollect = designerCollectService.getByDesignerId(designerCollect.getDesignerId(),user.getId());
        if(null != oldDesignerCollect){
            return ok(new JsonResult(false, "已关注该设计师").toNode());
        }

        designerCollect.setUserId(user.getId());
        designerCollectService.createDesignerCollect(designerCollect);

        return ok(new JsonResult(true,"关注成功").toNode());

    }



}