package controllers.product;

import common.exceptions.AppBusinessException;
import common.utils.JsonResult;
import common.utils.page.Page;
import common.utils.page.PageFactory;
import dtos.ProductDetail;
import ordercenter.models.Valuation;
import ordercenter.services.ValuationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import play.mvc.Controller;
import play.mvc.Result;
import productcenter.services.SeoService;
import services.product.ProductDetailService;
import utils.Global;
import views.html.product.detail;

import java.util.Optional;

@org.springframework.stereotype.Controller
public class ProductController extends Controller {

    @Autowired
    ProductDetailService productDetailService;

    @Autowired
    ValuationService valuationService;

    @Autowired
    SeoService seoService;


    public Result detail(String id) {

        String[] array = id.split("-");
        Integer productId = null;
        Integer skuId = null;
        if(!StringUtils.isNumeric(array[0])) {
            return badRequest();
        } else {
            productId = Integer.parseInt(array[0]);
        }
        if(array.length > 1) {
            if(!StringUtils.isNumeric(array[1])) {
                return badRequest();
            } else {
                skuId = Integer.parseInt(array[1]);
            }
        }

        ProductDetail productDetail = productDetailService.showDetail(productId, skuId);
        if(productDetail == null) {
            return Global.show404();
        }
        productDetail.setSeo(seoService.getProductSeo(productDetail.getProduct()));

        return ok(detail.render(productDetail));
    }

    public Result valuations(Integer productId, Integer point) {

        Page<Valuation> page = PageFactory.getPage(request());

        valuationService.findByProduct(Optional.of(page), productId, point);

        return ok(new JsonResult(true, null, page).toNode());
    }



}