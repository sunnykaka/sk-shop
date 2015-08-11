package controllers.api.user;

import api.response.user.LoginResult;
import api.response.user.RefreshTokenResult;
import api.response.user.UserDataDto;
import api.response.user.UserDto;
import base.BaseTest;
import common.exceptions.ErrorCode;
import common.utils.JsonUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import play.mvc.Http;
import play.mvc.Result;
import usercenter.cache.UserCache;
import usercenter.constants.MarketChannel;
import usercenter.domain.SmsSender;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static play.test.Helpers.*;


/**
 * Created by liubin on 15-4-2.
 */
public class LoginApiControllerTest extends BaseTest{

    @Test
    public void testRegisterSuccess() throws Exception {

        String phone = "1" + RandomStringUtils.randomNumeric(10);
        String username = RandomStringUtils.randomAlphabetic(10);
        String password = RandomStringUtils.randomAlphabetic(10);

        Result result = registerUser(phone, username, password);

        assertThat(result.status(), is(CREATED));
        LoginResult loginResult = JsonUtils.json2Object(contentAsString(result), LoginResult.class);

        assertLoginResultValid(phone, username, loginResult);

    }

    @Test
    public void testRegisterLoginSuccess() throws Exception {

        String phone = "1" + RandomStringUtils.randomNumeric(10);
        String username = RandomStringUtils.randomAlphabetic(10);
        String password = RandomStringUtils.randomAlphabetic(10);

        Result result = registerUser(phone, username, password);

        assertThat(result.status(), is(CREATED));
        LoginResult loginResult = JsonUtils.json2Object(contentAsString(result), LoginResult.class);

        assertLoginResultValid(phone, username, loginResult);

        //使用用户名登录
        result = login(username, password);
        assertThat(result.status(), is(OK));
        loginResult = JsonUtils.json2Object(contentAsString(result), LoginResult.class);
        assertLoginResultValid(phone, username, loginResult);

    }

    @Test
    public void testInvalidParameterCauseRegisterOrLoginError() throws Exception {

        String username = RandomStringUtils.randomAlphabetic(10);
        String password = RandomStringUtils.randomAlphabetic(10);

        Result result = registerUser("1" + RandomStringUtils.randomNumeric(10), null, password);
        assertResultAsError(result, ErrorCode.InvalidArgument);

        result = registerUser("1" + RandomStringUtils.randomNumeric(10), username, null);
        assertResultAsError(result, ErrorCode.InvalidArgument);

        String phone = "1" + RandomStringUtils.randomNumeric(10);
        //登录成功
        registerUser(phone, username, password);
        result = login(username, password);
        assertThat(result.status(), is(OK));
        LoginResult loginResult = JsonUtils.json2Object(contentAsString(result), LoginResult.class);
        assertLoginResultValid(phone, username, loginResult);

        //登录失败
        result = login(null, password);
        assertResultAsError(result, ErrorCode.InvalidArgument);
        result = login(username, null);
        assertResultAsError(result, ErrorCode.InvalidArgument);
        result = login(username, "123456");
        assertResultAsError(result, ErrorCode.InvalidArgument);

    }


    @Test
    public void testDuplicateUsernameOrPhoneCauseRegisterError() throws Exception {

        String phone = "1" + RandomStringUtils.randomNumeric(10);
        String username = RandomStringUtils.randomAlphabetic(10);
        String password = RandomStringUtils.randomAlphabetic(10);

        registerUser(phone, username, password);

        Result result = registerUser("1" + RandomStringUtils.randomNumeric(10), username, password);
        assertResultAsError(result, ErrorCode.UsernameExist);

    }


    @Test
    public void testUsernameExist() throws Exception {

        String phone = "1" + RandomStringUtils.randomNumeric(10);
        String username = RandomStringUtils.randomAlphabetic(10);
        String password = RandomStringUtils.randomAlphabetic(10);

        Http.RequestBuilder request = new Http.RequestBuilder().method(GET).uri(routes.LoginApiController.isUserExist(username, null).url());
        Result result = routeWithExceptionHandle(request);
        assertThat(result.status(), is(NO_CONTENT));
        assertThat(contentAsString(result), is(""));

        request = new Http.RequestBuilder().method(GET).uri(routes.LoginApiController.isUserExist(null, phone).url());
        result = routeWithExceptionHandle(request);
        assertThat(result.status(), is(NO_CONTENT));
        assertThat(contentAsString(result), is(""));

        registerUser(phone, username, password);

        request = new Http.RequestBuilder().method(GET).uri(routes.LoginApiController.isUserExist(null, null).url());
        result = routeWithExceptionHandle(request);
        assertResultAsError(result, ErrorCode.InvalidArgument);

        request = new Http.RequestBuilder().method(GET).uri(routes.LoginApiController.isUserExist(username, null).url());
        result = routeWithExceptionHandle(request);
        assertThat(result.status(), is(CONFLICT));
        assertThat(contentAsString(result), is(""));

        request = new Http.RequestBuilder().method(GET).uri(routes.LoginApiController.isUserExist(null, phone).url());
        result = routeWithExceptionHandle(request);
        assertThat(result.status(), is(CONFLICT));
        assertThat(contentAsString(result), is(""));

        request = new Http.RequestBuilder().method(GET).uri(routes.LoginApiController.isUserExist(username, phone).url());
        result = routeWithExceptionHandle(request);
        assertThat(result.status(), is(CONFLICT));
        assertThat(contentAsString(result), is(""));

        request = new Http.RequestBuilder().method(GET).uri(routes.LoginApiController.isUserExist(username, "1" + RandomStringUtils.randomNumeric(10)).url());
        result = routeWithExceptionHandle(request);
        assertThat(result.status(), is(CONFLICT));
        assertThat(contentAsString(result), is(""));

    }

