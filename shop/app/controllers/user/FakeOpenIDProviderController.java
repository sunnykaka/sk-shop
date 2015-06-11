package controllers.user;

import com.google.common.collect.Lists;
import common.utils.JsonUtils;
import common.utils.ParamUtils;
import common.utils.UrlUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import usercenter.domain.QQLogin;
import usercenter.domain.WeiboLogin;
import usercenter.domain.WeixinLogin;
import usercenter.services.UserService;

import java.util.HashMap;
import java.util.List;
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


    public Result weiboConnect(String client_id, String redirect_uri, String response_type, String state) {

        boolean error = false;
        if(StringUtils.isBlank(client_id) || StringUtils.isBlank(redirect_uri) || StringUtils.isBlank(response_type) ||
                StringUtils.isBlank(state)) {
            Logger.error(String.format("微博重定向请求参数有误, 某参数为空: client_id[%s], redirect_uri[%s], response_type[%s], scope[%s], state[%s]",
                    client_id, redirect_uri, response_type, state));
            error = true;
        }
        if(state.equals(ERROR_STATE)) {
            error = true;
        }

        String url = new WeiboLogin().redirectUrl;
        if(!error) {
            url += "?state=" + state + "&code=" + generateCode();
        } else {
            url += "?error=" + "error_happened" + "&error_code=" + 10086;
        }

        return redirect(url);

    }

    public Result weiboGetAccessToken(String client_id, String client_secret, String code, String grant_type, String redirect_uri) {

        String errorMsg = null;
        Map<String, Object> results = new HashMap<>();

        if(StringUtils.isBlank(client_id) || StringUtils.isBlank(client_secret) ||
                StringUtils.isBlank(code) || StringUtils.isBlank(grant_type) || StringUtils.isBlank(redirect_uri)) {
            errorMsg = String.format("微博获取accessToken请求参数有误, 某参数为空: appid[%s], secret[%s], code[%s], grant_type[%s], redirect_uri[%s]",
                    client_id, client_secret, code, grant_type, redirect_uri);
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
                    results.put("remind_in", 7200);
                    results.put("uid", openId);
                }
            }
        }

        if(errorMsg != null) {
            results.put("error_code", 10086);
            results.put("error", errorMsg);
        }

        return ok(JsonUtils.object2Node(results));

    }

    public Result weiboGetUserInfo(String access_token, String uid) {

        String errorMsg = null;
        Map<String, Object> results = new HashMap<>();

        if(StringUtils.isBlank(access_token) || StringUtils.isBlank(uid)){
            errorMsg = String.format("微博获取userInfo请求参数有误, 某参数为空: access_token[%s], uid[%s]",
                    access_token, uid);
            Logger.error(errorMsg);

        } else {

            String cachedOpenId = accessTokenToOpenIdMap.get(access_token);
            if(cachedOpenId == null) {
                errorMsg = "根据accessToken没有找到openId";
            } else {
                if(!cachedOpenId.equals(uid)) {
                    errorMsg = "openId值与预期不相等";
                } else {
                    results.put("id", Long.parseLong(uid));
                    results.put("screen_name", generateUsername());
                    results.put("gender", "f");
                }
            }

        }

        if(errorMsg != null) {
            results.put("error_code", 10086);
            results.put("error", errorMsg);
        }

        return ok(JsonUtils.object2Node(results));

    }


    public Result qqConnect(String client_id, String redirect_uri, String response_type, String state) {

        boolean error = false;
        if(StringUtils.isBlank(client_id) || StringUtils.isBlank(redirect_uri) || StringUtils.isBlank(response_type) ||
                StringUtils.isBlank(state)) {
            Logger.error(String.format("QQ重定向请求参数有误, 某参数为空: client_id[%s], redirect_uri[%s], response_type[%s], scope[%s], state[%s]",
                    client_id, redirect_uri, response_type, state));
            error = true;
        }
        if(state.equals(ERROR_STATE)) {
            error = true;
        }

        String url = new QQLogin().redirectUrl;
        if(!error) {
            url += "?state=" + state + "&code=" + generateCode();
        } else {
            url += "?msg=" + "error_happened" + "&code=" + 10086;
        }

        return redirect(url);

    }

    public Result qqGetAccessToken(String client_id, String client_secret, String code, String grant_type, String redirect_uri) {

        String errorMsg = null;
        Map<String, List<String>> results = new HashMap<>();

        if(StringUtils.isBlank(client_id) || StringUtils.isBlank(client_secret) ||
                StringUtils.isBlank(code) || StringUtils.isBlank(grant_type) || StringUtils.isBlank(redirect_uri)) {
            errorMsg = String.format("QQ获取accessToken请求参数有误, 某参数为空: appid[%s], secret[%s], code[%s], grant_type[%s], redirect_uri[%s]",
                    client_id, client_secret, code, grant_type, redirect_uri);
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
                    results.put("access_token", Lists.newArrayList(accessToken));
                    results.put("expires_in", Lists.newArrayList(String.valueOf(7200)));
                }
            }
        }

        if(errorMsg != null) {
            results.put("code", Lists.newArrayList(String.valueOf(10086)));
            results.put("msg", Lists.newArrayList(errorMsg));
        }

        return ok(UrlUtils.buildQueryString(results));

    }

    public Result qqGetUserInfo(String access_token) {

        String errorMsg = null;
        String result = null;

        if(StringUtils.isBlank(access_token)){
            errorMsg = String.format("QQ获取userInfo请求参数有误, 某参数为空: access_token[%s]",
                    access_token);
            Logger.error(errorMsg);

        } else {

            String cachedOpenId = accessTokenToOpenIdMap.get(access_token);
            if(cachedOpenId == null) {
                errorMsg = "根据accessToken没有找到openId";
            } else {
                result = String.format("callback( {\"client_id\":\"%s\",\"openid\":\"%s\"} ); ", "12345678", cachedOpenId);
            }

        }

        if(result == null) {
            Map<String, List<String>> results = new HashMap<>();
            results.put("code", Lists.newArrayList(String.valueOf(10086)));
            results.put("msg", Lists.newArrayList(errorMsg));
            return ok(UrlUtils.buildQueryString(results));
        } else {
            return ok(result);
        }

    }

    public Result qqGetUserDetailInfo(String oauth_consumer_key, String access_token, String openid, String format) {

        String errorMsg = null;
        Map<String, Object> results = new HashMap<>();

        if(StringUtils.isBlank(oauth_consumer_key) || StringUtils.isBlank(access_token) ||
                StringUtils.isBlank(openid) || StringUtils.isBlank(format)){
            errorMsg = String.format("QQ获取userInfo请求参数有误, 某参数为空: oauth_consumer_key[%s], access_token[%s], openid[%s], format[%s]",
                    oauth_consumer_key, access_token, openid, format);
            Logger.error(errorMsg);

        } else {
            String cachedOpenId = accessTokenToOpenIdMap.get(access_token);
            if(cachedOpenId == null) {
                errorMsg = "根据accessToken没有找到openId";
            } else {
                if(!cachedOpenId.equals(openid)) {
                    errorMsg = "openId值与预期不相等";
                } else {
                    results.put("ret", 0);
                    results.put("msg", "");
                    results.put("nickname", generateUsername());
                    results.put("gender", "女");
                    results.put("figureurl_qq_1", "http://qzapp.qlogo.cn/qzapp/100330589/A3D9931DEB0A57D5E8A73BF3E4991C40/50");
                }
            }

        }

        if(errorMsg != null) {
            results.put("ret", 10086);
            results.put("msg", errorMsg);
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
        String unionId =  RandomStringUtils.randomNumeric(8);
        codeToAccessTokenMap.put(code, accessToken);
        accessTokenToOpenIdMap.put(accessToken, unionId);
        return code;
    }


}