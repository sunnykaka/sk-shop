package controllers.product;

import common.exceptions.AppBusinessException;
import common.utils.JsonResult;
import common.utils.page.Page;
import common.utils.page.PageFactory;
import dtos.ProductDetail;
import ordercenter.models.Valuation;
import ordercenter.services.ValuationService;
import org.springframework.beans.factory.annotation.Autowired;
import play.mvc.Controller;
import play.mvc.Result;
import services.product.ProductDetailService;
//import views.html.product.detail;

import java.util.Optional;

@org.springframework.stereotype.Controller
public class ProductController extends Controller {

    @Autowired
    ProductDetailService productDetailService;

    @Autowired
    ValuationService valuationService;


    public Result detail(Integer productId) {

//        ProductDetail productDetail = productDetailService.showDetail(productId);
//        if(productDetail == null) {
//            throw new AppBusinessException("您请求的产品没有找到, 请看看其他的吧");
//        }
//
//        return ok(detail.render(productDetail));
        return ok("");
    }

    public Result valuations(Integer productId, Integer point) {

        Page<Valuation> page = PageFactory.getPage(request());

        valuationService.findByProduct(Optional.of(page), productId, point);

        return ok(new JsonResult(true, null, page).toNode());
    }



}