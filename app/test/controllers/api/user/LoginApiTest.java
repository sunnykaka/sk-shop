package controllers.api.user;

import api.response.user.LoginResult;
import api.response.user.UserDataDto;
import api.response.user.UserDto;
import base.DbTest;
import common.exceptions.AppException;
import common.utils.DateUtils;
import play.mvc.Http;
import play.mvc.Result;
import usercenter.cache.UserCache;
import usercenter.constants.MarketChannel;
import usercenter.domain.SmsSender;
import usercenter.utils.SessionUtils;

import java.util.HashMap;
import java.util.Map;

import static common.utils.TestUtils.UserRegisterInfo;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static play.test.Helpers.NO_CONTENT;
import static play.test.Helpers.POST;

/**
* 注册登录测试接口
* 如果测试类需要请求用户登录后的接口，则可以使用这个类中的方法进行注册和登录
*
* Created by liubin on 15-10-12.
*/
public interface LoginApiTest extends DbTest {

    /**
     * 注册
     * @param userRegisterInfo
     */
    default Result registerUser(UserRegisterInfo userRegisterInfo) {
        return registerUser(userRegisterInfo.phone, userRegisterInfo.username, userRegisterInfo.password);
    }

    /**
     * 注册
     * @param phone
     * @param username
     * @param password
     */
    default Result registerUser(String phone, String username, String password) {
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

    /**
     * 登录
     * @param userRegisterInfo
     * @return
     */
    default Result login(UserRegisterInfo userRegisterInfo) {
        return login(userRegisterInfo.username, userRegisterInfo.password);
    }

    /**
     * 登录
     * @param passport
     * @param password
     * @return
     */
    default Result login(String passport, String password) {
        Map<String, String> params = new HashMap<>();
        if(passport != null) {
            params.put("passport", passport);
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

    /**
     * 向request中加入用户session信息，如果request需要访问登录保护的功能，则需要先调用这个方法
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

    default void assertLoginResultValid(String phone, String username, LoginResult loginResult) {
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
