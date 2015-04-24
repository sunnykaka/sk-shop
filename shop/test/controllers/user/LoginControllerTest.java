package controllers.user;

import common.utils.test.BaseTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import play.cache.Cache;
import play.mvc.Result;
import play.test.FakeRequest;
import play.test.WithApplication;
import usercenter.services.UserService;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
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

        FakeRequest request = new FakeRequest(POST, routes.LoginController.requestPhoneCode(phone).url());

        Result result = route(request);
        assertThat(status(result), is(OK));
        assertThat(contentType(result), is("application/json"));
        assertThat(contentAsString(result), containsString("true"));

        String verificationCode = (String)Cache.get(UserService.VERIFICATION_CODE_KEY_PREFIX + phone);
        assertThat(verificationCode, notNullValue());
        assertThat(verificationCode.length(), is(UserService.VERIFICATION_CODE_LENGTH));

        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        params.put("phone", phone);
        params.put("verificationCode", verificationCode);

        request = new FakeRequest(POST, routes.LoginController.register().url()).withFormUrlEncodedBody(params);
        result = route(request);
        assertThat(status(result), is(SEE_OTHER));
        assertThat(flash(result).isEmpty(), is(true));
    }

    @Test
    public void testRegisterError() throws Exception {

        String phone = "1" + RandomStringUtils.randomNumeric(10);
        String username = RandomStringUtils.randomAlphabetic(2);
        String password = RandomStringUtils.randomAlphabetic(10);

        FakeRequest request = new FakeRequest(POST, routes.LoginController.requestPhoneCode(phone).url());

        Result result = route(request);
        assertThat(status(result), is(OK));
        assertThat(contentType(result), is("application/json"));
        assertThat(contentAsString(result), containsString("true"));

        String verificationCode = (String)Cache.get(UserService.VERIFICATION_CODE_KEY_PREFIX + phone);
        assertThat(verificationCode, notNullValue());
        assertThat(verificationCode.length(), is(UserService.VERIFICATION_CODE_LENGTH));

        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        params.put("phone", phone);
        params.put("verificationCode", verificationCode);

        request = new FakeRequest(POST, routes.LoginController.register().url()).withFormUrlEncodedBody(params);
        result = route(request);
        assertThat(status(result), is(SEE_OTHER));
        assertThat(flash(result).isEmpty(), is(false));
        assertThat(flash(result).get("errors"), is(notNullValue()));
        System.out.println("flash: " + flash(result).get("errors"));

    }


}
