package controllers;

import dtos.ProductInfo;
import models.CmsContent;
import models.CmsExbitionItem;
import models.CmsExhibition;
import models.ExhibitionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import play.mvc.Controller;
import play.mvc.Result;
import productcenter.models.Product;
import productcenter.services.ProductPictureService;
import productcenter.services.ProductService;
import services.CmsService;
import usercenter.dtos.DesignerView;
import usercenter.models.User;
import usercenter.services.DesignerService;
import usercenter.utils.SessionUtils;
import utils.secure.SecuredAction;
import views.html.*;

import java.util.*;

import static java.util.stream.Collectors.toList;

@org.springframework.stereotype.Controller
public class Application extends Controller {

    /**
     * 首页轮播图
     */
    private static final String SLIDER_BOX = "sliderBox";


    @Autowired
    private CmsService cmsService;

    @Autowired
    private DesignerService designerService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductPictureService productPictureService;


    /**
     * 首页，首发专场
     *
     * @return
     */
    public Result index() {
        Map<ExhibitionStatus, List<CmsExhibition>> exhibitions = cmsService.queryAllExhibition();
        List<CmsExhibition> sellingList = exhibitions.get(ExhibitionStatus.SELLING);
        List<CmsContent> contents = cmsService.allContents();
        List<CmsContent> sliderBoxs = new ArrayList<>();
        List<CmsContent> fontList = new ArrayList<>();
        for (CmsContent content : contents) {
            if (content.getType().equals("PIC") && content.getPosition().equals("sliderBox")) {
                sliderBoxs.add(content);
            }
            if (content.getType().equals("FONT")) {
                fontList.add(content);
            }
        }
        return ok(index.render(SessionUtils.currentUser(), sellingList, fontList, sliderBoxs));
    }


    /**
     * 预告
     *
     * @return
     */
    public Result preview() {
        Map<ExhibitionStatus, List<CmsExhibition>> exhibitions = cmsService.queryAllExhibition();
        List<CmsExhibition> sellingList = exhibitions.get(ExhibitionStatus.PREPARE);
        List<CmsContent> contents = cmsService.allContents();
        List<CmsContent> sliderBoxs = new ArrayList<>();
        List<CmsContent> fontList = new ArrayList<>();
        for (CmsContent content : contents) {
            if (content.getType().equals("PIC") && content.getPosition().equals("sliderBox")) {
                sliderBoxs.add(content);
            }
            if (content.getType().equals("FONT")) {
                fontList.add(content);
            }
        }
        return ok(preview.render(SessionUtils.currentUser(), sellingList, fontList, sliderBoxs));
    }


    /**
     * 设计师产品列表页面
     *
     * @param dId
     * @return
     */
    public Result designerProd(Integer dId) {

        /**
         * 1.获取设计师信息，并校验设计师是否存在
         */
        List<DesignerView> designers = designerService.designerById(dId);
        DesignerView designer = null;
        if (designers != null) {
            designer = designers.get(0);
        } else {
            /**
             * 如果设计师不存在，则去404
             */
            return notFound();
        }


        List<CmsExhibition> exhibitions = cmsService.findExhibitionByDesigner(dId);
        List<Product> products = productService.products4Designer(dId);

        /**
         *组装产品相关的信息，包括：产品信息，产品主图，产品如果是首发就活取首发信息
         */
        List<ProductInfo> productList = products.stream().map(prod -> {
            ProductInfo info = new ProductInfo();
            info.setProduct(prod);
            info.setMainPic(productPictureService.getMainProductPictureByProductId(prod.getId()));
            Optional<CmsExhibition> optional = cmsService.findExhibitionWithProdId(prod.getId());
            if (optional.isPresent()) {
                info.setCmsExhibition(optional.get());
            }
            return info;
        }).collect(toList());

        /**
         * 如果设计师没有专场，那所有的商品直接展示为正常售卖
         */
        if (exhibitions == null || exhibitions.size() < 1) {
            return ok(product_list.render(SessionUtils.currentUser(), designer, null, null, productList));
        }

        List<ProductInfo> sellProds = productList.stream().filter(prod -> {
            CmsExhibition ex = prod.getCmsExhibition();
            if (ex == null) {
                return false;
            }
            return ex.getStatus().equals(ExhibitionStatus.SELLING);
        }).collect(toList());


        List<ProductInfo> preProds = productList.stream().filter(prod -> {
            CmsExhibition ex = prod.getCmsExhibition();
            if (ex == null) {
                return false;
            }
            return ex.getStatus().equals(ExhibitionStatus.PREPARE);
        }).collect(toList());

        List<ProductInfo> normalProds = productList.stream().filter(prod -> {
            CmsExhibition ex = prod.getCmsExhibition();
            if (ex == null) {
                return true;
            }
            return ex.getStatus().equals(ExhibitionStatus.OVER);
        }).collect(toList());

//        /**
//         * 先拿到预告的专场，再查询专场的产品的id
//         */
//        List<Integer> preIds = exhibitions.stream().filter(ex -> ex.getStatus().equals(ExhibitionStatus.PREPARE)).map(ex -> ex.getId()).collect(toList());
//        List<CmsExbitionItem> items = cmsService.queryItemListByExbId(preIds);
//        Set<Integer> preProdIds = items.stream().map(item -> item.getProdId()).collect(toSet());
//
//        /**
//         * 首发中的商品ID
//         */
//        List<Integer> sellIds = exhibitions.stream().filter(ex -> ex.getStatus().equals(ExhibitionStatus.PREPARE)).map(ex -> ex.getId()).collect(toList());
//        List<CmsExbitionItem> items2 = cmsService.queryItemListByExbId(sellIds);
//        Set<Integer> sellProdIds = items2.stream().map(item -> item.getProdId()).collect(toSet());
//
//
//        List<Product> preProds = products.stream().filter(prod -> preProdIds.contains(prod.getId())).collect(toList());
//        List<Product> sellProds = products.stream().filter(prod -> sellProdIds.contains(prod.getId())).collect(toList());
//        List<Product> normalProds = products.stream().filter(prod -> !sellProdIds.contains(prod.getId()) && !preProdIds.contains(prod.getId())).collect(toList());

        return ok(product_list.render(SessionUtils.currentUser(), designer, sellProds, preProds, normalProds));
    }


    /**
     * 设计师列表
     *
     * @return
     */
    public Result designers() {
        List<DesignerView> list = designerService.designerById(null);
        return ok(designers.render(list));
    }


    @SecuredAction
    public Result myOrder() {

        User user = SessionUtils.currentUser();
        System.out.println("username" + user.getUserName());

        return ok(myOrder.render(user));

    }


}