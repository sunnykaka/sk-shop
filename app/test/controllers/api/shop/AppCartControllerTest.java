package controllers.api.shop;

import api.response.shop.CartDto;
import api.response.shop.CartItemDto;
import api.response.shop.SkuDto;
import api.response.shop.SkuPropertyDto;
import api.response.user.LoginResult;
import base.BaseTest;
import com.google.common.collect.Lists;
import common.exceptions.ErrorCode;
import common.utils.JsonUtils;
import common.utils.Money;
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
import productcenter.models.SkuProperty;
import productcenter.models.SkuStorage;
import productcenter.models.StockKeepingUnit;
import productcenter.services.ProductService;
import productcenter.services.ProductTestDataService;
import productcenter.services.SkuAndStorageService;
import usercenter.models.User;
import usercenter.services.DesignerService;
import usercenter.services.UserService;
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
        Logger.debug(" AppCartController.toBilling result: " + contentAsString(result));
        assertThat(result.status(), is(OK));
        map = JsonUtils.json2Object(contentAsString(result), Map.class);
        assertThat(map.get("cart"), notNullValue());
        CartDto cartDto = JsonUtils.json2Object(JsonUtils.object2Json(map.get("cart")), CartDto.class);
        assertCartDtoMatches(cartDto, Lists.newArrayList(sku));

    }

    @Test
    public void testToBillingByPromptlyPay() {

        ProductTestDataService productTestDataService = Global.ctx.getBean(ProductTestDataService.class);
        SkuAndStorageService skuAndStorageService = Global.ctx.getBean(SkuAndStorageService.class);

        Product product = productTestDataService.initProduct();
        List<StockKeepingUnit> stockKeepingUnits = skuAndStorageService.querySkuListByProductId(product.getId());
        StockKeepingUnit sku = stockKeepingUnits.get(0);
        int stockQuantity = skuAndStorageService.getSkuStorage(sku.getId()).getStockQuantity();

        LoginResult loginResult = mockUser();


        //请求结算接口
        Http.RequestBuilder request = new Http.RequestBuilder().method(POST).uri(
                routes.AppCartController.toBillingByPromptlyPay(sku.getId(), stockQuantity).url());
        request = wrapLoginInfo(request, loginResult.getAccessToken());
        Result result = routeWithExceptionHandle(request);
        Logger.debug(" AppCartController.toBillingByPromptlyPay result: " + contentAsString(result));
        assertThat(result.status(), is(OK));
        Map<String, Object> map = JsonUtils.json2Object(contentAsString(result), Map.class);
        assertThat(map.get("cart"), notNullValue());
        CartDto cartDto = JsonUtils.json2Object(JsonUtils.object2Json(map.get("cart")), CartDto.class);
        assertCartDtoMatches(cartDto, Lists.newArrayList(sku));

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

    public static void assertCartDtoMatches(CartDto cartDto, List<StockKeepingUnit> skuList) {
        SkuAndStorageService skuAndStorageService = Global.ctx.getBean(SkuAndStorageService.class);
        ProductService productService = Global.ctx.getBean(ProductService.class);
        DesignerService designerService = Global.ctx.getBean(DesignerService.class);

        assertThat(cartDto.getCartItemList(), notNullValue());
        assertThat(cartDto.getCartItemList().size(), is(skuList.size()));
        Money totalMoney = Money.valueOf(0d);

        for(int i = 0; i < cartDto.getCartItemList().size(); i++) {
            CartItemDto cartItemDto = cartDto.getCartItemList().get(i);
            StockKeepingUnit sku = skuList.get(i);
            SkuStorage skuStorage = skuAndStorageService.getSkuStorage(sku.getId());
            Product product = productService.getProductById(sku.getProductId());

            assertThat(cartItemDto.getCustomerId(), is(product.getCustomerId()));
            assertThat(cartItemDto.getCustomerName(), is(designerService.getDesignerById(product.getCustomerId()).getName()));
            assertThat(cartItemDto.getProductId(), is(product.getId()));
            assertThat(cartItemDto.getProductName(), is(product.getName()));
            assertThat(cartItemDto.getNumber() > 0, is(true));
            assertThat(cartItemDto.getCurUnitPrice(), is(skuAndStorageService.getSkuCurrentPrice(sku.getId())));
            assertThat(cartItemDto.getTotalPrice(), is(cartItemDto.getCurUnitPrice().multiply(cartItemDto.getNumber())));
            totalMoney = totalMoney.add(cartItemDto.getTotalPrice());
            assertThat(cartItemDto.getOnline(), is(product.isOnline()));
            assertThat(cartItemDto.isSelected(), is(true));
            assertThat(cartItemDto.isDelete(), is(false));

            assertThat(cartItemDto.getStockQuantity(), is(skuStorage.getStockQuantity()));
            assertThat(cartItemDto.getTradeMaxNumber(), is(skuStorage.getTradeMaxNumber()));
            assertThat(cartItemDto.isHasStock(), is(skuStorage.getStockQuantity() > 0));

            SkuDto skuDto = cartItemDto.getSku();
            assertThat(skuDto, notNullValue());
            assertThat(skuDto.getId(), is(sku.getId()));

            List<SkuPropertyDto> skuPropertyDtoList = skuDto.getSkuProperties();
            assertThat(skuPropertyDtoList.size(), is(sku.getSkuProperties().size()));
            for (int j = 0; j < skuPropertyDtoList.size(); j++) {
                SkuPropertyDto skuPropertyDto = skuPropertyDtoList.get(j);
                SkuProperty skuProperty = sku.getSkuProperties().get(j);

                assertThat(skuPropertyDto.getPidvid(), is(skuProperty.getPidvid()));
                assertThat(skuPropertyDto.getPropertyId(), is(skuProperty.getPropertyId()));
                assertThat(skuPropertyDto.getPropertyName(), is(skuProperty.getPropertyName()));
                assertThat(skuPropertyDto.getPropertyValue(), is(skuProperty.getPropertyValue()));
                assertThat(skuPropertyDto.getSkuId(), is(skuProperty.getSkuId()));
                assertThat(skuPropertyDto.getValueId(), is(skuProperty.getValueId()));
            }

        }

        assertThat(totalMoney, is(cartDto.getTotalMoney()));
    }

}
