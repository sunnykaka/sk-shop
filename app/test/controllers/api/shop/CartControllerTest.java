package controllers.api.shop;

import api.response.user.UserDto;
import base.BaseTest;
import controllers.api.TestUtils;
import org.junit.Test;
import play.mvc.Http;
import play.mvc.Result;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static play.test.Helpers.*;


/**
 * Created by liubin on 15-4-2.
 */
public class CartControllerTest extends BaseTest {

    private void testAddSkuToCartAddNum(UserDto userDto) {
        Http.RequestBuilder request  = new Http.RequestBuilder().method(POST).uri(controllers.api.shop.routes.CartApiController.addSkuToCartAddNum(5651, 1).url());
        Result result = routeWithExceptionHandle(request);
        if(result.status() != OK) {
            assertThat(result.status(), is(CONFLICT));
        }
    }

    private void testAddSkuToCartReplaceNum(UserDto userDto) {
        Http.RequestBuilder request  = new Http.RequestBuilder().method(POST).uri(controllers.api.shop.routes.CartApiController.addSkuToCartReplaceNum(5651, 1).url());
        Result result = routeWithExceptionHandle(request);
        if(result.status() != OK) {
            assertThat(result.status(), is(CONFLICT));
        }
    }

    private void testGetUserCartItemNum(UserDto userDto) {
        Http.RequestBuilder request  = new Http.RequestBuilder().method(GET).uri(controllers.api.shop.routes.CartApiController.getUserCartItemNum().url());
        Result result = routeWithExceptionHandle(request);
        if(result.status() != OK) {
            assertThat(result.status(), is(CONFLICT));
        }
    }

    private void testShowCart(UserDto userDto) {
        Http.RequestBuilder request  = new Http.RequestBuilder().method(GET).uri(controllers.api.shop.routes.CartApiController.getUserCartItemNum().url());
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

    private void testToBilling(UserDto userDto) {
        Http.RequestBuilder request  = new Http.RequestBuilder().method(GET).uri(controllers.api.shop.routes.CartApiController.toBilling("47073").url());
        Result result = routeWithExceptionHandle(request);
        if(result.status() != OK) {
            assertThat(result.status(), is(CONFLICT));
        }
    }

    private void testToBillingByPromptlyPay(UserDto userDto) {
        Http.RequestBuilder request  = new Http.RequestBuilder().method(POST).uri(controllers.api.shop.routes.CartApiController.toBillingByPromptlyPay(5651,1).url());
        Result result = routeWithExceptionHandle(request);
        if(result.status() != OK) {
            assertThat(result.status(), is(CONFLICT));
        }
    }

    //有些数据如sku等会发生变化，在测试时需要手工调整一下
    @Test
    public void testAllCartMethod() throws Exception {
        UserDto userDto = TestUtils.login();
        if(userDto != null) {
            testAddSkuToCartAddNum(userDto);
            testAddSkuToCartReplaceNum(userDto);
            testGetUserCartItemNum(userDto);
            testShowCart(userDto);
            testDeleteCartItem(userDto);
            testToBilling(userDto);
            testToBillingByPromptlyPay(userDto);
        }
    }
}
