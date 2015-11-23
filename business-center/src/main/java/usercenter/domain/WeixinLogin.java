package usercenter.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import common.exceptions.AppBusinessException;
import common.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import play.Logger;
import play.libs.F;
import usercenter.dtos.OpenUserInfo;
import usercenter.models.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liubin on 15-5-22.
 */
public class WeixinLogin extends OpenIDConnector {

    public WeixinLogin() {
        super("weixin");
    }

    @Override
    protected Map<String, List<String>> prepareConnectParams() {
        Map<String, List<String>> paramMap = new HashMap<>();
        paramMap.put("appid", Lists.newArrayList(appId));
        paramMap.put("redirect_uri", Lists.newArrayList(redirectUrl));
        paramMap.put("response_type", Lists.newArrayList("code"));
        paramMap.put("scope", Lists.newArrayList("snsapi_login"));
        paramMap.put("state", Lists.newArrayList(createState()));
        return paramMap;
    }

    @SuppressWarnings("unchecked")
    public Object[] handleCallback(String code, String state, String registerIP) {

        if (StringUtils.isBlank(code) || StringUtils.isBlank(state)) {
            Logger.error("微信登录回调的时候发现code或者state值为空");
            throw new AppBusinessException("登录失败, 请尝试其他登录方式");
        }

        checkState(state);

        F.Promise<OpenUserInfo> openUserInfoPromise =
                wsClient.url(accessTokenUrl).
                setQueryParameter("appid", appId).
                setQueryParameter("secret", appSecret).
                setQueryParameter("code", code).
                setQueryParameter("grant_type", "authorization_code").
                get().
                flatMap(response -> {
                    JsonNode jsonNode = response.asJson();
                    logResponse("微信登录请求accessToken的返回结果", jsonNode);
                    Map<String, Object> map = JsonUtils.node2Object(jsonNode, Map.class);
                    throwExceptionIfErrorHappen(map);
                    return wsClient.url(userInfoUrl).
                            setQueryParameter("access_token", map.get("access_token").toString()).
                            setQueryParameter("openid", map.get("openid").toString()).
                            get();
                }).map(response -> {
                    JsonNode jsonNode = response.asJson();
                    logResponse("微信登录请求userInfo的返回结果", jsonNode);
                    Map<String, Object> map = JsonUtils.node2Object(jsonNode, Map.class);
                    throwExceptionIfErrorHappen(map);
                    return OpenUserInfo.fromWeixin(map);
                });


        return getUser(registerIP, openUserInfoPromise);
    }

    private void throwExceptionIfErrorHappen(Map<String, Object> map) {
        if (map.containsKey("errcode")) {
            String errorMsg = String.format("errcode: [%s], errmsg: [%s]", map.get("errcode"), map.get("errmsg"));
            throw new AppBusinessException(errorMsg);
        }
    }

    private void logResponse(String message, JsonNode jsonNode) {
        if (Logger.isDebugEnabled()) {
            Logger.debug(message + ", response json: " + jsonNode.toString());
        }
    }

}
