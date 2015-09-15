package controllers.api.user;

import api.response.user.FavoritesDto;
import common.exceptions.AppBusinessException;
import common.exceptions.ErrorCode;
import common.utils.JsonResult;
import common.utils.JsonUtils;
import common.utils.ParamUtils;
import common.utils.page.Page;
import controllers.BaseController;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 收藏商品
 */
@org.springframework.stereotype.Controller
public class ProductFavoritesApiController extends BaseController {

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
    public Result list(int pageNo, int pageSize) {

        User user = this.currentUser();

        Page<ProductCollect> page = new Page(pageNo,pageSize);
        Page<FavoritesDto> pageDto = new Page(pageNo,pageSize);
        List<ProductCollect> pageProductCollcets = productCollectService.getProductCollectList(Optional.of(page),user.getId());
        List<FavoritesDto> favoritesDtos = new ArrayList<>();
        for(ProductCollect pc:pageProductCollcets){
            Product product = productService.getProductById(pc.getProductId());
            ProductPicture pp = productPictureService.getMinorProductPictureByProductId(pc.getProductId());

            pc.setProductName(product.getName());
            pc.setProductPic(pp.getPictureUrl());
            pc.setDesignerId(product.getCustomerId());

            favoritesDtos.add(FavoritesDto.build(pc));

        }

        pageDto.setResult(favoritesDtos);

        return ok(JsonUtils.object2Node(pageDto));

    }

    /**
     * 删除我的收藏商品
     *
     * @return
     */
    @SecuredAction
    public Result del(int id) {

        User user = this.currentUser();

        try {
            productCollectService.deleteMyProductCollect(id, user.getId());
        }catch (AppBusinessException e){
            throw new AppBusinessException(ErrorCode.Conflict, "没有该资源，操作失败");
        }

        return noContent();

    }

    /**
     * 添加我的收藏商品
     *
     * @return
     */
    @SecuredAction
    public Result add(){

        User user = this.currentUser();

        int productId = ParamUtils.getObjectId(request());

        Product product = productService.getProductById(productId);
        if(null == product){
            throw new AppBusinessException(ErrorCode.Conflict, "收藏的商品找不到");
        }

        ProductCollect oldProductCollect = productCollectService.getByProductId(productId,user.getId());

        if(null != oldProductCollect){
            throw new AppBusinessException(ErrorCode.Conflict, "已收藏该商品");
        }

        ProductCollect productCollect = new ProductCollect();

        productCollect.setProductId(productId);
        productCollect.setUserId(user.getId());
        productCollect.setCollectTime(DateTime.now());
        productCollectService.createProductCollect(productCollect);

        //productCollectService.countProductCollect(productId);

        return noContent();

    }



}