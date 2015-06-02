package controllers.user;

import common.utils.JsonUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import usercenter.domain.WeixinLogin;
import usercenter.services.UserService;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@org.springframework.stereotype.Controller
public class FakeOpenIDProviderController extends Controller {

    @Autowired
    UserService userService;

    private Map<String, String> codeToAccessTokenMap = new ConcurrentHashMap<>();
    private Map<String, String> accessTokenToOpenIdMap = new ConcurrentHashMap<>();


    public static final String ERROR_STATE = "ERROR_STATE";

    public Result wxConnect(String appid, String redirect_uri, String response_type, String scope, String state) {

        boolean error = false;
        if(StringUtils.isBlank(appid) || StringUtils.isBlank(redirect_uri) || StringUtils.isBlank(response_type) ||
                StringUtils.isBlank(scope) || StringUtils.isBlank(state)) {
            Logger.error(String.format("微信重定向请求参数有误, 某参数为空: appid[%s], redirect_uri[%s], response_type[%s], scope[%s], state[%s]",
                    appid, redirect_uri, response_type, scope, state));
            error = true;
        }
        if(state.equals(ERROR_STATE)) {
            error = true;
        }

        String url = new WeixinLogin().redirectUrl + "?state=" + state;
        if(!error) {
            url += "&code=" + generateCode();
        }

        return redirect(url);

    }

    public Result wxGetAccessToken(String appid, String secret, String code, String grant_type) {

        String errorMsg = null;
        Map<String, Object> results = new HashMap<>();

        if(StringUtils.isBlank(appid) || StringUtils.isBlank(secret) ||
                StringUtils.isBlank(code) || StringUtils.isBlank(grant_type)) {
            errorMsg = String.format("微信获取accessToken请求参数有误, 某参数为空: appid[%s], secret[%s], code[%s], grant_type[%s]",
                    appid, secret, code, grant_type);
            Logger.error(errorMsg);

        } else {

            String accessToken = codeToAccessTokenMap.get(code);
            if(accessToken == null) {
                errorMsg = "根据code没有找到accessToken";
            } else {
                String openId = accessTokenToOpenIdMap.get(accessToken);
                if(openId == null) {
                    errorMsg = "根据accessToken没有找到openId";
                } else {
                    results.put("access_token", accessToken);
                    results.put("expires_in", 7200);
                    results.put("refresh_token", accessToken);
                    results.put("openid", openId);
                    results.put("scope", "snsapi_userinfo");
                    results.put("unionid", "");
                }
            }
        }

        if(errorMsg != null) {
            results.put("errcode", 10086);
            results.put("errmsg", errorMsg);

        }

        return ok(JsonUtils.object2Node(results));

    }

    public Result wxGetUserInfo(String access_token, String openid) {

        String errorMsg = null;
        Map<String, Object> results = new HashMap<>();

        if(StringUtils.isBlank(access_token) || StringUtils.isBlank(openid)){
            errorMsg = String.format("微信获取userInfo请求参数有误, 某参数为空: access_token[%s], openid[%s]",
                    access_token, openid);
            Logger.error(errorMsg);

        } else {

            String cachedOpenId = accessTokenToOpenIdMap.get(access_token);
            if(cachedOpenId == null) {
                errorMsg = "根据accessToken没有找到openId";
            } else {
                if(!cachedOpenId.equals(openid)) {
                    errorMsg = "openId值与预期不相等";
                } else {
                    results.put("openid", openid);
                    results.put("nickname", generateUsername());
                    results.put("sex", 1);
                    results.put("province", "广西");
                    results.put("city", "桂林");
                    results.put("country", "CN");
                    results.put("headimgurl", "");
                    results.put("unionid", cachedOpenId);
                }
            }

        }

        if(errorMsg != null) {
            results.put("errcode", 10086);
            results.put("errmsg", errorMsg);
        }

        return ok(JsonUtils.object2Node(results));

    }

    private String generateUsername() {
        //50%几率生成过长的字符串
        if(new Random().nextBoolean()) {
            return RandomStringUtils.randomAlphabetic(30);
        } else {
            return "张三_" + RandomStringUtils.randomNumeric(6);
        }
    }


    private String generateCode() {

        String code = RandomStringUtils.randomAlphabetic(16);
        String accessToken =  RandomStringUtils.randomAlphabetic(32);
        String unionId =  RandomStringUtils.randomAlphabetic(32);
        codeToAccessTokenMap.put(code, accessToken);
        accessTokenToOpenIdMap.put(accessToken, unionId);
        return code;
    }


}