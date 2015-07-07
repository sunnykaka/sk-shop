package controllers.user;

import base.BaseTest;
import common.utils.DateUtils;
import common.utils.JsonResult;
import play.*;
import play.Application;
import play.api.*;
import play.core.DefaultWebCommands;
import play.mvc.Http;
import scala.Option;
import usercenter.utils.SessionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import play.mvc.Result;
import usercenter.cache.UserCache;
import usercenter.domain.SmsSender;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static play.test.Helpers.*;


/**
 * Created by liubin on 15-4-2.
 */
public class LoginControllerTest extends BaseTest {


    @Test
    public void testRegisterSuccess() throws Exception {

        String phone = "1" + RandomStringUtils.randomNumeric(10);
        String username = RandomStringUtils.randomAlphabetic(10);
        String password = RandomStringUtils.randomAlphabetic(10);

        registerUser(phone, username, password);

    }

    @Test
    public void testRegisterErrorForUsernameTooShort() throws Exception {

        String phone = "1" + RandomStringUtils.randomNumeric(10);
        String username = RandomStringUtils.randomAlphabetic(1);
        String password = RandomStringUtils.randomAlphabetic(10);

        Http.RequestBuilder request = new Http.RequestBuilder().method(POST).uri(routes.LoginController.requestPhoneCode(phone).url());
        Result result = route(request);
        assertThat(result.status(), is(OK));
        assertThat(result.contentType(), is("application/json"));
        assertThat(contentAsString(result), containsString("true"));

        String verificationCode = UserCache.getPhoneVerificationCode(phone, SmsSender.Usage.REGISTER);
        assertThat(verificationCode, notNullValue());
        assertThat(verificationCode.length(), is(SmsSender.VERIFICATION_CODE_LENGTH));

        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        params.put("phone", phone);
        params.put("verificationCode", verificationCode);

        request = new Http.RequestBuilder().method(POST).uri(routes.LoginController.register().url()).bodyForm(params);
        result = route(request);
        assertThat(result.status(), is(OK));
        JsonResult jsonResult = JsonResult.fromJson(contentAsString(result));
        assertThat(jsonResult.getResult(), is(false));
        assertThat(jsonResult.getData(), is(nullValue()));
        assertThat(jsonResult.getMessage(), is(notNullValue()));

    }

    @Test
    public void testRegisterErrorForVerificationCodeNotCorrect() throws Exception {

        String phone = "1" + RandomStringUtils.randomNumeric(10);
        String username = RandomStringUtils.randomAlphabetic(10);
        String password = RandomStringUtils.randomAlphabetic(10);

        Http.RequestBuilder request = new Http.RequestBuilder().method(POST).uri(routes.LoginController.requestPhoneCode(phone).url());

        Result result = route(request);
        assertThat(result.status(), is(OK));
        assertThat(result.contentType(), is("application/json"));
        assertThat(contentAsString(result), containsString("true"));

        String verificationCode = UserCache.getPhoneVerificationCode(phone, SmsSender.Usage.REGISTER);
        assertThat(verificationCode, notNullValue());
        assertThat(verificationCode.length(), is(SmsSender.VERIFICATION_CODE_LENGTH));

        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        params.put("phone", phone);
        params.put("verificationCode", "foobar");

        request = new Http.RequestBuilder().method(POST).uri(routes.LoginController.register().url()).bodyForm(params);
        result = route(request);
        assertThat(result.status(), is(OK));
        JsonResult jsonResult = JsonResult.fromJson(contentAsString(result));
        assertThat(jsonResult.getResult(), is(false));
        assertThat(jsonResult.getData(), is(nullValue()));
        assertThat(jsonResult.getMessage(), is(notNullValue()));


    }


