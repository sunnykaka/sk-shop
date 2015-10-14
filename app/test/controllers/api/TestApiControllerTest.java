package controllers.api;

import api.response.user.LoginResult;
import base.BaseTest;
import base.DbTest;
import common.exceptions.ErrorCode;
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
import static play.test.Helpers.*;
import static common.utils.TestUtils.*;

/**
 * Created by liubin on 15-4-2.
 */
public class TestApiControllerTest extends BaseTest implements DbTest {

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

        UserRegisterInfo userRegisterInfo = mockUserRegisterInfo();
        String phone = userRegisterInfo.phone;
        String username = userRegisterInfo.username;
        String password = userRegisterInfo.password;

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

        //登出
        params = new HashMap<>();
        params.put("accessToken", loginResult.getAccessToken());

        request = new Http.RequestBuilder().
                method(PUT).
                uri(controllers.api.user.routes.LoginApiController.logout().url()).
                bodyForm(params);

        result = routeWithExceptionHandle(request);
        assertThat(result.status(), is(NO_CONTENT));

        //再次访问,提示无效的accessToken
        request = new Http.RequestBuilder().
                method(POST).
                uri(routes.TestApiController.protectedResource().url()).
                bodyForm(params);
        result = routeWithExceptionHandle(request);
        assertThat(result.status(), is(UNAUTHORIZED));

    }




}
