package controllers.activity;

import org.springframework.beans.factory.annotation.Autowired;
import play.mvc.Controller;
import play.mvc.Result;
import productcenter.models.Product;
import productcenter.models.StockKeepingUnit;
import productcenter.services.ProductService;
import productcenter.services.SkuAndStorageService;
import services.CmsService;
import views.html.activity.bianxingji;
import views.html.activity.member;
import views.html.activity.fashion;
import views.html.activity.loukong;
import views.html.activity.federico;

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

    /**
     * 提交订单-选择支付方式(生成订单)
     * @return
     */
    public Result toMember() {
        return ok(member.render());
    }

    /**
     * 变形记活动
     *
     * @return
     */
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

    public Result fashion() {

        final int[] activityProductId = {385,403,405,425,421,21,27,117,133,251,115,233,441,169,239,389,443,387,391,395,399,401};

        Map<Integer, String> mapName = new HashMap<>();
        Map<Integer, String> mapPrice = new HashMap<>();
        for(int productId:activityProductId){
            Product product = productService.getProductById(productId);

            if(null == product){
                mapPrice.put(productId,null);
                mapName.put(productId,null);
            }else{
                //根据判断是否是首发，当前价格要现算
                mapName.put(productId,product.getName());
                StockKeepingUnit stockKeepingUnit = skuSeervice.querySkuByProductIdPriceSmall(productId);
                if(null == stockKeepingUnit){
                    mapPrice.put(productId,null);
                }else{
                    boolean isFirstPublish = cmsService.onFirstPublish(product.getId());
                    if(isFirstPublish) {
                        mapPrice.put(productId,getPositivePrice(stockKeepingUnit.getPrice().toString()));
                    } else {
                        mapPrice.put(productId,getPositivePrice(stockKeepingUnit.getMarketPrice().toString()));
                    }
                }

            }
        }

        return ok(fashion.render(mapName,mapPrice));
    }

    public Result loukong() {

        final int[] activityProductId = {119,69,241,257,243,105,135,99};

        Map<Integer, String> mapName = new HashMap<>();
        Map<Integer, String> mapPrice = new HashMap<>();
        for(int productId:activityProductId){
            Product product = productService.getProductById(productId);

            if(null == product){
                mapPrice.put(productId,null);
                mapName.put(productId,null);
            }else{
                //根据判断是否是首发，当前价格要现算
                mapName.put(productId,product.getName());
                StockKeepingUnit stockKeepingUnit = skuSeervice.querySkuByProductIdPriceSmall(productId);
                if(null == stockKeepingUnit){
                    mapPrice.put(productId,null);
                }else{
                    boolean isFirstPublish = cmsService.onFirstPublish(product.getId());
                    if(isFirstPublish) {
                        mapPrice.put(productId,getPositivePrice(stockKeepingUnit.getPrice().toString()));
                    } else {
                        mapPrice.put(productId,getPositivePrice(stockKeepingUnit.getMarketPrice().toString()));
                    }
                }

            }
        }

        return ok(loukong.render(mapName, mapPrice));
    }

    public Result federico() {

        final int[] activityProductId = {69,27,15,53,21,11,63,57,67};

        Map<Integer, String> mapName = new HashMap<>();
        Map<Integer, String> mapPrice = new HashMap<>();
        for(int productId:activityProductId){
            Product product = productService.getProductById(productId);

            if(null == product){
                mapPrice.put(productId,null);
                mapName.put(productId,null);
            }else{
                //根据判断是否是首发，当前价格要现算
                mapName.put(productId,product.getName());
                StockKeepingUnit stockKeepingUnit = skuSeervice.querySkuByProductIdPriceSmall(productId);
                if(null == stockKeepingUnit){
                    mapPrice.put(productId,null);
                }else{
                    boolean isFirstPublish = cmsService.onFirstPublish(product.getId());
                    if(isFirstPublish) {
                        mapPrice.put(productId,getPositivePrice(stockKeepingUnit.getPrice().toString()));
                    } else {
                        mapPrice.put(productId,getPositivePrice(stockKeepingUnit.getMarketPrice().toString()));
                    }
                }

            }
        }

        return ok(federico.render(mapName, mapPrice));
    }

    private String getPositivePrice(String str){
        return str.split("\\.")[0];
    }

}