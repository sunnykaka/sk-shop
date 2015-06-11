package controllers.user;

import common.utils.DateUtils;
import common.utils.JsonResult;
import common.utils.test.BaseTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Ignore;
import org.junit.Test;
import play.mvc.Result;
import play.test.FakeRequest;
import play.test.WithApplication;
import usercenter.cache.UserCache;
import usercenter.domain.QQLogin;
import usercenter.domain.SmsSender;
import usercenter.domain.WeiboLogin;
import usercenter.domain.WeixinLogin;
import usercenter.models.User;
import usercenter.utils.SessionUtils;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static play.test.Helpers.*;


/**
 * Created by liubin on 15-4-2.
 */
public class OpenIdLoginControllerTest extends WithApplication implements BaseTest {

    @Test
    @Ignore
    public void testWeixinLoginOnline() throws Exception {

        String code = "031fdda9d2c9e2ed245494fcd2affe2P";
        String state = RandomStringUtils.randomAlphanumeric(8);
        WeixinLogin weixinLogin = new WeixinLogin();

        User user = weixinLogin.handleCallback(code, state, "");

        assertThat(user.getId(), is(notNullValue()));

    }

    @Test
    @Ignore
    public void testWeiboLoginOnline() throws Exception {

        String code = "79275c8d32d99667dc2750ace0e98e50";
        String state = RandomStringUtils.randomAlphanumeric(8);
        User user = new WeiboLogin().handleCallback(code, state, "", "", "");

        assertThat(user.getId(), is(notNullValue()));

    }

    @Test
    @Ignore
    public void testQQLoginOnline() throws Exception {

        String code = "A1A11C17A4DE690A89ECAE4EC61FEEF7";
        String state = RandomStringUtils.randomAlphanumeric(8);
        User user = new QQLogin().handleCallback(code, state, "", "");

        assertThat(user.getId(), is(notNullValue()));

    }



}