package controllers.api;

import api.response.user.LoginResult;
import common.utils.JsonUtils;
import controllers.api.user.LoginApiControllerTest;
import org.apache.commons.lang3.RandomStringUtils;
import play.mvc.Result;

import static play.test.Helpers.contentAsString;

/**
 * Created by lidujun on 15-8-28.
 */
public class TestUtils {
    public static LoginResult createUserAndLogin() {
        String phone = "1" + RandomStringUtils.randomNumeric(10);
        String username = RandomStringUtils.randomAlphabetic(10);
        String password = RandomStringUtils.randomAlphabetic(10);

        LoginApiControllerTest loginApiControllerTest = new LoginApiControllerTest();
        Result result = loginApiControllerTest.registerUser(phone, username, password);
        LoginResult loginResult = JsonUtils.json2Object(contentAsString(result), LoginResult.class);
        loginApiControllerTest.assertLoginResultValid(phone, username, loginResult);

        return loginResult;
    }
}
