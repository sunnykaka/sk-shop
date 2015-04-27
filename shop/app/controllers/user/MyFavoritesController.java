package controllers.user;

import common.utils.page.Page;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import productcenter.models.ProductCollect;
import productcenter.services.ProductCollectService;
import views.html.user.myFavorites;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Controller
public class MyFavoritesController extends Controller {

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
    public Result favoritesIndex(int pageNo,int pageSize) {

        Page<ProductCollect> page = new Page(pageNo,pageSize);
        List<ProductCollect> pageProductCollcet = productCollectService.getProductCollectList(Optional.of(page),test_userId);
        page.setResult(pageProductCollcet);

        return ok(myFavorites.render(page));

    }

    /**
     * 删除我的收藏
     *
     * @param productId
     * @return
     */
    public Result favoritesDel(int productId) {

        productCollectService.deleteMyProductCollect(productId,test_userId);

        return redirect(routes.MyFavoritesController.favoritesIndex(DEFAULT_PAGE_NO,DEFAULT_PAGE_SIZE));

    }

    public Result favoritesAdd(){

        Form<ProductCollect> ProductCollectForm = Form.form(ProductCollect.class).bindFromRequest();
        ProductCollect productCollect = ProductCollectForm.get();
        productCollect.setUserId(test_userId);
        productCollect.setCollectTime(new DateTime());
        productCollectService.createProductCollect(productCollect);

        return redirect(routes.MyFavoritesController.favoritesIndex(DEFAULT_PAGE_NO,DEFAULT_PAGE_SIZE));

    }



}