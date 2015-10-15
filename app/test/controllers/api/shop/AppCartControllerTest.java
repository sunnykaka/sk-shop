package controllers.api.shop;

import api.response.shop.CartDto;
import api.response.shop.CartItemDto;
import api.response.user.LoginResult;
import base.BaseTest;
import common.exceptions.ErrorCode;
import common.utils.JsonUtils;
import controllers.api.user.LoginApiTest;
import ordercenter.models.Cart;
import ordercenter.models.CartItem;
import ordercenter.services.CartService;
import org.junit.Before;
import org.junit.Test;
import play.Logger;
import play.mvc.Http;
import play.mvc.Result;
import productcenter.models.Product;
import productcenter.models.StockKeepingUnit;
import productcenter.services.ProductTestDataService;
import productcenter.services.SkuAndStorageService;
import usercenter.models.User;
import usercenter.services.UserService;
import utils.Global;

import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static play.test.Helpers.*;


/**
 * Created by liubin on 15-4-2.
 */
public class AppCartControllerTest extends BaseTest implements LoginApiTest {

    private CartService cartService;
    private UserService userService;

    @Before
    public void setUp() throws Exception {
        cartService = Global.ctx.getBean(CartService.class);
        userService = Global.ctx.getBean(UserService.class);
    }

    @Test
    public void testGetUserCartItemNum() {

        LoginResult loginResult = mockUser();

        Http.RequestBuilder request = new Http.RequestBuilder().method(GET).uri(routes.AppCartController.getUserCartItemNum().url());
        request = wrapLoginInfo(request, loginResult.getAccessToken());
        Result result = routeWithExceptionHandle(request);
        Logger.debug(" CartController.getUserCartItemNum result: " + contentAsString(result));
        assertThat(result.status(), is(OK));
        Map<String, Object> map = JsonUtils.json2Object(contentAsString(result), Map.class);
        assertThat(map, notNullValue());
        assertThat(map.get("totalNum"), is(0));

    }

    @Test
    public void testAddSkuToCartAddOrReplaceNum() {

        ProductTestDataService productTestDataService = Global.ctx.getBean(ProductTestDataService.class);
        SkuAndStorageService skuAndStorageService = Global.ctx.getBean(SkuAndStorageService.class);

        Product product = productTestDataService.initProduct();
        List<StockKeepingUnit> stockKeepingUnits = skuAndStorageService.querySkuListByProductId(product.getId());
        StockKeepingUnit sku = stockKeepingUnits.get(0);
        int stockQuantity = skuAndStorageService.getSkuStorage(sku.getId()).getStockQuantity();

        LoginResult loginResult = mockUser();

        //测试addSkuToCartAddNum
        Result result = testAddSkuToCartAddNum(sku, stockQuantity, loginResult);
        assertThat(result.status(), is(OK));
        Map<String, Object> map = JsonUtils.json2Object(contentAsString(result), Map.class);
        assertThat(map, notNullValue());
        assertThat(map.get("itemTotalNum"), is(stockQuantity));

        result = testAddSkuToCartAddNum(sku, stockQuantity, loginResult);
        assertResultAsError(result, ErrorCode.Conflict);


        //测试addSkuToCartReplaceNum
        result = testReplaceSkuToCartAddNum(sku, stockQuantity, loginResult);
        assertThat(result.status(), is(OK));
        map = JsonUtils.json2Object(contentAsString(result), Map.class);
        assertThat(map, notNullValue());
        assertThat(map.get("itemTotalNum"), is(stockQuantity));

        result = testReplaceSkuToCartAddNum(sku, stockQuantity + 1, loginResult);
        assertResultAsError(result, ErrorCode.Conflict);

    }

    @Test
    public void testToBilling() {

        ProductTestDataService productTestDataService = Global.ctx.getBean(ProductTestDataService.class);
        SkuAndStorageService skuAndStorageService = Global.ctx.getBean(SkuAndStorageService.class);

        Product product = productTestDataService.initProduct();
        List<StockKeepingUnit> stockKeepingUnits = skuAndStorageService.querySkuListByProductId(product.getId());
        StockKeepingUnit sku = stockKeepingUnits.get(0);
        int stockQuantity = skuAndStorageService.getSkuStorage(sku.getId()).getStockQuantity();

        LoginResult loginResult = mockUser();

        //加入产品到购物车
        Result result = testAddSkuToCartAddNum(sku, stockQuantity, loginResult);
        assertThat(result.status(), is(OK));
        Map<String, Object> map = JsonUtils.json2Object(contentAsString(result), Map.class);
        assertThat(map, notNullValue());
        assertThat(map.get("itemTotalNum"), is(stockQuantity));

        Integer cartItemId = doInTransactionWithGeneralDao(generalDao -> {
            User user = userService.findByPhone(loginResult.getUser().getPhone());
            Cart cart = cartService.getCartByUserId(user.getId());
            List<CartItem> cartItems = cartService.queryCarItemsByCartId(cart.getId());
            return cartItems.get(0).getId();
        });

        //请求结算接口
        Http.RequestBuilder request = new Http.RequestBuilder().method(POST).uri(
                routes.AppCartController.toBilling(String.valueOf(cartItemId)).url());
        request = wrapLoginInfo(request, loginResult.getAccessToken());
        result = routeWithExceptionHandle(request);
        Logger.debug(" AppCartController.selCartItemProcess result: " + contentAsString(result));
        assertThat(result.status(), is(OK));
        map = JsonUtils.json2Object(contentAsString(result), Map.class);
        assertThat(map.get("cart"), notNullValue());
        CartDto cartDto = JsonUtils.json2Object(contentAsString(result), CartDto.class);
        assertThat(cartDto.getCartItemList(), notNullValue());
        assertThat(cartDto.getCartItemList().size(), not(0));
        for(CartItemDto cartItemDto : cartDto.getCartItemList()) {
            assertThat(cartItemDto.getCustomerId(), is(product.getCustomerId()));
            assertThat(cartItemDto.getCustomerName(), is(product.getCustomer().getName()));
            assertThat(cartItemDto.getProductId(), is(product.getId()));
            assertThat(cartItemDto.getProductName(), is(product.getName()));
//            assertThat(cartItemDto.getCurUnitPrice(), is(product.getName()));
            assertThat(cartItemDto.getNumber() > 0, is(true));
//            assertThat(cartItemDto.getTotalPrice(), is(product.getName()));

        }



    }


    private Result testAddSkuToCartAddNum(StockKeepingUnit sku, int stockQuantity, LoginResult loginResult) {
        Http.RequestBuilder request = new Http.RequestBuilder().method(POST).uri(
                routes.AppCartController.addSkuToCartAddNum(sku.getId(), stockQuantity).url());
        request = wrapLoginInfo(request, loginResult.getAccessToken());
        Result result = routeWithExceptionHandle(request);
        Logger.debug(" CartController.addSkuToCartAddNum result: " + contentAsString(result));
        return result;
    }

    private Result testReplaceSkuToCartAddNum(StockKeepingUnit sku, int stockQuantity, LoginResult loginResult) {
        Http.RequestBuilder request = new Http.RequestBuilder().method(POST).uri(
                routes.AppCartController.addSkuToCartReplaceNum(sku.getId(), stockQuantity).url());
        request = wrapLoginInfo(request, loginResult.getAccessToken());
        Result result = routeWithExceptionHandle(request);
        Logger.debug(" CartController.addSkuToCartReplaceNum result: " + contentAsString(result));
        return result;
    }



}