    @Test
    public void testRegisterLoginSuccess() throws Exception {

        String phone = "1" + RandomStringUtils.randomNumeric(10);
        String username = RandomStringUtils.randomAlphabetic(10);
        String password = RandomStringUtils.randomAlphabetic(10);

        registerUser(phone, username, password);

        //使用用户名登录
        Map<String, String> params = new HashMap<>();
        params.put("passport", username);
        params.put("password", password);

        Http.RequestBuilder request = new Http.RequestBuilder().method(POST).uri(routes.LoginController.login().url()).bodyForm(params);
        Result result = route(request);
        assertThat(result.status(), is(OK));
        JsonResult jsonResult = JsonResult.fromJson(contentAsString(result));
        assertThat(jsonResult.getResult(), is(true));
        assertThat(jsonResult.getData(), is(notNullValue()));
        assertThat(jsonResult.getMessage(), is(nullValue()));

        assertThat(result.flash().isEmpty(), is(true));
        String userId = result.session().get(SessionUtils.SESSION_CREDENTIALS);
        assertThat(userId, notNullValue());

        request = new Http.RequestBuilder().method(GET).uri(controllers.routes.Application.index().url()).
                session(SessionUtils.SESSION_CREDENTIALS, userId).
                session(SessionUtils.SESSION_REQUEST_TIME, String.valueOf(DateUtils.current().getMillis()));
        result = route(request);
        assertThat(result.status(), is(OK));
        assertThat(contentAsString(result).contains(username), is(true));

        //使用手机登录
        params = new HashMap<>();
        params.put("passport", phone);
        params.put("password", password);

        request = new Http.RequestBuilder().method(POST).uri(routes.LoginController.login().url()).bodyForm(params);
        result = route(request);
        assertThat(result.status(), is(OK));
        jsonResult = JsonResult.fromJson(contentAsString(result));
        assertThat(jsonResult.getResult(), is(true));
        assertThat(jsonResult.getData(), is(notNullValue()));
        assertThat(jsonResult.getMessage(), is(nullValue()));

        assertThat(result.flash().isEmpty(), is(true));
        userId = result.session().get(SessionUtils.SESSION_CREDENTIALS);
        assertThat(userId, notNullValue());

        request = new Http.RequestBuilder().method(GET).uri(controllers.routes.Application.index().url()).
                session(SessionUtils.SESSION_CREDENTIALS, userId).
                session(SessionUtils.SESSION_REQUEST_TIME, String.valueOf(DateUtils.current().getMillis()));

        result = route(request);
        assertThat(status(result), is(OK));
        assertThat(contentAsString(result).contains(username), is(true));

    }

    @Test
    public void testRequestIndexAsGuest() throws Exception {
        Http.RequestBuilder request = new Http.RequestBuilder().method(GET).uri(controllers.routes.Application.index().url());
        Result result = route(request);
        assertThat(result.status(), is(OK));
        assertThat(contentAsString(result).contains("登录"), is(true));

    }

    @Test
    public void testLoginError() throws Exception {

        String phone = "1" + RandomStringUtils.randomNumeric(10);
        String username = RandomStringUtils.randomAlphabetic(10);
        String password = RandomStringUtils.randomAlphabetic(10);

        registerUser(phone, username, password);

        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", "12345678");
        Http.RequestBuilder request = new Http.RequestBuilder().method(POST).uri(routes.LoginController.login().url()).bodyForm(params);
        Result result = route(request);
        assertThat(result.status(), is(OK));
        JsonResult jsonResult = JsonResult.fromJson(contentAsString(result));
        assertThat(jsonResult.getResult(), is(false));
        assertThat(jsonResult.getData(), is(nullValue()));
        assertThat(jsonResult.getMessage(), is(notNullValue()));

    }

    @Test
    public void testRequestPhoneCodeSixTimesError() throws Exception {
        String phone = "1" + RandomStringUtils.randomNumeric(10);
        for (int i=0; i<SmsSender.SEND_MESSAGE_MAX_TIMES_IN_DAY; i++) {
            Http.RequestBuilder request = new Http.RequestBuilder().method(POST).uri(routes.LoginController.requestPhoneCode(phone).url());
            Result result = route(request);
            assertThat(result.status(), is(OK));
            assertThat(result.contentType(), is("application/json"));
            assertThat(contentAsString(result), containsString("true"));

        }
        Http.RequestBuilder request = new Http.RequestBuilder().method(POST).uri(routes.LoginController.requestPhoneCode(phone).url());

        Result result = route(request);
        assertThat(result.status(), is(OK));
        assertThat(result.contentType(), is("application/json"));
        assertThat(contentAsString(result), containsString("false"));
        assertThat(contentAsString(result), containsString("发送次数超过上限"));

    }


    private void registerUser(String phone, String username, String password) {
        Http.RequestBuilder request = new Http.RequestBuilder().method(POST).uri(routes.LoginController.requestPhoneCode(phone).url());

        Result result = route(request);
        assertThat(result.status(), is(OK));
        assertThat(result.contentType(), is("application/json"));
        assertThat(contentAsString(result), containsString("true"));

        String verificationCode = UserCache.getPhoneVerificationCode(phone, SmsSender.Usage.REGISTER);
        assertThat(verificationCode, notNullValue());
        assertThat(verificationCode.length(), is(SmsSender.VERIFICATION_CODE_LENGTH));

        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        params.put("phone", phone);
        params.put("verificationCode", verificationCode);

        request = new Http.RequestBuilder().method(POST).uri(routes.LoginController.register().url()).bodyForm(params);
        result = route(request);
        assertThat(result.status(), is(OK));
        assertThat(result.flash().isEmpty(), is(true));
        JsonResult jsonResult = JsonResult.fromJson(contentAsString(result));
        assertThat(jsonResult.getResult(), is(true));
        assertThat(jsonResult.getData(), is(notNullValue()));
        assertThat(jsonResult.getMessage(), is(nullValue()));

    }


}
