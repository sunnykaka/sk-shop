package controllers.api.shop;

import api.response.product.DesignerSizeDto;
import api.response.product.ProductDetailDto;
import api.response.product.ProductDto;
import api.response.user.DesignerDto;
import api.response.user.LoginResult;
import api.response.user.UserDto;
import base.BaseTest;
import com.google.common.collect.Lists;
import common.utils.JsonUtils;
import common.utils.UrlUtils;
import controllers.api.TestUtils;
import org.junit.Test;
import play.mvc.Http;
import play.mvc.Result;
import productcenter.dtos.SkuCandidate;
import productcenter.dtos.SkuInfo;
import productcenter.models.Product;
import productcenter.services.ProductTestDataService;
import services.api.user.UserTokenProvider;
import utils.Global;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static play.test.Helpers.*;


/**
 * Created by liubin on 15-4-2.
 */
public class CartControllerTest extends BaseTest {

    private void testAddSkuToCartAddNum(LoginResult loginResult) {

        ProductTestDataService productTestDataService = Global.ctx.getBean(ProductTestDataService.class);
        Product initProduct = productTestDataService.initProduct();

        SkuInfo paramSku = doInTransactionWithGeneralDao(generalDao -> {
            Product product = generalDao.get(Product.class, initProduct.getId());

            Http.RequestBuilder request = new Http.RequestBuilder().method(GET).uri(controllers.api.product.routes.ProductApiController.detail(String.valueOf(product.getId())).url());
            Result result = routeWithExceptionHandle(request);
            assertThat(result.status(), is(OK));

            ProductDetailDto productDetailDto = JsonUtils.json2Object(contentAsString(result), ProductDetailDto.class);

            ProductDto productDto = productDetailDto.getProduct();
            assertThat(productDto, notNullValue());
            assertThat(productDto.getName(), is(product.getName()));

            assertThat(productDto.getTagType(), is(product.getTagType()));
            assertThat(productDto.getDescription(), is(product.getDescription()));
            DesignerDto designerDto = productDto.getDesigner();
            assertThat(designerDto, notNullValue());
            assertThat(designerDto.getName(), is(product.getCustomer().getName()));
            assertThat(designerDto.getId(), is(product.getCustomer().getId()));

            DesignerSizeDto designerSizeDto = productDetailDto.getDesignerSize();
            assertThat(designerSizeDto, notNullValue());
            assertThat(designerSizeDto.getId(), is(product.getDesignerSizeId()));

            SkuInfo defaultSku = productDetailDto.getDefaultSku();
            assertThat(defaultSku, notNullValue());

            List<SkuCandidate> skuCandidateList = productDetailDto.getSkuCandidateList();
            assertThat(skuCandidateList, notNullValue());
            assertThat(skuCandidateList.size() > 0, is(true));

            Map<String, SkuInfo> skuMap = productDetailDto.getSkuMap();
            assertThat(skuMap, notNullValue());
            assertThat(skuMap.size() > 0, is(true));

            return defaultSku;
        });


        Map<String, String> params = new HashMap();
        params.put(UserTokenProvider.ACCESS_TOKEN_KEY, loginResult.getAccessToken());

        //addSkuToCartAddNum
        Http.RequestBuilder request = new Http.RequestBuilder().
                method(POST).
                uri(controllers.api.shop.routes.CartApiController.addSkuToCartAddNum(paramSku.getSkuId(), 1).url()).
                bodyForm(params);
        Result result = routeWithExceptionHandle(request);
        if(result.status() != OK) {
            assertThat(result.status(), is(CONFLICT));
        } else {
            Map<String, Integer> map = JsonUtils.json2Object(contentAsString(result), Map.class);
            assertThat(map.get("itemTotalNum"), is(1));
        }

        //addSkuToCartReplaceNum
        request = new Http.RequestBuilder().
                method(POST).
                uri(controllers.api.shop.routes.CartApiController.addSkuToCartReplaceNum(paramSku.getSkuId(), 1).url()).
                bodyForm(params);
        result = routeWithExceptionHandle(request);
        if(result.status() != OK) {
            assertThat(result.status(), is(CONFLICT));
        } else {
            Map<String, Integer> map = JsonUtils.json2Object(contentAsString(result), Map.class);
            assertThat(map.get("itemTotalNum"), is(1));
        }
    }

