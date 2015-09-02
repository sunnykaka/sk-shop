package controllers.api;

import api.response.user.LoginResult;
import api.response.user.UserDto;
import common.utils.JsonUtils;
import controllers.api.user.LoginApiControllerTest;
import org.apache.commons.lang3.RandomStringUtils;
import play.mvc.Result;

import static play.mvc.Http.Status.OK;
import static play.test.Helpers.contentAsString;

/**
 * Created by lidujun on 15-8-28.
 */
public class TestUtils {
    public static UserDto login() {
        String phone = "1" + RandomStringUtils.randomNumeric(10);
        String username = RandomStringUtils.randomAlphabetic(10);
        String password = RandomStringUtils.randomAlphabetic(10);
        Result result = new LoginApiControllerTest().registerUser(phone, username, password);
        if(result.status() == OK) {
            LoginResult loginResult = JsonUtils.json2Object(contentAsString(result), LoginResult.class);
            return loginResult.getUser();
        }
        return null;
    }
}
