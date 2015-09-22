package controllers.product;

import common.utils.JsonResult;
import common.utils.page.Page;
import common.utils.page.PageFactory;
import dtos.ProductDetail;
import dtos.ProductInSellList;
import ordercenter.models.Valuation;
import ordercenter.services.ValuationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import play.mvc.Controller;
import play.mvc.Result;
import productcenter.dtos.ProductQueryVO;
import productcenter.models.NavigateCategory;
import productcenter.models.Product;
import productcenter.services.ProductListService;
import productcenter.services.ProductPictureService;
import services.product.ProductDetailService;
import usercenter.utils.SessionUtils;
import utils.Global;
import views.html.product.detail;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@org.springframework.stereotype.Controller
public class ProductController extends Controller {

    @Autowired
    ProductDetailService productDetailService;

    @Autowired
    ValuationService valuationService;

    @Autowired
    private ProductListService productListService;

    @Autowired
    private ProductPictureService productPictureService;


    /**
     * 商品列表页的查询
     *
     * @param pageNo
     * @param navId
     * @return
     */
    public Result list(int pageNo, int navId,int st,String status) {
        Page<Product> page = new Page(pageNo, 12);
        ProductQueryVO queryVO = new ProductQueryVO();
        queryVO.setNavigateId(navId);
        queryVO.setSt(st);
        queryVO.setSaleStatus(status);
        List<Product> products = productListService.productList(Optional.of(page), queryVO);


        /**
         *组装产品相关的信息，包括：产品信息，产品主图，产品如果是首发就活取首发信息
         */
        ProductInSellList.Builder builder = ProductInSellList.Builder.getInstance();
        List<ProductInSellList> productList = products.stream().map(prod -> builder.buildProdct(prod)).collect(toList());


        Page<ProductInSellList> pageResult = new Page(pageNo, 12);
        pageResult.setTotalCount(page.getTotalCount());
        pageResult.setResult(productList);
        List<NavigateCategory> navigateCategories = productListService.navigatesBelow(-1);
        return ok(views.html.product.sell_list.render(navigateCategories, pageResult,queryVO));
    }

    public Result detail(String id) {

        String[] array = id.split("-");
        Integer productId = null;
//        Integer skuId = null;
        if(!StringUtils.isNumeric(array[0])) {
            return Global.show400();
        } else {
            productId = Integer.parseInt(array[0]);
        }
//        if(array.length > 1) {
//            if(!StringUtils.isNumeric(array[1])) {
//                return Global.show400();
//            } else {
//                skuId = Integer.parseInt(array[1]);
//            }
//        }

        ProductDetail productDetail = productDetailService.showDetail(productId, null, SessionUtils.currentUser());
        if (productDetail == null) {
            return Global.show404();
        }

        return ok(detail.render(productDetail,(int)(Math.random()*10)%6+1));
    }

    public Result valuations(Integer productId, Integer point) {

        Page<Valuation> page = PageFactory.getPage(request());

        valuationService.findByProduct(Optional.of(page), productId, point);
        for(Valuation valuation:page.getResult()){
            valuation.setUserName(common.utils.StringUtils.getSecurityName(valuation.getUserName()));
        }

        return ok(new JsonResult(true, null, page).toNode());
    }



}