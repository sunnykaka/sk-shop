package usercenter.domain;

import common.exceptions.AppBusinessException;
import common.utils.UrlUtils;
import common.utils.play.BaseGlobal;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import play.Configuration;
import play.Logger;
import play.Play;
import play.libs.F;
import play.libs.ws.WSClient;
import play.mvc.Http;
import usercenter.dtos.OpenUserInfo;
import usercenter.models.User;
import usercenter.services.UserOuterService;

import java.util.List;
import java.util.Map;

/**
 * Created by liubin on 15-6-10.
 */
public abstract class OpenIDConnector {

    WSClient wsClient = BaseGlobal.injector.instanceOf(WSClient.class);
    Configuration conf = Play.application().configuration();
    public final String appId;
    public final String appSecret;
    public final String redirectUrl;
    public final String connectUrl;
    public final String accessTokenUrl;
    public final String userInfoUrl;
    public final String userDetailInfoUrl;

    protected String name;

    protected OpenIDConnector(String name) {
        this.name = name;

        appId = conf.getString("shop." + name + ".appId");
        appSecret = conf.getString("shop." + name + ".appSecret");
        redirectUrl = conf.getString("shop." + name + ".redirectUrl");
        connectUrl = conf.getString("shop." + name + ".connectUrl");
        accessTokenUrl = conf.getString("shop." + name + ".accessTokenUrl");
        userInfoUrl = conf.getString("shop." + name + ".userInfoUrl");
        userDetailInfoUrl = conf.getString("shop." + name + ".userDetailInfoUrl");
    }

    public String redirectToConnectUrl() {

        Map<String, List<String>> paramMap = prepareConnectParams();

        String url = connectUrl + "?" + UrlUtils.buildQueryString(paramMap);
        play.Logger.debug(name + "登录重定向到: " + url);
        return url;
    }

    protected abstract Map<String, List<String>> prepareConnectParams();

    protected String createState() {
        String state = RandomStringUtils.randomAlphabetic(12);
        Http.Context.current().session().put(name + "_state", state);
        return state;
    }

    protected void checkState(String state) {
        String stateInSession = Http.Context.current().session().get(name + "_state");
        if (StringUtils.isBlank(stateInSession) || !stateInSession.equals(state)) {
            Logger.error(name + "登录回调的时候校验session中的值" + name + "_state失败, stateInSession:" + stateInSession);
            throw new AppBusinessException("登录失败, 请尝试其他登录方式");
        }
    }

    protected User getUser(String registerIP, F.Promise<OpenUserInfo> openUserInfoPromise) {

        OpenUserInfo openUserInfo;
        try {
            openUserInfo = openUserInfoPromise.get(10000L);
        } catch (AppBusinessException e) {
            Logger.error("请求" + name + "接口返回错误信息: " + e.getMessage());
            throw new AppBusinessException("登录失败, 请尝试其他登录方式");
        } catch (Exception e) {
            Logger.error("请求" + name + "接口发生错误", e);
            throw new AppBusinessException("登录失败, 请尝试其他登录方式");
        }

        UserOuterService userOuterService = BaseGlobal.ctx.getBean(UserOuterService.class);
        return userOuterService.handleOpenIdCallback(openUserInfo, registerIP);
    }

}
