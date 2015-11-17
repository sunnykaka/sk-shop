package usercenter.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import common.exceptions.AppBusinessException;
import common.utils.JsonUtils;
import common.utils.UrlUtils;
import org.apache.commons.lang3.StringUtils;
import play.Logger;
import play.libs.F;
import usercenter.dtos.OpenUserInfo;
import usercenter.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liubin on 15-5-22.
 */
public class QQLogin extends OpenIDConnector {

    public QQLogin() {
        super("qq");
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
    public Object[] handleCallback(String code, String state, String msg, String registerIP) {

        if (!StringUtils.isBlank(msg) || StringUtils.isBlank(state)) {
            Logger.error(String.format("QQ登录回调失败, msg: [%s], code: [%s]", msg, code));
            throw new AppBusinessException("登录失败, 请尝试其他登录方式");
        }

        checkState(state);

        F.Promise<OpenUserInfo> openUserInfoPromise =
                wsClient.url(accessTokenUrl).
                setQueryParameter("grant_type", "authorization_code").
                setQueryParameter("client_id", appId).
                setQueryParameter("client_secret", appSecret).
                setQueryParameter("code", code).
                setQueryParameter("redirect_uri", redirectUrl).
                get().
                flatMap(response -> {
                    String body = response.getBody();
                    Logger.debug("QQ登录请求accessToken的返回结果: " + body);
                    Map<String, List<String>> map = UrlUtils.disposeQueryString(body);
                    if (map.containsKey("msg")) {
                        String errorMsg = String.format("QQ登录请求accessToken返回错误, msg:%s, code:%s",
                                map.get("msg").toString(), map.getOrDefault("code", new ArrayList<>()).toString());
                        throw new AppBusinessException(errorMsg);
                    }

                    String accessToken = map.get("access_token").get(0);
                    //请求openid
                    return F.Promise.pure(new Object[]{
                            wsClient.url(userInfoUrl).
                            setQueryParameter("access_token", accessToken).get(),
                            accessToken});
                }).flatMap(array -> {
                    F.Promise<play.libs.ws.WSResponse> p = (F.Promise<play.libs.ws.WSResponse>) array[0];
                    String accessToken = (String) array[1];
                    return p.flatMap(response -> {
                        String body = response.getBody();
                        Logger.debug("QQ登录请求userInfo的返回结果: " + body);
                        if (!body.contains("openid")) {
                            throw new AppBusinessException("QQ登录请求userInfo返回错误: " + body);
                        } else {
                            String json = body.substring(body.indexOf("{"), body.indexOf("}") + 1);
                            Map<String, Object> map = JsonUtils.json2Object(json, Map.class);
                            String openId = (String) map.get("openid");
                            //请求userDetailInfo
                            return F.Promise.pure(new Object[]{
                                    wsClient.url(userDetailInfoUrl).
                                            setQueryParameter("access_token", accessToken).
                                            setQueryParameter("oauth_consumer_key", appId).
                                            setQueryParameter("openid", openId).
                                            setQueryParameter("format", "json").
                                            get(),
                                    openId});
                        }
                    });

                }).flatMap(array -> {
                    F.Promise<play.libs.ws.WSResponse> p = (F.Promise<play.libs.ws.WSResponse>) array[0];
                    String openId = (String) array[1];
                    return p.map(response -> {
                        JsonNode jsonNode = response.asJson();
                        Logger.debug("QQ登录请求userDetailInfo的返回结果: " + jsonNode.toString());
                        Map<String, Object> map = JsonUtils.node2Object(jsonNode, Map.class);
                        if (map.isEmpty() || !map.containsKey("ret") || !map.get("ret").toString().equals("0")) {
                            throw new AppBusinessException("QQ登录请求userDetailInfo返回错误: " + map.get("msg"));
                        }

                        //解析userDetailInfo的结果
                        return OpenUserInfo.fromQQ(map, openId);

                    });
                });


        return getUser(registerIP, openUserInfoPromise);

    }

}
