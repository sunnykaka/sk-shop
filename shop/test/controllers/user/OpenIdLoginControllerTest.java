package controllers.user;

import base.BaseTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Ignore;
import org.junit.Test;
import usercenter.domain.QQLogin;
import usercenter.domain.WeiboLogin;
import usercenter.domain.WeixinLogin;
import usercenter.models.User;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;


/**
 * Created by liubin on 15-4-2.
 */
public class OpenIdLoginControllerTest extends BaseTest {

    @Test
    @Ignore
    public void testWeixinLoginOnline() throws Exception {

        String code = "031fdda9d2c9e2ed245494fcd2affe2P";
        String state = RandomStringUtils.randomAlphanumeric(8);
        WeixinLogin weixinLogin = new WeixinLogin();

        User user = (User)weixinLogin.handleCallback(code, state, "")[0];

        assertThat(user.getId(), is(notNullValue()));

    }

    @Test
    @Ignore
    public void testWeiboLoginOnline() throws Exception {

        String code = "79275c8d32d99667dc2750ace0e98e50";
        String state = RandomStringUtils.randomAlphanumeric(8);
        User user = (User)new WeiboLogin().handleCallback(code, state, "", "", "")[0];

        assertThat(user.getId(), is(notNullValue()));

    }

    @Test
    @Ignore
    public void testQQLoginOnline() throws Exception {

        String code = "A1A11C17A4DE690A89ECAE4EC61FEEF7";
        String state = RandomStringUtils.randomAlphanumeric(8);
        User user = (User)new QQLogin().handleCallback(code, state, "", "")[0];

        assertThat(user.getId(), is(notNullValue()));

    }



}