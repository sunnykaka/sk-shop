package controllers.api.shop;

import api.response.user.LoginResult;
import base.BaseTest;
import common.utils.JsonUtils;
import controllers.api.user.LoginApiControllerTest;
import org.junit.Test;
import play.mvc.Http;
import play.mvc.Result;
import services.api.user.UserTokenProvider;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static play.mvc.Http.Status.CONFLICT;
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


            // (boolean isPromptlyPay, String selItems, int addressId, String payOrg)
            Map<String, String> params = new HashMap();
            params.put(UserTokenProvider.ACCESS_TOKEN_KEY, loginResult.getAccessToken());
            Http.RequestBuilder request = new Http.RequestBuilder().
                    method(POST).
                    uri(controllers.api.shop.routes.AppOrderAndPayController.submitToPay(false, "47772,47773", 96, "Alipay", "", "IOSApp").url()).
                    bodyForm(params);
            result = routeWithExceptionHandle(request);
            if(result.status() != OK) {
                assertThat(result.status(), is(CONFLICT));
            } else {
                Map<String, Object> map = JsonUtils.json2Object(contentAsString(result), Map.class);
            }
        }
    }
}
