package controllers.user;

import base.DbTest;
import common.exceptions.AppException;
import common.utils.DateUtils;
import common.utils.JsonResult;
import play.mvc.Http;
import play.mvc.Result;
import usercenter.cache.UserCache;
import usercenter.domain.SmsSender;
import usercenter.utils.SessionUtils;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.POST;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.route;
import static common.utils.TestUtils.*;

/**
 * 注册登录测试接口
 * 如果测试类需要请求用户登录后的接口，则可以使用这个类中的方法进行注册和登录
 *
 * Created by liubin on 15-10-12.
 */
public interface LoginTest extends DbTest {

    /**
     * 注册
     * @param userRegisterInfo
     */
    default void registerUser(UserRegisterInfo userRegisterInfo) {
        registerUser(userRegisterInfo.phone, userRegisterInfo.username, userRegisterInfo.password);
    }

    /**
     * 注册
     * @param phone
     * @param username
     * @param password
     */
    default void registerUser(String phone, String username, String password) {
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

    /**
     * 登录
     * @param userRegisterInfo
     * @return session中的用户ID
     */
    default Integer login(UserRegisterInfo userRegisterInfo) {
        return login(userRegisterInfo.username, userRegisterInfo.password);
    }

    /**
     * 登录
     * @param passport
     * @param password
     * @return session中的用户ID
     */
    default Integer login(String passport, String password) {
        Map<String, String> params = new HashMap<>();
        params.put("passport", passport);
        params.put("password", password);

        Http.RequestBuilder request = new Http.RequestBuilder().method(POST).uri(routes.LoginController.login().url()).bodyForm(params);
        Result result = route(request);
        assertThat(result.status(), is(OK));
        JsonResult jsonResult = JsonResult.fromJson(contentAsString(result));
        assertThat(jsonResult.getResult(), is(true));
        assertThat(jsonResult.getData(), is(notNullValue()));
        assertThat(jsonResult.getMessage(), is(nullValue()));

        assertThat(result.flash().isEmpty(), is(true));
        String credentials = result.session().get(SessionUtils.SESSION_CREDENTIALS);
        assertThat(credentials, notNullValue());
        return SessionUtils.getUserFromCredentials(credentials);
    }

    /**
     * 向request中加入用户session信息，如果request需要访问登录保护的功能，则在调用route之前需要调用这个方法
     * @param request
     * @param userId
     * @return
     */
    default Http.RequestBuilder wrapLoginInfo(Http.RequestBuilder request, Integer userId) {
        try {
            return request.session(SessionUtils.SESSION_CREDENTIALS, SessionUtils.buildCredentials(userId)).
                    session(SessionUtils.SESSION_REQUEST_TIME, String.valueOf(DateUtils.current().getMillis()));
        } catch (AppException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 创建一个用户
     * @return
     */
    default Integer mockUser() {
        UserRegisterInfo userRegisterInfo = mockUserRegisterInfo();
        registerUser(userRegisterInfo);
        return login(userRegisterInfo);
    }

}
