package controllers.api.shop;

import api.response.user.LoginResult;
import base.BaseTest;
import common.utils.JsonUtils;
import controllers.api.user.LoginApiControllerTest;
import org.junit.Test;
import play.mvc.Http;
import play.mvc.Result;

import static play.mvc.Http.Status.OK;
import static play.test.Helpers.POST;
import static play.test.Helpers.contentAsString;

/**
 * Created by lidujun on 15-9-1.
 */
public class OrderAndPayApiControllerTest extends BaseTest {
    //有些数据如sku等会发生变化，在测试时需要手工调整一下
    @Test
    public void testSubmitToPay() throws Exception { //(boolean isPromptlyPay, String selItems, int addressId, String payOrg)
        Result result = new LoginApiControllerTest().login("1234567890", "1234567890");
        if(result.status() == OK) {
            LoginResult loginResult = JsonUtils.json2Object(contentAsString(result), LoginResult.class);
            //(boolean isPromptlyPay, String selItems, int addressId, String payOrg)
            Http.RequestBuilder request  = new Http.RequestBuilder().method(POST).uri(controllers.api.shop.routes.OrderAndPayApiController.submitToPay(false, "47772,47773", 96, "Alipay").url());
            result = routeWithExceptionHandle(request);
            if(result.status() != OK) {
                //assertThat(result.status(), is(CONFLICT));
            }
        }
    }
}
