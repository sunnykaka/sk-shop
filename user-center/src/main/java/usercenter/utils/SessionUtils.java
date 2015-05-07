package usercenter.utils;

import common.exceptions.AppException;
import common.utils.DateUtils;
import common.utils.EncryptUtil;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import play.Logger;
import play.Play;
import play.mvc.Http;
import usercenter.cache.UserCache;
import usercenter.models.User;

/**
 * Created by liubin on 15-4-27.
 */
public class SessionUtils {

    public static final String SESSION_CREDENTIALS = "passport";
    public static final String SESSION_ORIGINAL_URL = "origin";
    public static final String SESSION_REQUEST_TIME = "rtime";
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

        User user = UserCache.getUserInSession(userId);
        session.put(SESSION_REQUEST_TIME, String.valueOf(DateUtils.current().getMillis()));
        Http.Context.current().args.put(USER_KEY, user);

        return user;

    }

    public static void setCurrentUser(User user, boolean rememberMe) throws AppException {
        //设置到cookie
        Http.Session session = Http.Context.current().session();
        Integer userId = user.getId();
        String credentials = buildCredentials(userId);
        session.put(SESSION_CREDENTIALS, credentials);

        if(rememberMe) {
            pleaseRememberMe(credentials, REMEMBER_ME_DAYS);
        }

        session.put(SESSION_REQUEST_TIME, String.valueOf(DateUtils.current().getMillis()));

        //设置到缓存
        UserCache.setUserInSession(user, REMEMBER_ME_DAYS * 24 * 3600);

    }

    private static String buildCredentials(Integer userId) throws AppException {
        return EncryptUtil.encrypt(String.valueOf(userId));
    }

    private static Integer getUserFromCredentials(String credentials){
        if(StringUtils.isBlank(credentials)) return null;
        try {
            return Integer.parseInt(EncryptUtil.decrypt(credentials));
        } catch (AppException e) {
            Logger.error("", e);
        }
        return null;
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

    public static void clearRememberMe() {
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


    private static void clear() {
        Http.Session session = Http.Context.current().session();
        session.remove(SESSION_CREDENTIALS);
        session.remove(SESSION_REQUEST_TIME);
    }

    public static void logout(User user) {
        clearRememberMe();
        clear();
    }

    public static void setOriginalUrl(String uri) {
        Http.Context.current().session().put(SESSION_ORIGINAL_URL, uri);
    }

    public static String getOriginalUrlOrDefault(String defaultUrl) {
        String originalUrl = Http.Context.current().session().get(SESSION_ORIGINAL_URL);
        if(StringUtils.isBlank(originalUrl)) {
            return defaultUrl;
        } else {
            return originalUrl;
        }
    }
}
