package controllers.api.product;

import api.response.product.ProductDetailDto;
import common.exceptions.AppBusinessException;
import common.exceptions.ErrorCode;
import common.utils.JsonUtils;
import controllers.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import play.mvc.Result;
import productcenter.services.ProductService;
import services.api.product.ProductDetailApiService;

@org.springframework.stereotype.Controller
public class ProductApiController extends BaseController {

    @Autowired
    ProductService productService;

    @Autowired
    ProductDetailApiService productDetailApiService;

    public Result detail(String id) {

        String[] array = id.split("-");
        Integer productId = null;
//        Integer skuId = null;
        if(!StringUtils.isNumeric(array[0])) {
            throw new AppBusinessException(ErrorCode.InvalidArgument, "无效的商品ID: " + array[0]);
        } else {
            productId = Integer.parseInt(array[0]);
        }
//        if(array.length > 1) {
//            if(!StringUtils.isNumeric(array[1])) {
//                throw new AppBusinessException(ErrorCode.InvalidArgument, "无效的sku ID: " + array[1]);
//            } else {
//                skuId = Integer.parseInt(array[1]);
//            }
//        }

        ProductDetailDto productDetailDto = productDetailApiService.showDetail(productId, null, this.currentUser());

        return ok(JsonUtils.object2Node(productDetailDto));
    }


}