package controllers.shop;

import base.BaseTest;
import common.utils.JsonResult;
import controllers.user.LoginTest;
import ordercenter.models.Cart;
import ordercenter.models.CartItem;
import ordercenter.services.CartService;
import org.junit.Test;
import play.Logger;
import play.mvc.Http;
import play.mvc.Result;
import productcenter.models.Product;
import productcenter.models.StockKeepingUnit;
import productcenter.services.ProductTestDataService;
import productcenter.services.SkuAndStorageService;
import utils.Global;

import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static play.test.Helpers.*;


/**
 * Created by liubin on 15-4-2.
 */
public class CartControllerTest extends BaseTest implements LoginTest {

    @Test
    public void testGetUserCartItemNum() {

        Integer userId = mockUser();

        Http.RequestBuilder request = new Http.RequestBuilder().method(GET).uri(routes.CartController.getUserCartItemNum().url());
        request = wrapLoginInfo(request, userId);
        Result result = route(request);
        Logger.debug(" CartController.getUserCartItemNum result: " + contentAsString(result));
        assertThat(result.status(), is(OK));
        JsonResult jsonResult = JsonResult.fromJson(contentAsString(result));
        assertThat(jsonResult.getResult(), is(true));
        assertThat(jsonResult.getData(), is(0));
        assertThat(jsonResult.getMessage(), is(notNullValue()));

    }

    @Test
    public void testAddSkuToCartAddOrReplaceNum() {

        ProductTestDataService productTestDataService = Global.ctx.getBean(ProductTestDataService.class);
        SkuAndStorageService skuAndStorageService = Global.ctx.getBean(SkuAndStorageService.class);

        Product product = productTestDataService.initProduct();
        List<StockKeepingUnit> stockKeepingUnits = skuAndStorageService.querySkuListByProductId(product.getId());
        StockKeepingUnit sku = stockKeepingUnits.get(0);
        int stockQuantity = skuAndStorageService.getSkuStorage(sku.getId()).getStockQuantity();

        Integer userId = mockUser();

        //测试addSkuToCartAddNum
        JsonResult jsonResult = testAddSkuToCartAddNum(sku, stockQuantity, userId);
        assertThat(jsonResult.getResult(), is(true));
        assertThat(jsonResult.getData(), is(notNullValue()));
        assertThat(jsonResult.getMessage(), is(notNullValue()));
        Map data = (Map) jsonResult.getData();
        assertThat(data.get("itemTotalNum"), is(stockQuantity));

        jsonResult = testAddSkuToCartAddNum(sku, stockQuantity, userId);
        assertThat(jsonResult.getResult(), is(false));
        assertThat(jsonResult.getData(), is(notNullValue()));
        assertThat(jsonResult.getMessage(), is(notNullValue()));

        data = (Map) jsonResult.getData();
        //这里返回的最大购买数量等于库存，因为第二次请求添加stockQuantity个产品到购物车，购物车产品数量等于2 * stockQuantity, 超过最大购买数量
        assertThat(data.get("maxCanBuyNum"), is(stockQuantity));


        //测试addSkuToCartReplaceNum
        jsonResult = testReplaceSkuToCartAddNum(sku, stockQuantity, userId);
        assertThat(jsonResult.getResult(), is(true));
        assertThat(jsonResult.getData(), is(notNullValue()));
        assertThat(jsonResult.getMessage(), is(notNullValue()));


        jsonResult = testReplaceSkuToCartAddNum(sku, stockQuantity + 1, userId);
        assertThat(jsonResult.getResult(), is(false));
        assertThat(jsonResult.getData(), is(notNullValue()));
        assertThat(jsonResult.getMessage(), is(notNullValue()));

        data = (Map) jsonResult.getData();
        assertThat(data.get("maxCanBuyNum"), is(stockQuantity));


    }

