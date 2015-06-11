package usercenter.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import common.exceptions.AppBusinessException;
import common.utils.JsonUtils;
import common.utils.UrlUtils;
import org.apache.commons.lang3.StringUtils;
import play.Logger;
import play.libs.F;
import play.libs.ws.WS;
import usercenter.dtos.OpenUserInfo;
import usercenter.models.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liubin on 15-5-22.
 */
public class WeiboLogin extends OpenIDConnector {

    public WeiboLogin() {
        super("weibo");
    }

    @Override
    protected Map<String, List<String>> prepareConnectParams() {
        Map<String, List<String>> paramMap = new HashMap<>();
        paramMap.put("client_id", Lists.newArrayList(appId));
        paramMap.put("redirect_uri", Lists.newArrayList(redirectUrl));
        paramMap.put("response_type", Lists.newArrayList("code"));
        paramMap.put("state", Lists.newArrayList(createState()));
        return paramMap;
    }

    @SuppressWarnings("unchecked")
    public User handleCallback(String code, String state, String error, String error_code,
                               String registerIP) {

        if (StringUtils.isBlank(code) || StringUtils.isBlank(state)) {
            Logger.error(String.format("微博登录回调的时候发现code或者state值为空, error: [%s], error_code: [%s]",
                    error, error_code));
            throw new AppBusinessException("登录失败, 请尝试其他登录方式");
        }

        checkState(state);

        F.Promise<OpenUserInfo> openUserInfoPromise =
                WS.url(accessTokenUrl).
                        setQueryParameter("client_id", appId).
                        setQueryParameter("client_secret", appSecret).
                        setQueryParameter("code", code).
                        setQueryParameter("grant_type", "authorization_code").
                        setQueryParameter("redirect_uri", redirectUrl).
                        post("").
                flatMap(response -> {
                    JsonNode jsonNode = response.asJson();
                    logResponse("微博登录请求accessToken的返回结果", jsonNode);
                    Map<String, Object> map = JsonUtils.node2Object(jsonNode, Map.class);
                    throwExceptionIfErrorHappen(map);
                    return WS.url(userInfoUrl).
                            setQueryParameter("access_token", map.get("access_token").toString()).
                            setQueryParameter("uid", map.get("uid").toString()).
                            get();
                }).map(response -> {
                    JsonNode jsonNode = response.asJson();
                    logResponse("微博登录请求userInfo的返回结果", jsonNode);
                    Map<String, Object> map = JsonUtils.node2Object(jsonNode, Map.class);
                    throwExceptionIfErrorHappen(map);
                    return OpenUserInfo.fromWeibo(map);
                });

        return getUser(registerIP, openUserInfoPromise);
    }

    private void throwExceptionIfErrorHappen(Map<String, Object> map) {
        if (map.containsKey("error_code")) {
            String errorMsg = String.format("error: [%s], error_code: [%s]",
                    map.get("error"), map.get("error_code"));
            throw new AppBusinessException(errorMsg);
        }
    }

    private void logResponse(String message, JsonNode jsonNode) {
        if (Logger.isDebugEnabled()) {
            Logger.debug(message + ", response json: " + jsonNode.toString());
        }
    }

}
