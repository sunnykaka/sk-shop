package controllers.api;

import api.response.user.LoginResult;
import base.BaseTest;
import common.exceptions.ErrorCode;
import common.utils.JsonUtils;
import controllers.api.user.LoginApiControllerTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import play.mvc.Http;
import play.mvc.Result;
import services.api.user.UserTokenProvider;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static play.test.Helpers.*;


/**
 * Created by liubin on 15-4-2.
 */
public class TestApiControllerTest extends BaseTest {

    @Test
    public void testRequestPublicResourceSuccess() throws Exception {

        Http.RequestBuilder request = new Http.RequestBuilder().method(POST).uri(routes.TestApiController.publicResource().url());
        Result result = routeWithExceptionHandle(request);
        assertThat(result.status(), is(OK));
        Map<String, String> map = JsonUtils.json2Object(contentAsString(result), Map.class);
        assertThat(map.get("key1"), is("value1"));
        assertThat(map.get("key2"), is("value2"));

    }

    @Test
    public void testRequestProtectedResourceError() throws Exception {

        Http.RequestBuilder request = new Http.RequestBuilder().
                method(POST).
                uri(routes.TestApiController.protectedResource().url());
        Result result = routeWithExceptionHandle(request);
        assertResultAsError(result, ErrorCode.InvalidAccessToken);

        Map<String, String> params = new HashMap<>();
        params.put(UserTokenProvider.ACCESS_TOKEN_KEY, "123456");
        request = new Http.RequestBuilder().
                method(POST).
                uri(routes.TestApiController.protectedResource().url()).
                bodyForm(params);
        result = routeWithExceptionHandle(request);
        assertResultAsError(result, ErrorCode.InvalidAccessToken);

    }

    @Test
    public void testRequestProtectedResourceSuccess() throws Exception {

        String phone = "1" + RandomStringUtils.randomNumeric(10);
        String username = RandomStringUtils.randomAlphabetic(10);
        String password = RandomStringUtils.randomAlphabetic(10);

        LoginApiControllerTest loginApiControllerTest = new LoginApiControllerTest();
        Result result = loginApiControllerTest.registerUser(phone, username, password);
        LoginResult loginResult = JsonUtils.json2Object(contentAsString(result), LoginResult.class);
        loginApiControllerTest.assertLoginResultValid(phone, username, loginResult);

        Map<String, String> params = new HashMap<>();
        params.put(UserTokenProvider.ACCESS_TOKEN_KEY, loginResult.getAccessToken());

        Http.RequestBuilder request = new Http.RequestBuilder().
                method(POST).
                uri(routes.TestApiController.protectedResource().url()).
                bodyForm(params);
        result = routeWithExceptionHandle(request);
        assertThat(result.status(), is(OK));
        Map<String, String> map = JsonUtils.json2Object(contentAsString(result), Map.class);
        assertThat(map.get("key1"), is("value1"));
        assertThat(map.get("key2"), is("value2"));
        assertThat(map.get("phone"), is(phone));
    }




}
