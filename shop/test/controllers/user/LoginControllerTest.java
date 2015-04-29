package controllers.user;

import common.utils.DateUtils;
import common.utils.JsonResult;
import usercenter.utils.SessionUtils;
import common.utils.test.BaseTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import play.mvc.Result;
import play.test.FakeRequest;
import play.test.WithApplication;
import usercenter.cache.UserCache;
import usercenter.domain.PhoneVerification;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static play.test.Helpers.*;


/**
 * Created by liubin on 15-4-2.
 */
public class LoginControllerTest extends WithApplication implements BaseTest {

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
        String username = RandomStringUtils.randomAlphabetic(2);
        String password = RandomStringUtils.randomAlphabetic(10);

        FakeRequest request = new FakeRequest(POST, routes.LoginController.requestPhoneCode(phone).url());

        Result result = route(request);
        assertThat(status(result), is(OK));
        assertThat(contentType(result), is("application/json"));
        assertThat(contentAsString(result), containsString("true"));

        String verificationCode = UserCache.getPhoneVerificationCode(phone);
        assertThat(verificationCode, notNullValue());
        assertThat(verificationCode.length(), is(PhoneVerification.VERIFICATION_CODE_LENGTH));

        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        params.put("phone", phone);
        params.put("verificationCode", verificationCode);

        request = new FakeRequest(POST, routes.LoginController.register().url()).withFormUrlEncodedBody(params);
        result = route(request);
        assertThat(status(result), is(OK));
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

        FakeRequest request = new FakeRequest(POST, routes.LoginController.requestPhoneCode(phone).url());

        Result result = route(request);
        assertThat(status(result), is(OK));
        assertThat(contentType(result), is("application/json"));
        assertThat(contentAsString(result), containsString("true"));

        String verificationCode = UserCache.getPhoneVerificationCode(phone);
        assertThat(verificationCode, notNullValue());
        assertThat(verificationCode.length(), is(PhoneVerification.VERIFICATION_CODE_LENGTH));

        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        params.put("phone", phone);
        params.put("verificationCode", "foobar");

        request = new FakeRequest(POST, routes.LoginController.register().url()).withFormUrlEncodedBody(params);
        result = route(request);
        assertThat(status(result), is(OK));
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

        FakeRequest request = new FakeRequest(POST, routes.LoginController.login().url()).withFormUrlEncodedBody(params);
        Result result = route(request);
        assertThat(status(result), is(OK));
        JsonResult jsonResult = JsonResult.fromJson(contentAsString(result));
        assertThat(jsonResult.getResult(), is(true));
        assertThat(jsonResult.getData(), is(notNullValue()));
        assertThat(jsonResult.getMessage(), is(nullValue()));

        assertThat(flash(result).isEmpty(), is(true));
        String userId = session(result).get(SessionUtils.SESSION_CREDENTIALS);
        assertThat(userId, notNullValue());

        request = new FakeRequest(GET, controllers.routes.Application.index().url()).
                withSession(SessionUtils.SESSION_CREDENTIALS, userId).
                withSession(SessionUtils.SESSION_REQUEST_TIME, String.valueOf(DateUtils.current().getMillis()));
        result = route(request);
        assertThat(status(result), is(OK));
        assertThat(contentAsString(result).contains(username), is(true));

        //使用手机登录
        params = new HashMap<>();
        params.put("passport", phone);
        params.put("password", password);

        request = new FakeRequest(POST, routes.LoginController.login().url()).withFormUrlEncodedBody(params);
        result = route(request);
        assertThat(status(result), is(OK));
        jsonResult = JsonResult.fromJson(contentAsString(result));
        assertThat(jsonResult.getResult(), is(true));
        assertThat(jsonResult.getData(), is(notNullValue()));
        assertThat(jsonResult.getMessage(), is(nullValue()));

        assertThat(flash(result).isEmpty(), is(true));
        userId = session(result).get(SessionUtils.SESSION_CREDENTIALS);
        assertThat(userId, notNullValue());

        request = new FakeRequest(GET, controllers.routes.Application.index().url()).
                withSession(SessionUtils.SESSION_CREDENTIALS, userId).
                withSession(SessionUtils.SESSION_REQUEST_TIME, String.valueOf(DateUtils.current().getMillis()));

        result = route(request);
        assertThat(status(result), is(OK));
        assertThat(contentAsString(result).contains(username), is(true));

    }

    @Test
    public void testRequestIndexAsGuest() throws Exception {
        FakeRequest request = new FakeRequest(GET, controllers.routes.Application.index().url());
        Result result = route(request);
        assertThat(status(result), is(OK));
        assertThat(contentAsString(result).contains("游客"), is(true));

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
        FakeRequest request = new FakeRequest(POST, routes.LoginController.login().url()).withFormUrlEncodedBody(params);
        Result result = route(request);
        assertThat(status(result), is(OK));
        JsonResult jsonResult = JsonResult.fromJson(contentAsString(result));
        assertThat(jsonResult.getResult(), is(false));
        assertThat(jsonResult.getData(), is(nullValue()));
        assertThat(jsonResult.getMessage(), is(notNullValue()));

    }


    private void registerUser(String phone, String username, String password) {
        FakeRequest request = new FakeRequest(POST, routes.LoginController.requestPhoneCode(phone).url());

        Result result = route(request);
        assertThat(status(result), is(OK));
        assertThat(contentType(result), is("application/json"));
        assertThat(contentAsString(result), containsString("true"));

        String verificationCode = UserCache.getPhoneVerificationCode(phone);
        assertThat(verificationCode, notNullValue());
        assertThat(verificationCode.length(), is(PhoneVerification.VERIFICATION_CODE_LENGTH));

        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        params.put("phone", phone);
        params.put("verificationCode", verificationCode);

        request = new FakeRequest(POST, routes.LoginController.register().url()).withFormUrlEncodedBody(params);
        result = route(request);
        assertThat(status(result), is(OK));
        assertThat(flash(result).isEmpty(), is(true));
        JsonResult jsonResult = JsonResult.fromJson(contentAsString(result));
        assertThat(jsonResult.getResult(), is(true));
        assertThat(jsonResult.getData(), is(notNullValue()));
        assertThat(jsonResult.getMessage(), is(nullValue()));

    }


}