    @Test
    public void testSelCartItemProcess() {

        ProductTestDataService productTestDataService = Global.ctx.getBean(ProductTestDataService.class);
        SkuAndStorageService skuAndStorageService = Global.ctx.getBean(SkuAndStorageService.class);

        Product product = productTestDataService.initProduct();
        List<StockKeepingUnit> stockKeepingUnits = skuAndStorageService.querySkuListByProductId(product.getId());
        StockKeepingUnit sku = stockKeepingUnits.get(0);
        int stockQuantity = skuAndStorageService.getSkuStorage(sku.getId()).getStockQuantity();

        Integer userId = mockUser();

        //加入产品到购物车
        JsonResult jsonResult = testAddSkuToCartAddNum(sku, stockQuantity, userId);
        assertThat(jsonResult.getResult(), is(true));
        assertThat(jsonResult.getData(), is(notNullValue()));
        assertThat(jsonResult.getMessage(), is(notNullValue()));
        Map data = (Map) jsonResult.getData();
        assertThat(data.get("itemTotalNum"), is(stockQuantity));

        Integer cartItemId = doInTransactionWithGeneralDao(generalDao -> {
            CartService cartService = Global.ctx.getBean(CartService.class);
            Cart cart = cartService.getCartByUserId(userId);
            List<CartItem> cartItems = cartService.queryCarItemsByCartId(cart.getId());
            return cartItems.get(0).getId();
        });

        //请求结算接口
        Http.RequestBuilder request = new Http.RequestBuilder().method(GET).uri(
                routes.CartController.selCartItemProcess(String.valueOf(cartItemId)).url());
        request = wrapLoginInfo(request, userId);
        Result result = route(request);
        Logger.debug(" CartController.selCartItemProcess result: " + contentAsString(result));
        assertThat(result.status(), is(OK));
        jsonResult = JsonResult.fromJson(contentAsString(result));
        assertThat(jsonResult.getResult(), is(true));

    }

    @Test
    public void testVerifyPromptlyPayData() {

        ProductTestDataService productTestDataService = Global.ctx.getBean(ProductTestDataService.class);
        SkuAndStorageService skuAndStorageService = Global.ctx.getBean(SkuAndStorageService.class);

        Product product = productTestDataService.initProduct();
        List<StockKeepingUnit> stockKeepingUnits = skuAndStorageService.querySkuListByProductId(product.getId());
        StockKeepingUnit sku = stockKeepingUnits.get(0);
        int stockQuantity = skuAndStorageService.getSkuStorage(sku.getId()).getStockQuantity();

        Integer userId = mockUser();

        //请求直接购买验证接口
        Http.RequestBuilder request = new Http.RequestBuilder().method(GET).uri(
                routes.CartController.verifyPromptlyPayData(sku.getId(), stockQuantity).url());
        request = wrapLoginInfo(request, userId);
        Result result = route(request);
        Logger.debug(" CartController.verifyPromptlyPayData result: " + contentAsString(result));
        assertThat(result.status(), is(OK));
        JsonResult jsonResult = JsonResult.fromJson(contentAsString(result));
        assertThat(jsonResult.getResult(), is(true));

    }

    private JsonResult testAddSkuToCartAddNum(StockKeepingUnit sku, int stockQuantity, Integer userId) {
        Http.RequestBuilder request = new Http.RequestBuilder().method(GET).uri(
                routes.CartController.addSkuToCartAddNum(sku.getId(), stockQuantity).url());
        request = wrapLoginInfo(request, userId);
        Result result = route(request);
        Logger.debug(" CartController.addSkuToCartAddNum result: " + contentAsString(result));
        assertThat(result.status(), is(OK));
        return JsonResult.fromJson(contentAsString(result));
    }

    private JsonResult testReplaceSkuToCartAddNum(StockKeepingUnit sku, int stockQuantity, Integer userId) {
        Http.RequestBuilder request = new Http.RequestBuilder().method(GET).uri(
                routes.CartController.addSkuToCartReplaceNum(sku.getId(), stockQuantity).url());
        request = wrapLoginInfo(request, userId);
        Result result = route(request);
        Logger.debug(" CartController.addSkuToCartReplaceNum result: " + contentAsString(result));
        assertThat(result.status(), is(OK));
        return JsonResult.fromJson(contentAsString(result));
    }



}
