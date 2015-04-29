package usercenter.utils;

import common.utils.DateUtils;
import org.joda.time.DateTime;
import play.Play;
import play.mvc.Http;
import usercenter.cache.UserCache;
import usercenter.models.User;

/**
 * Created by liubin on 15-4-27.
 */
public class SessionUtils {

    public static final String SESSION_USER_ID = "sk_uid";
    public static final String SESSION_USER_EXPIRATION = "sk_expiration";
    public static final String ORIGINAL_URL = "sk_original_url";
    public static final int REMEMBER_ME_DAYS = 14;
    public static final String USER_KEY = "sk_user";

    public static User currentUser() {

        if(Http.Context.current().args.get(USER_KEY) != null) {
            return (User)Http.Context.current().args.get(USER_KEY);
        }

        Http.Session session = Http.Context.current().session();
        String userIdStr = session.get(SESSION_USER_ID);
        if(userIdStr == null) {
            return null;
        }
        Integer userId = Integer.parseInt(userIdStr);

        String expireTime = session.get(SESSION_USER_EXPIRATION);
        //如果没有remember me或者remember me已经到期, 则需要校验session有没有过期.
        if(expireTime == null || DateUtils.current().isAfter(Long.parseLong(expireTime))) {
            //校验session是否超时
            Long userRequestTime = UserCache.getLastUserRequestTime(userId);
            if(userRequestTime == null) {
                clear();
                return null;
            }
            int sessionTimeoutInMinutes = Play.application().configuration().getInt("shop.sessionTimeout", 30);
            if(DateUtils.current().isAfter(userRequestTime + sessionTimeoutInMinutes * 60 * 1000L)) {
                //超时
                clear();
                return null;
            }
        }

        User user = UserCache.getUser(userId);
        UserCache.setUserRequestTime(userId, DateUtils.current().getMillis());
        Http.Context.current().args.put(USER_KEY, user);

        return user;

    }

    public static void setCurrentUser(User user, boolean rememberMe) {
        //设置到cookie
        Integer userId = user.getId();
        Http.Session session = Http.Context.current().session();
        session.put(SESSION_USER_ID, String.valueOf(userId));
        if(rememberMe) {
            DateTime current = DateUtils.current();
            long expirationTime = current.plusDays(REMEMBER_ME_DAYS).getMillis();
            session.put(SESSION_USER_EXPIRATION, String.valueOf(expirationTime));
        } else {
            session.remove(SESSION_USER_EXPIRATION);
        }

        //设置到缓存
        UserCache.setUser(user);
        UserCache.setUserRequestTime(userId, DateUtils.current().getMillis());

    }

    public static void clear() {
        Http.Session session = Http.Context.current().session();
        session.remove(SESSION_USER_ID);
        session.remove(SESSION_USER_EXPIRATION);

    }

    public static void setOriginalUrl(String uri) {
        Http.Context.current().session().put(ORIGINAL_URL, uri);
    }

    public static String getOriginalUrl() {
        return Http.Context.current().session().get(ORIGINAL_URL);
    }
}
