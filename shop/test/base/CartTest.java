package base;

import common.utils.DateUtils;
import common.utils.JsonResult;
import controllers.shop.routes;
import controllers.user.LoginTest;
import ordercenter.models.Cart;
import ordercenter.models.CartItem;
import ordercenter.services.CartService;
import org.apache.commons.lang3.RandomStringUtils;
import play.Logger;
import play.mvc.Http;
import play.mvc.Result;
import productcenter.models.StockKeepingUnit;
import usercenter.models.address.Address;
import usercenter.services.AddressService;
import utils.Global;

import java.util.Map;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.GET;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.route;

/**
 * Created by liubin on 15-10-22.
 */
public interface CartTest extends LoginTest {


    default Integer addSkuToCart(StockKeepingUnit sku, int number, Integer userId) {
        JsonResult jsonResult = testAddSkuToCartAddNum(sku, number, userId);
        assertThat(jsonResult.getResult(), is(true));
        assertThat(jsonResult.getData(), is(notNullValue()));
        assertThat(jsonResult.getMessage(), is(notNullValue()));
        Map data = (Map) jsonResult.getData();
        assertThat((Integer)data.get("itemTotalNum") >= number, is(true));

        return doInSingleSession(generalDao -> {
            CartService cartService = Global.ctx.getBean(CartService.class);
            Cart cart = cartService.getCartByUserId(userId);
            Optional<CartItem> cartItem = cart.getNotDeleteCartItemList().stream().
                    filter(x -> x.getSkuId() == sku.getId()).findFirst();
            assertThat(cartItem.isPresent(), is(true));
            return cartItem.get().getId();
        });
    }

    default JsonResult testAddSkuToCartAddNum(StockKeepingUnit sku, int stockQuantity, Integer userId) {
        Http.RequestBuilder request = new Http.RequestBuilder().method(GET).uri(
                routes.CartController.addSkuToCartAddNum(sku.getId(), stockQuantity).url());
        request = wrapLoginInfo(request, userId);
        Result result = route(request);
        Logger.debug(" CartController.addSkuToCartAddNum result: " + contentAsString(result));
        assertThat(result.status(), is(OK));
        return JsonResult.fromJson(contentAsString(result));
    }

    default JsonResult testReplaceSkuToCartAddNum(StockKeepingUnit sku, int stockQuantity, Integer userId) {
        Http.RequestBuilder request = new Http.RequestBuilder().method(GET).uri(
                routes.CartController.addSkuToCartReplaceNum(sku.getId(), stockQuantity).url());
        request = wrapLoginInfo(request, userId);
        Result result = route(request);
        Logger.debug(" CartController.addSkuToCartReplaceNum result: " + contentAsString(result));
        assertThat(result.status(), is(OK));
        return JsonResult.fromJson(contentAsString(result));
    }

    default Address mockAddress(Integer userId) {

        AddressService addressService = Global.ctx.getBean(AddressService.class);

        Address address = new Address();
        address.setUserId(userId);
        address.setArea("南山区");
        address.setCity("深圳市");
        address.setProvince("广东");
        address.setName("约翰");
        address.setLocation("滨海大道随机" + RandomStringUtils.randomAlphabetic(8));
        address.setMobile("1" + RandomStringUtils.randomNumeric(10));
        address.setDefaultAddress(true);
        address.setCreateDate(DateUtils.current());
        address.setUpdateDate(DateUtils.current());
        address.setEmail("jork@gmail.com");

        addressService.createAddress(address);

        return address;
    }


}
