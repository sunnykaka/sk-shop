package controllers.activity;

import common.utils.Money;
import org.springframework.beans.factory.annotation.Autowired;
import play.mvc.Controller;
import play.mvc.Result;
import productcenter.models.Product;
import productcenter.models.StockKeepingUnit;
import productcenter.services.ProductService;
import productcenter.services.SkuAndStorageService;
import services.CmsService;
import views.html.activity.bianxingji;

import java.util.HashMap;
import java.util.Map;

/**
 * 送货地址管理
 */
@org.springframework.stereotype.Controller
public class ActivityController extends Controller {

    @Autowired
    private ProductService productService;

    @Autowired
    private CmsService cmsService;

    @Autowired
    private SkuAndStorageService skuSeervice;

    public Result bianxingji() {

        final int[] activityProductId = {483,217,119,407,141,215,255,147,249,127,113,123,237,115,257,107,259,111,175,155,475,165,171,471};

        Map<Integer,String> map = new HashMap<>();
        for(int productId:activityProductId){
            Product product = productService.getProductById(productId);

            if(null == product){
                map.put(productId,null);
            }else{
                //根据判断是否是首发，当前价格要现算
                StockKeepingUnit stockKeepingUnit = skuSeervice.querySkuByProductIdPriceSmall(productId);
                if(null == stockKeepingUnit){
                    map.put(productId,null);
                }else{
                    boolean isFirstPublish = cmsService.onFirstPublish(product.getId());
                    if(isFirstPublish) {
                        map.put(productId, getPositivePrice(stockKeepingUnit.getPrice().toString()));
                    } else {
                        map.put(productId, getPositivePrice(stockKeepingUnit.getMarketPrice().toString()));
                    }
                }

            }
        }

        return ok(bianxingji.render(map));
    }

    private String getPositivePrice(String str){
        return str.split("\\.")[0];
    }

}