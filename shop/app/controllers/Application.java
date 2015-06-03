package controllers;

import common.utils.JsonResult;
import common.utils.page.Page;
import dtos.CmsPosition;
import dtos.ExhibitionPosition;
import dtos.ProductInfo;
import models.CmsContent;
import models.CmsExbitionItem;
import models.CmsExhibition;
import models.ExhibitionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import play.mvc.Controller;
import play.mvc.Result;
import productcenter.models.Product;
import productcenter.services.ProductCollectService;
import productcenter.services.ProductPictureService;
import productcenter.services.ProductService;
import services.CmsService;
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
    private CmsService cmsService;

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
        List<CmsExhibition> floor1 = cmsService.queryExhibitionByPosition(ExhibitionPosition.FLOOR_ONE, 4, ExhibitionStatus.SELLING);
        List<CmsExhibition> floor2 = cmsService.queryExhibitionByPosition(ExhibitionPosition.FLOOR_TWO, 3, ExhibitionStatus.SELLING);
        List<CmsExhibition> floor3 = cmsService.queryExhibitionByPosition(ExhibitionPosition.FLOOR_THREE, 6, ExhibitionStatus.SELLING);
        List<CmsExhibition> floor3Double = cmsService.queryExhibitionByPosition(ExhibitionPosition.FLOOR_THREE_DOUBLE, 1, ExhibitionStatus.SELLING);

        CmsExhibition doubleExhibition = null;

        if (floor3Double != null && floor3Double.size()>0) {
            doubleExhibition = floor3Double.get(0);
        }

        Map<String, List<CmsExhibition>> exhibtionMap = new HashMap<>();
        exhibtionMap.put(ExhibitionPosition.FLOOR_ONE, floor1);
        exhibtionMap.put(ExhibitionPosition.FLOOR_TWO, floor2);
        exhibtionMap.put(ExhibitionPosition.FLOOR_THREE, floor3);


        List<CmsContent> contents = cmsService.allContents();

        List<CmsContent> sliderBoxs = contents.stream().filter(content -> content.getPosition().equals(CmsPosition.SLIDER_BOX)).collect(toList());
        CmsContent font1 = contents.stream().filter(content -> content.getPosition().equals(CmsPosition.INDEX_FONT_1)).findFirst().get();
        CmsContent font2 = contents.stream().filter(content -> content.getPosition().equals(CmsPosition.INDEX_FONT_2)).findFirst().get();

        int size = sliderBoxs.size() > 3? 3 : sliderBoxs.size();

        return ok(index.render(SessionUtils.currentUser(), exhibtionMap, doubleExhibition, font1, font2, sliderBoxs.subList(0,size)));
    }


    private void checkExhibitionContent(List<CmsExhibition> list) {
        /**
         * 内容是满的
         */
        if (list.size() == 14) {
            return;
        }
        int count = 1;
        List<Integer> missPositionIndex = new ArrayList<>(14);
        for (CmsExhibition ex : list) {
            if (ex.getPositionIndex() == count) {
                continue;
            }
            list.add(cmsService.findExhibitionByPosition(count));
            count++;
        }


    }


    /**
     * 预告
     *
     * @return
     */
    public Result preview() {
        List<CmsExhibition> floor1 = cmsService.queryExhibitionByPosition(ExhibitionPosition.FLOOR_ONE, 4, ExhibitionStatus.PREPARE);
        List<CmsExhibition> floor2 = cmsService.queryExhibitionByPosition(ExhibitionPosition.FLOOR_TWO, 3, ExhibitionStatus.PREPARE);
        List<CmsExhibition> floor3 = cmsService.queryExhibitionByPosition(ExhibitionPosition.FLOOR_THREE, 6, ExhibitionStatus.PREPARE);
        List<CmsExhibition> floor3Double = cmsService.queryExhibitionByPosition(ExhibitionPosition.FLOOR_THREE_DOUBLE, 1, ExhibitionStatus.PREPARE);

        CmsExhibition doubleExhibition = null;

        if (floor3Double != null && floor3Double.size()>0) {
            doubleExhibition = floor3Double.get(0);
        }

        Map<String, List<CmsExhibition>> exhibtionMap = new HashMap<>();
        exhibtionMap.put(ExhibitionPosition.FLOOR_ONE, floor1);
        exhibtionMap.put(ExhibitionPosition.FLOOR_TWO, floor2);
        exhibtionMap.put(ExhibitionPosition.FLOOR_THREE, floor3);


        List<CmsContent> contents = cmsService.allContents();

        List<CmsContent> sliderBoxs = contents.stream().filter(content -> content.getPosition().equals(CmsPosition.PREVIEW_SLIDER_BOX)).collect(toList());
        CmsContent font1 = contents.stream().filter(content -> content.getPosition().equals(CmsPosition.PREVIEW_FONT_1)).findFirst().get();
        CmsContent font2 = contents.stream().filter(content -> content.getPosition().equals(CmsPosition.PREVIEW_FONT_2)).findFirst().get();

        int size = sliderBoxs.size() > 3? 3 : sliderBoxs.size();

        return ok(preview.render(SessionUtils.currentUser(), exhibtionMap, doubleExhibition, font1, font2, sliderBoxs.subList(0, size)));
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
        List<DesignerView> designers = designerService.designerById(dId,null);
        DesignerView designer = null;
        if (designers != null) {
            designer = designers.get(0);
            designer.setFavorites(designerCollectService.isFavorites(user,dId));
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
            info.setMainPic(productPictureService.getMinorProductPictureByProductId(prod.getId()));
            Optional<CmsExhibition> optional = cmsService.findExhibitionWithProdId(prod.getId());
            info.setFavorites(productCollectService.isFavorites(user, prod.getId()));
            info.setFavoritesNum(productCollectService.countProductCollect(prod.getId()));
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
    public Result designers(int currPage) {
        Page  page = new Page(currPage,8);
        List<DesignerView> list = designerService.designerById(null,page);
        List<CmsContent> contents = cmsService.allContents();
        CmsContent content = contents.stream().filter(co -> co.getPosition().equals(CmsPosition.DESIGNER_LIST_LOGO)).findFirst().get();
        return ok(designers.render(content,list));
    }

    public Result designers4More(int currPage) {
        Page  page = new Page(currPage,8);
        List<DesignerView> list = designerService.designerById(null, page);
        return ok(new JsonResult(true,"",list).toNode());
    }


    @SecuredAction
    public Result myOrder() {

        User user = SessionUtils.currentUser();
        System.out.println("username" + user.getUserName());

        return ok(myOrder.render(user));

    }

    public Result about() {

        return ok(about.render());

    }


}