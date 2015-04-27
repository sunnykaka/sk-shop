package controllers.user;

import common.utils.JsonResult;
import common.utils.page.Page;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import usercenter.models.DesignerCollect;
import usercenter.services.DesignerCollectService;
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

    public static int test_userId = 1;

    @Autowired
    private DesignerCollectService designerCollectService;

    /**
     * 收藏商品列表
     *
     * @return
     */
    public Result index(int pageNo, int pageSize) {

        Page<DesignerCollect> page = new Page(pageNo,pageSize);
        List<DesignerCollect> pageProductCollcet = designerCollectService.getDesignerCollectList(Optional.of(page), test_userId);
        page.setResult(pageProductCollcet);

        return ok(designerFavorites.render(page));

    }

    /**
     * 删除我的收藏
     *
     * @param designerId
     * @return
     */
    public Result del(int designerId) {

        designerCollectService.deleteMyDesignerCollect(designerId, test_userId);

        return redirect(routes.DesignerFavoritesController.index(DEFAULT_PAGE_NO, DEFAULT_PAGE_SIZE));

    }

    public Result add(){

        Form<DesignerCollect> DesignerCollectForm = Form.form(DesignerCollect.class).bindFromRequest();
        DesignerCollect designerCollect = DesignerCollectForm.get();

        DesignerCollect oldDesignerCollect = designerCollectService.getByDesignerId(designerCollect.getDesignerId(),test_userId);
        if(null != oldDesignerCollect){
            return ok(new JsonResult(false, "已关注该设计师").toNode());
        }

        designerCollect.setUserId(test_userId);
        designerCollectService.createDesignerCollect(designerCollect);

        //return redirect(routes.DesignerFavoritesController.index(DEFAULT_PAGE_NO,DEFAULT_PAGE_SIZE));
        return ok(new JsonResult(true,"关注成功").toNode());

    }



}