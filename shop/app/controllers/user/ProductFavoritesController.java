package controllers.user;

import common.exceptions.AppBusinessException;
import common.utils.JsonResult;
import common.utils.page.Page;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import play.mvc.Controller;
import play.mvc.Result;
import productcenter.models.Product;
import productcenter.models.ProductCollect;
import productcenter.models.ProductPicture;
import productcenter.services.ProductCollectService;
import productcenter.services.ProductPictureService;
import productcenter.services.ProductService;
import usercenter.models.User;
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

    @Autowired
    private ProductCollectService productCollectService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductPictureService productPictureService;

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
            ProductPicture pp = productPictureService.getMinorProductPictureByProductId(pc.getProductId());

            pc.setProductName(product.getName());
            pc.setProductPic(pp.getPictureUrl());
            pc.setDesignerId(product.getCustomerId());

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

        try {
            productCollectService.deleteMyProductCollect(productId,user.getId());
        }catch (AppBusinessException e){
            return ok(new JsonResult(false, "删除失败").toNode());
        }


        return ok(new JsonResult(true, "删除成功").toNode());

    }

    /**
     * 添加我的收藏商品
     *
     * @return
     */
    @SecuredAction
    public Result add(int productId){

        User user = SessionUtils.currentUser();

        Product product = productService.getProductById(productId);
        if(null == product){
            return ok(new JsonResult(false, "收藏的商品找不到").toNode());
        }

        ProductCollect oldProductCollect = productCollectService.getByProductId(productId,user.getId());

        if(null != oldProductCollect){
            return ok(new JsonResult(false, "已收藏该商品").toNode());
        }

        ProductCollect productCollect = new ProductCollect();

        productCollect.setProductId(productId);
        productCollect.setUserId(user.getId());
        productCollect.setCollectTime(DateTime.now());
        productCollectService.createProductCollect(productCollect);

        int count = productCollectService.countProductCollect(productId);

        return ok(new JsonResult(true, String.valueOf(count)).toNode());

    }



}