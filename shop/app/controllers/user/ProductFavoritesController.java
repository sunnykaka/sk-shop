package controllers.user;

import common.utils.JsonResult;
import common.utils.page.Page;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import productcenter.models.Product;
import productcenter.models.ProductCollect;
import productcenter.models.ProductPicture;
import productcenter.services.ProductCollectService;
import productcenter.services.ProductPictureService;
import productcenter.services.ProductService;
import usercenter.models.DesignerPicture;
import usercenter.models.User;
import usercenter.services.DesignerService;
import usercenter.utils.SessionUtils;
import utils.secure.SecuredAction;
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

    @Autowired
    private ProductCollectService productCollectService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductPictureService productPictureService;

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

        Page<ProductCollect> page = new Page(pageNo,pageSize);
        List<ProductCollect> pageProductCollcets = productCollectService.getProductCollectList(Optional.of(page),user.getId());

        for(ProductCollect pc:pageProductCollcets){
            Product product = productService.getProductById(pc.getProductId());
            ProductPicture pp = productPictureService.getMainProductPictureByProductId(pc.getProductId());
            DesignerPicture dp = designerService.getDesignerPicByDesignerById(product.getCustomerId());

            pc.setProductName(product.getName());
            pc.setProductPic(pp.getPictureUrl());
            pc.setDesignerId(product.getCustomerId());
            pc.setDesignerPic(dp.getPictureUrl());

        }

        page.setResult(pageProductCollcets);

        return ok(productFavorites.render(page));

    }

    /**
     * 删除我的收藏商品
     *
     * @param productId
     * @return
     */
    @SecuredAction
    public Result del(int productId) {

        User user = SessionUtils.currentUser();

        productCollectService.deleteMyProductCollect(productId,user.getId());

        return redirect(routes.ProductFavoritesController.index(DEFAULT_PAGE_NO, DEFAULT_PAGE_SIZE));

    }

    /**
     * 添加我的收藏商品
     *
     * @return
     */
    @SecuredAction
    public Result add(){

        User user = SessionUtils.currentUser();

        Form<ProductCollect> ProductCollectForm = Form.form(ProductCollect.class).bindFromRequest();
        ProductCollect productCollect = ProductCollectForm.get();

        ProductCollect oldProductCollect = productCollectService.getByProductId(productCollect.getProductId(),user.getId());

        if(null != oldProductCollect){
            return ok(new JsonResult(false, "已收藏该商品").toNode());
        }

        productCollect.setUserId(user.getId());
        productCollect.setCollectTime(new DateTime());
        productCollectService.createProductCollect(productCollect);

        return ok(new JsonResult(true, "收藏成功").toNode());

    }



}