    @Test
    public void testRefreshTokenSuccess() throws Exception {
        String phone = "1" + RandomStringUtils.randomNumeric(10);
        String username = RandomStringUtils.randomAlphabetic(10);
        String password = RandomStringUtils.randomAlphabetic(10);

        Result result = registerUser(phone, username, password);
        assertThat(result.status(), is(CREATED));
        LoginResult loginResult = JsonUtils.json2Object(contentAsString(result), LoginResult.class);
        assertLoginResultValid(phone, username, loginResult);

        //使用用户名登录
        result = login(username, password);
        assertThat(result.status(), is(OK));
        loginResult = JsonUtils.json2Object(contentAsString(result), LoginResult.class);
        assertLoginResultValid(phone, username, loginResult);

        Map<String, String> params = new HashMap<>();
        params.put("refreshToken", loginResult.getRefreshToken());
        //刷新token
        Http.RequestBuilder request = new Http.RequestBuilder().
                method(POST).
                uri(routes.LoginApiController.refreshToken().url()).
                bodyForm(params);
        result = routeWithExceptionHandle(request);
        assertThat(result.status(), is(OK));
        RefreshTokenResult refreshTokenResult = JsonUtils.json2Object(contentAsString(result), RefreshTokenResult.class);
        assertThat(refreshTokenResult.getAccessToken(), is(loginResult.getAccessToken()));
        assertThat(refreshTokenResult.getRefreshToken(), is(loginResult.getRefreshToken()));
        assertThat(refreshTokenResult.getExpiresIn(), is(loginResult.getExpiresIn()));


    }

    public Result login(String username, String password) {
        Map<String, String> params = new HashMap<>();
        if(username != null) {
            params.put("passport", username);
        }
        if(password != null) {
            params.put("password", password);
        }
        params.put("channel", MarketChannel.iOS.getValue());

        Http.RequestBuilder request = new Http.RequestBuilder().
                method(POST).
                uri(routes.LoginApiController.login().url()).
                bodyForm(params);
        return routeWithExceptionHandle(request);
    }


    public Result registerUser(String phone, String username, String password) {

        Map<String, String> params = new HashMap<>();
        params.put("phone", phone);

        Http.RequestBuilder request = new Http.RequestBuilder().
                method(POST).
                uri(routes.LoginApiController.requestPhoneCode().url()).
                bodyForm(params);

        Result result = routeWithExceptionHandle(request);
        assertThat(result.status(), is(NO_CONTENT));

        String verificationCode = UserCache.getPhoneVerificationCode(phone, SmsSender.Usage.REGISTER);
        assertThat(verificationCode, notNullValue());
        assertThat(verificationCode.length(), is(SmsSender.VERIFICATION_CODE_LENGTH));

        if(username != null) {
            params.put("username", username);
        }
        if(password != null) {
            params.put("password", password);
        }
        params.put("phone", phone);
        params.put("verificationCode", verificationCode);
        params.put("channel", MarketChannel.iOS.getValue());

        request = new Http.RequestBuilder().
                method(POST).
                uri(routes.LoginApiController.register().url()).
                bodyForm(params);
        result = routeWithExceptionHandle(request);
        return result;
    }

    public void assertLoginResultValid(String phone, String username, LoginResult loginResult) {
        assertThat(loginResult.getAccessToken(), notNullValue());
        assertThat(loginResult.getRefreshToken(), notNullValue());
        assertThat(loginResult.getExpiresIn() > 0, is(true));
        assertThat(loginResult.getUser(), notNullValue());

        UserDto userDto = loginResult.getUser();
        assertThat(userDto.getPhone(), is(phone));
        assertThat(userDto.getUserName(), is(username));
        assertThat(userDto.getEmail(), nullValue());
        assertThat(userDto.getUserData(), notNullValue());

        UserDataDto userDataDto = userDto.getUserData();
        assertThat(userDataDto.getName(), nullValue());

    }


}
