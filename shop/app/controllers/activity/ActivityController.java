package controllers.activity;

import common.utils.Money;
import org.springframework.beans.factory.annotation.Autowired;
import play.mvc.Controller;
import play.mvc.Result;
import productcenter.models.Product;
import productcenter.models.StockKeepingUnit;
import productcenter.services.ProductService;
import productcenter.services.SkuAndStorageService;
import views.html.activity.bianxingji;
import views.html.activity.member;
import views.html.activity.soap;
import views.html.activity.fashion;
import views.html.activity.loukong;
import views.html.activity.federico;
import views.html.activity.louise;
import views.html.activity.earlyAutumn;

import java.util.HashMap;
import java.util.Map;

/**
 * 送货地址管理
 */
@org.springframework.stereotype.Controller
public class ActivityController extends Controller {

    private static final String MAP_KEY_NAME = "name";
    private static final String MAP_KEY_PRICE = "price";

    /** 变型记 */
    protected static final int[] BIANXINGJI_PRODUCT_IDS = {483, 217, 119, 407, 141, 215, 255, 147, 249, 127, 113, 123, 237, 115, 257, 107, 259, 111, 175, 155, 475, 165, 171, 471};
    /** 5件最具风格单品，轻松掌握时尚流行 */
    protected static final int[] FASHION_PRODUCT_IDS = {385, 403, 405, 425, 421, 21, 27, 117, 133, 251, 115, 233, 441, 169, 239, 389, 443, 387, 391, 395, 399, 401};
    /** 镂空美学 */
    protected static final int[] LOUKONG_PRODUCT_IDS = {119, 69, 241, 257, 243, 105, 135, 99};
    /** 酷感&硬朗 */
    protected static final int[] FEDERICO_PRODUCT_IDS = {69, 27, 15, 53, 21, 11, 63, 57, 67};
    /** 用浪漫寻觅小确幸 */
    protected static final int[] LOUISE_PRODUCT_IDS = {387, 546, 541, 341, 345, 365, 369, 553, 537, 531, 373, 554, 407, 399, 547, 363, 557, 556, 539, 411, 532};
    /** 静美如秋，吸睛新品 */
    protected static final int[] EARLYAUTUMN_PRODUCT_IDS = {569, 229, 573, 133, 574, 27, 225, 231, 223};

    @Autowired
    private ProductService productService;

    @Autowired
    private SkuAndStorageService skuService;

    /**
     * 提交订单-选择支付方式(生成订单)
     *
     * @return
     */

    public Result toMember() {
        return ok(member.render());
    }


    public Result toSoap() {
        return ok(soap.render());
    }

    /**
     * 变形记活动
     *
     * @return
     */
    public Result bianxingji() {

        Map<String,Map<Integer,String>> map = activityMap(BIANXINGJI_PRODUCT_IDS);

        return ok(bianxingji.render(map.get(MAP_KEY_PRICE)));
    }

    public Result fashion() {

        Map<String,Map<Integer,String>> map = activityMap(FASHION_PRODUCT_IDS);

        return ok(fashion.render(map.get(MAP_KEY_NAME), map.get(MAP_KEY_PRICE)));
    }

    public Result loukong() {

        Map<String,Map<Integer,String>> map = activityMap(LOUKONG_PRODUCT_IDS);

        return ok(loukong.render(map.get(MAP_KEY_NAME), map.get(MAP_KEY_PRICE)));
    }

    public Result federico() {

        Map<String,Map<Integer,String>> map = activityMap(FEDERICO_PRODUCT_IDS);

        return ok(federico.render(map.get(MAP_KEY_NAME), map.get(MAP_KEY_PRICE)));

    }

    public Result louise() {

        Map<String,Map<Integer,String>> map = activityMap(LOUISE_PRODUCT_IDS);

        return ok(louise.render(map.get(MAP_KEY_NAME), map.get(MAP_KEY_PRICE)));
    }

    public Result earlyAutumn() {

        Map<String,Map<Integer,String>> map = activityMap(EARLYAUTUMN_PRODUCT_IDS);

        return ok(earlyAutumn.render(map.get(MAP_KEY_NAME), map.get(MAP_KEY_PRICE)));
    }

    /**
     * 获取商品名字列表、价格列表 (返回取key中的‘name’，‘price’)
     *
     * @param activityProductId 活动id
     * @return map
     */
    public Map<String,Map<Integer,String>> activityMap(int[] activityProductId){
        Map<String,Map<Integer,String>> map = new HashMap<>();

        Map<Integer, String> mapName = new HashMap<>();
        Map<Integer, String> mapPrice = new HashMap<>();

        for (int productId : activityProductId) {
            Product product = productService.getProductById(productId);

            if (null == product) {
                mapName.put(productId, null);
                mapPrice.put(productId, null);
            } else {
                //根据判断是否是首发，当前价格要现算
                mapName.put(productId, product.getName());
                StockKeepingUnit sku = skuService.querySkuByProductIdPriceSmall(productId);
                Money money = skuService.getSkuCurrentPrice(sku);
                mapPrice.put(productId, money.equals(Money.valueOf(0)) ? null : money.toString());
            }
        }

        map.put(MAP_KEY_NAME, mapName);
        map.put(MAP_KEY_PRICE, mapPrice);
        return map;

    }

}