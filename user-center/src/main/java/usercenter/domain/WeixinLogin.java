package usercenter.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import common.exceptions.AppBusinessException;
import common.utils.JsonUtils;
import common.utils.UrlUtils;
import common.utils.play.BaseGlobal;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import play.Configuration;
import play.Logger;
import play.Play;
import play.libs.F;
import play.mvc.Http;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import play.libs.ws.*;
import usercenter.dtos.OpenUserInfo;
import usercenter.models.User;
import usercenter.services.UserOuterService;
import usercenter.services.UserService;

/**
 * Created by liubin on 15-5-22.
 */
public class WeixinLogin {

    Configuration conf = Play.application().configuration();
    public final String appId = conf.getString("shop.weixin.appId");
    public final String appSecret = conf.getString("shop.weixin.appSecret");
    public final String redirectUrl = conf.getString("shop.weixin.redirectUrl");
    public final String connectUrl = conf.getString("shop.weixin.connectUrl");
    public final String accessTokenUrl = conf.getString("shop.weixin.accessTokenUrl");
    public final String userInfoUrl = conf.getString("shop.weixin.userInfoUrl");

    public WeixinLogin() {

    }

    public String redirectToConnectUrl() {

        Map<String, List<String>> paramMap = new HashMap<>();
        paramMap.put("appid", Lists.newArrayList(appId));
        paramMap.put("redirect_uri", Lists.newArrayList(redirectUrl));
        paramMap.put("response_type", Lists.newArrayList("code"));
        paramMap.put("scope", Lists.newArrayList("snsapi_login"));
        paramMap.put("state", Lists.newArrayList(createState()));

        String url = connectUrl + "?" + UrlUtils.buildQueryString(paramMap);
        play.Logger.debug("重定向到微信登录: " + url);
        return url;
    }

    private String createState() {

        String state = RandomStringUtils.randomAlphabetic(12);
        Http.Context.current().session().put("wx_state", state);
        return state;
    }


    @SuppressWarnings("unchecked")
    public User handleCallback(String code, String state, String registerIP) {

        String errorMsg = "登录失败, 请尝试其他登录方式";
        if (StringUtils.isBlank(code) || StringUtils.isBlank(state)) {
            Logger.error("微信登录回调的时候发现code或者state值为空");
            throw new AppBusinessException(errorMsg);
        }
        String stateInSession = Http.Context.current().session().get("wx_state");
        if (StringUtils.isBlank(stateInSession) || !stateInSession.equals(state)) {
            Logger.error("微信登录回调的时候校验session中的值wx_state失败");
            throw new AppBusinessException(errorMsg);
        }

        F.Promise<OpenUserInfo> openUserInfoPromise =
                WS.url(accessTokenUrl).
                setQueryParameter("appid", appId).
                setQueryParameter("secret", appSecret).
                setQueryParameter("code", code).
                setQueryParameter("grant_type", "authorization_code").
                get().
                flatMap(response -> {
                    JsonNode jsonNode = response.asJson();
                    logResponse("请求accessToken的返回结果", jsonNode);
                    Map<String, Object> map = JsonUtils.node2Object(jsonNode, Map.class);
                    throwExceptionIfErrorHappen(map);
                    return WS.url(userInfoUrl).
                            setQueryParameter("access_token", map.get("access_token").toString()).
                            setQueryParameter("openid", map.get("openid").toString()).
                            get();
                }).map(response -> {
                    JsonNode jsonNode = response.asJson();
                    logResponse("请求userInfo的返回结果", jsonNode);
                    Map<String, Object> map = JsonUtils.node2Object(jsonNode, Map.class);
                    throwExceptionIfErrorHappen(map);
                    return OpenUserInfo.fromWeixin(map);
                });

        OpenUserInfo openUserInfo;
        try {
            openUserInfo = openUserInfoPromise.get(10000L);
        } catch (AppBusinessException e) {
            Logger.error("请求微信接口返回错误信息", e.getMessage());
            throw new AppBusinessException(errorMsg);
        } catch (Exception e) {
            Logger.error("请求微信接口发生错误", e);
            throw new AppBusinessException(errorMsg);
        }

        UserOuterService userOuterService = BaseGlobal.ctx.getBean(UserOuterService.class);
        return userOuterService.handleOpenIdCallback(openUserInfo, registerIP);
    }

    private void throwExceptionIfErrorHappen(Map<String, Object> map) {
        if (map.containsKey("errcode")) {
            String errorMsg = String.format("errcode:%s, errmsg:%s", map.getOrDefault("errcode", ""), map.getOrDefault("errmsg", ""));
            throw new AppBusinessException(errorMsg);
        }
    }

    private void logResponse(String message, JsonNode jsonNode) {
        if (Logger.isDebugEnabled()) {
            Logger.debug(message + ", response json: " + jsonNode.toString());
        }
    }

}
