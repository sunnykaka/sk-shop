package controllers;

import cmscenter.models.SkModule;
import cmscenter.services.SkCmsService;
import common.exceptions.AppBusinessException;
import common.utils.JsonResult;
import common.utils.page.Page;
import common.utils.play.BaseGlobal;
import dtos.ProductInSellList;
import dtos.ProductInfo;
import org.springframework.beans.factory.annotation.Autowired;
import play.mvc.Controller;
import play.mvc.Result;
import productcenter.constants.SaleStatus;
import productcenter.models.Product;
import productcenter.services.ProductCollectService;
import productcenter.services.ProductPictureService;
import productcenter.services.ProductService;
import usercenter.dtos.DesignerView;
import usercenter.models.User;
import usercenter.services.DesignerCollectService;
import usercenter.services.DesignerService;
import usercenter.utils.SessionUtils;
import utils.secure.SecuredAction;
import views.html.*;

import java.util.*;

import static java.util.stream.Collectors.toList;

@org.springframework.stereotype.Controller
public class Application extends Controller {


    @Autowired
    private SkCmsService skCmsService;


    @Autowired
    private DesignerService designerService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductPictureService productPictureService;

    @Autowired
    private ProductCollectService productCollectService;

    @Autowired
    private DesignerCollectService designerCollectService;

    /**
     * 首页，首发专场
     *
     * @return
     */
    public Result index() {

        List<SkModule> modules = skCmsService.buildModuleAndContent();
        return ok(index.render(SessionUtils.currentUser(), modules));
    }


    /**
     * 设计师产品列表页面
     *
     * @param dId
     * @return
     */
    public Result designerProd(Integer dId) {
        User user = SessionUtils.currentUser();

        /**
         * 1.获取设计师信息，并校验设计师是否存在
         */
        List<DesignerView> designers = designerService.designerById(dId);
        DesignerView designer = null;
        if (designers != null) {
            designer = designers.get(0);
            designer.setFavorites(designerCollectService.isFavorites(user, dId));
        } else {
            /**
             * 如果设计师不存在，则去404
             */
            return notFound();
        }

        List<Product> products = productService.products4Designer(dId);

        /**
         *组装产品相关的信息，包括：产品信息，产品主图，产品如果是首发就活取首发信息
         */
        ProductInSellList.Builder builder = ProductInSellList.Builder.getInstance();
        List<ProductInSellList> productList = products.stream().map(prod -> builder.buildProdct(prod)).collect(toList());


        /**
         *组装产品相关的信息，包括：产品信息，产品主图，产品如果是首发就活取首发信息
         */
//        List<ProductInfo> productList = products.stream().map(prod -> {
//            ProductInfo info = new ProductInfo();
//            info.setProduct(prod);
//            info.setMainPic(productPictureService.getMinorProductPictureByProductId(prod.getId()));
////            Optional<CmsExhibition> optional = cmsService.findExhibitionWithProdId(prod.getId());
//            info.setFavorites(productCollectService.isFavorites(user, prod.getId()));
//            info.setFavoritesNum(productCollectService.countProductCollect(prod.getId()));
////            if (optional.isPresent()) {
////                info.setCmsExhibition(optional.get());
////            }
//            return info;
//        }).collect(toList());

//        /**
//         * 如果设计师没有专场，那所有的商品直接展示为正常售卖
//         */
//        if (exhibitions == null || exhibitions.size() < 1) {
//            return ok(product_list.render(SessionUtils.currentUser(), designer, null, null, productList));
//        }

        /**
         * 首发
         */
        List<ProductInSellList> sellProds = productList.stream().filter(prod ->
                        prod.getProduct().getSaleStatus().equals(SaleStatus.FIRSTSELL.toString())
        ).collect(toList());


        /**
         * 预售
         */
        List<ProductInSellList> preProds = productList.stream().filter(prod ->
                        prod.getProduct().getSaleStatus().equals(SaleStatus.PRESELL.toString())
        ).collect(toList());


        /**
         * 热卖
         */
        List<ProductInSellList> normalProds = productList.stream().filter(prod ->
                        prod.getProduct().getSaleStatus().equals(SaleStatus.HOTSELL.toString())
        ).collect(toList());


        /**
         * 即将开售
         */
        List<ProductInSellList> planProds = productList.stream().filter(prod ->
                        prod.getProduct().getSaleStatus().equals(SaleStatus.PLANSELL.toString())
        ).collect(toList());


        /**
         * 排序，将售完的商品排至最后
         */
        Collections.sort(sellProds);
        Collections.sort(normalProds);
        Collections.sort(preProds);
        Collections.sort(planProds);

        return ok(product_list.render(SessionUtils.currentUser(), designer, sellProds, preProds, normalProds, planProds));
    }


    /**
     * 设计师列表
     *
     * @return
     */
    public Result designers(int currPage) {
        Page page = new Page(currPage, 8);
        List<DesignerView> list = designerService.designerByPriority(page);
//        List<CmsContent> contents = cmsService.allContents();
//        CmsContent content = contents.stream().filter(co -> co.getPosition().equals(CmsPosition.DESIGNER_LIST_LOGO)).findFirst().get();
        Integer count = designerService.allOnlineDesignerCount();
        return ok(designers.render(list, count));
    }

    public Result designers4More(int currPage) {
        Page page = new Page(currPage, 8);
        List<DesignerView> list = designerService.designerByPriority(page);
        return ok(new JsonResult(true, "", list).toNode());
    }


    @SecuredAction
    public Result myOrder() {

        User user = SessionUtils.currentUser();
        System.out.println("username" + user.getUserName());

        return ok(myOrder.render(user));

    }

    public Result userLikeExhibition(Integer prodId, String phone) {
        User user = SessionUtils.currentUser();
        Optional<Integer> userId = user == null ? Optional.empty() : Optional.of(user.getId());
        try {
            skCmsService.userLikeExhibition(prodId, phone, userId);
            return ok(new JsonResult(true).toNode());
        } catch (AppBusinessException e) {
            return ok(new JsonResult(false, e.getMessage()).toNode());
        }
    }


    public Result about() {

        return ok(about.render());

    }


    public Result robots() {

        return ok(views.txt.robots.render(BaseGlobal.isProd()));

    }


}