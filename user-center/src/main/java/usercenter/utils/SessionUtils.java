package usercenter.utils;

import common.exceptions.AppException;
import common.utils.AES;
import common.utils.DateUtils;
import common.utils.test.EncryptUtil;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import play.Logger;
import play.Play;
import play.mvc.Http;
import usercenter.cache.UserCache;
import usercenter.models.User;

import java.util.Base64;

/**
 * Created by liubin on 15-4-27.
 */
public class SessionUtils {

    public static final String SESSION_CREDENTIALS = "sk_credentials";
    public static final String SESSION_ORIGINAL_URL = "sk_original_url";
    public static final String SESSION_REQUEST_TIME = "sk_request_time";
    public static final int REMEMBER_ME_DAYS = 14;
    public static final String USER_KEY = "sk_user";
    public static final String COOKE_REMEMBER_ME = "sk_rm";

    public static User currentUser() {

        if(Http.Context.current().args.get(USER_KEY) != null) {
            return (User)Http.Context.current().args.get(USER_KEY);
        }

        Http.Session session = Http.Context.current().session();
        String credentials = session.get(SESSION_CREDENTIALS);
        Integer userId = getUserFromCredentials(credentials);
        if(userId == null) {
            return null;
        }

        Integer userFromRememberMe = getUserFromRememberMe();
        //如果没有remember me, 或者remember的用户不等于当前用户, 则需要校验session有没有过期.
        if(userFromRememberMe == null || !userFromRememberMe.equals(userId)) {
            //校验session是否超时
//            Long userRequestTime = UserCache.getLastUserRequestTime(userId);
            String userRequestTimeStr = session.get(SESSION_REQUEST_TIME);
            if(userRequestTimeStr == null) {
                clear();
                return null;
            }
            long userRequestTime = Long.parseLong(userRequestTimeStr);
            int sessionTimeoutInMinutes = Play.application().configuration().getInt("shop.sessionTimeout", 30);
            if(DateUtils.current().isAfter(userRequestTime + sessionTimeoutInMinutes * 60 * 1000L)) {
                //超时
                clear();
                return null;
            }
        }

        User user = UserCache.getUser(userId);
//        UserCache.setUserRequestTime(userId, DateUtils.current().getMillis());
        session.put(SESSION_REQUEST_TIME, String.valueOf(DateUtils.current().getMillis()));
        Http.Context.current().args.put(USER_KEY, user);

        return user;

    }

    public static void setCurrentUser(User user, boolean rememberMe) {
        //设置到cookie
        Http.Session session = Http.Context.current().session();
        Integer userId = user.getId();
        String credentials = buildCredentials(userId);
        session.put(SESSION_CREDENTIALS, credentials);
        if(rememberMe) {

            pleaseRememberMe(credentials, REMEMBER_ME_DAYS);

        } else {
            clearRememberMe();
        }

        session.put(SESSION_REQUEST_TIME, String.valueOf(DateUtils.current().getMillis()));

        //设置到缓存
        UserCache.setUser(user);
//        UserCache.setUserRequestTime(userId, DateUtils.current().getMillis());

    }

    private static String buildCredentials(Integer userId) {
        return String.valueOf(userId);
    }

    private static Integer getUserFromCredentials(String credentials) {
        if(StringUtils.isBlank(credentials)) return null;
        return Integer.parseInt(credentials);
    }


    private static void pleaseRememberMe(String credentials, int rememberMeDays) {

        long expireTime = DateUtils.current().plusDays(rememberMeDays).getMillis();
        int cookieMaxAge = rememberMeDays * 24 * 3600;
        String cookieValue = credentials + "," + String.valueOf(expireTime);
        try {
            Http.Context.current().response().setCookie(COOKE_REMEMBER_ME, EncryptUtil.encrypt(cookieValue), cookieMaxAge);
        } catch (AppException e) {
            Logger.error("加密cookie的时候发生错误", e);
        }

    }

    private static void clearRememberMe() {
        Http.Context.current().response().discardCookie(COOKE_REMEMBER_ME);
    }

    public static Integer getUserFromRememberMe() {
        Http.Cookie cookie = Http.Context.current().request().cookie(COOKE_REMEMBER_ME);
        if(cookie == null || StringUtils.isBlank(cookie.value())) return null;
        try {
            String cookieValue = EncryptUtil.decrypt(cookie.value());
            String[] array = cookieValue.split(",");
            String credentials = array[0];
            long expireTime = Long.parseLong(array[1]);
            if(new DateTime(expireTime).isBeforeNow()) {
                //已超时
                clearRememberMe();
                return null;
            }

            return getUserFromCredentials(credentials);
        } catch (AppException e) {
            Logger.error("解密cookie的时候发生错误", e);
            clearRememberMe();
            return null;
        }
    }



    public static void clear() {
        Http.Session session = Http.Context.current().session();
        session.remove(SESSION_CREDENTIALS);
        session.remove(SESSION_REQUEST_TIME);

    }

    public static void setOriginalUrl(String uri) {
        Http.Context.current().session().put(SESSION_ORIGINAL_URL, uri);
    }

    public static String getOriginalUrl() {
        return Http.Context.current().session().get(SESSION_ORIGINAL_URL);
    }
}