    private void testGetUserCartItemNum(LoginResult loginResult) {
        Map<String, List<String>> params = new HashMap<>();
        params.put(UserTokenProvider.ACCESS_TOKEN_KEY, Lists.newArrayList(loginResult.getAccessToken()));
        Http.RequestBuilder request = new Http.RequestBuilder().
                method(GET).
                uri(controllers.api.shop.routes.CartApiController.getUserCartItemNum().url() + "?" + UrlUtils.buildQueryString(params));
        Result result = routeWithExceptionHandle(request);
        if(result.status() != OK) {
            assertThat(result.status(), is(CONFLICT));
        } else {
            Map<String, Integer> map = JsonUtils.json2Object(contentAsString(result), Map.class);
            assertThat(map.get("totalNum"), is(1));
        }
    }

    private void testShowCart(LoginResult loginResult) {
        Map<String, List<String>> params = new HashMap<>();
        params.put(UserTokenProvider.ACCESS_TOKEN_KEY, Lists.newArrayList(loginResult.getAccessToken()));
        Http.RequestBuilder request = new Http.RequestBuilder().
                method(GET).
                uri(controllers.api.shop.routes.CartApiController.showCart().url() + "?" + UrlUtils.buildQueryString(params));
        Result result = routeWithExceptionHandle(request);
        if(result.status() != OK) {
            assertThat(result.status(), is(CONFLICT));
        } else {
            Map<String, Integer> map = JsonUtils.json2Object(contentAsString(result), Map.class);
            assertThat(map.size(), is(3));


        }
    }

    private void testToBilling(UserDto userDto) {
        Http.RequestBuilder request  = new Http.RequestBuilder().method(GET).uri(controllers.api.shop.routes.CartApiController.toBilling("47073").url());
        Result result = routeWithExceptionHandle(request);
        if(result.status() != OK) {
            assertThat(result.status(), is(CONFLICT));
        }
    }

    private void testToBillingByPromptlyPay(UserDto userDto) {
        Http.RequestBuilder request  = new Http.RequestBuilder().method(POST).uri(controllers.api.shop.routes.CartApiController.toBillingByPromptlyPay(5651, 1).url());
        Result result = routeWithExceptionHandle(request);
        if(result.status() != OK) {
            assertThat(result.status(), is(CONFLICT));
        }
    }

    private void testDeleteCartItem(UserDto userDto) {
        Http.RequestBuilder request  = new Http.RequestBuilder().method(DELETE).uri(controllers.api.shop.routes.CartApiController.deleteCartItem(47073).url());
        Result result = routeWithExceptionHandle(request);
        if(result.status() != OK) {
            assertThat(result.status(), is(CONFLICT));
        }
    }

    //有些数据如sku等会发生变化，在测试时需要手工调整一下
    @Test
    public void testAllCartMethod() throws Exception {
        LoginResult loginResult = TestUtils.createUserAndLogin();
        if(loginResult != null) {
            testAddSkuToCartAddNum(loginResult);
            testGetUserCartItemNum(loginResult);
            testShowCart(loginResult);













//
//            UserDto userDto = loginResult.getUser();
//            if(userDto != null) {
//
//
//
//
//
//
//
//
////                testAddSkuToCartReplaceNum(userDto);
////                testGetUserCartItemNum(userDto);
////                testShowCart(userDto);
////                testDeleteCartItem(userDto);
////                testToBilling(userDto);
////                testToBillingByPromptlyPay(userDto);
//            }
        }
    }
}
