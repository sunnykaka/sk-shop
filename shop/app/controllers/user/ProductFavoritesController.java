package controllers.user;

import common.utils.JsonResult;
import common.utils.page.Page;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import productcenter.models.ProductCollect;
import productcenter.services.ProductCollectService;
import views.html.user.productFavorites;

import java.util.List;
import java.util.Optional;

/**
 * 收藏商品
 */
@org.springframework.stereotype.Controller
public class ProductFavoritesController extends Controller {

    /** 每页显示 5 条数据 */
    public static final int DEFAULT_PAGE_SIZE = 5;

    /** 页数, 默认显示第 1 页 */
    private static final int DEFAULT_PAGE_NO = 1;

    public static int test_userId = 1;

    @Autowired
    private ProductCollectService productCollectService;

    /**
     * 收藏商品列表
     *
     * @return
     */
    public Result index(int pageNo, int pageSize) {

        Page<ProductCollect> page = new Page(pageNo,pageSize);
        List<ProductCollect> pageProductCollcet = productCollectService.getProductCollectList(Optional.of(page),test_userId);
        page.setResult(pageProductCollcet);

        return ok(productFavorites.render(page));

    }

    /**
     * 删除我的收藏商品
     *
     * @param productId
     * @return
     */
    public Result del(int productId) {

        productCollectService.deleteMyProductCollect(productId,test_userId);

        return redirect(routes.ProductFavoritesController.index(DEFAULT_PAGE_NO, DEFAULT_PAGE_SIZE));

    }

    /**
     * 添加我的收藏商品
     *
     * @return
     */
    public Result add(){

        Form<ProductCollect> ProductCollectForm = Form.form(ProductCollect.class).bindFromRequest();
        ProductCollect productCollect = ProductCollectForm.get();

        ProductCollect oldProductCollect = productCollectService.getByProductId(productCollect.getProductId(),test_userId);

        if(null != oldProductCollect){
            return ok(new JsonResult(false, "已收藏该商品").toNode());
        }

        productCollect.setUserId(test_userId);
        productCollect.setCollectTime(new DateTime());
        productCollectService.createProductCollect(productCollect);

        return ok(new JsonResult(true, "收藏成功").toNode());

    }



}