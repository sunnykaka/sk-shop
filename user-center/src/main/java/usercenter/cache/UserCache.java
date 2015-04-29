package usercenter.cache;

import common.utils.RedisUtils;
import play.Play;
import play.cache.Cache;
import usercenter.models.User;

/**
 * Created by liubin on 15-4-27.
 */
public class UserCache {

    public static void setUser(User user) {

        Cache.set(RedisUtils.buildKey("users", String.valueOf(user.getId())), user);

    }

    public static User getUser(Integer userId) {

        return (User)Cache.get(RedisUtils.buildKey("users", String.valueOf(userId)));

    }

    public static int getPhoneRegisterTimeCount(String phone) {

        String count = (String)Cache.get(RedisUtils.buildKey("register_phone_count", phone));
        if(count == null) {
            return 0;
        } else {
            return Integer.parseInt(count);
        }

    }

    public static void setPhoneRegisterTimeCount(String phone, int count, int expiration) {

        Cache.set(RedisUtils.buildKey("register_phone_count", phone), count, expiration);

    }

    public static String getPhoneVerificationCode(String phone) {
        return (String)Cache.get(RedisUtils.buildKey("register_phone", phone));
    }

    public static void setPhoneVerificationCode(String phone, String value, int expiration) {
        Cache.set(RedisUtils.buildKey("register_phone", phone), value, expiration);
    }


//    public static void setUserRequestTime(Integer userId, long requestTime) {
//        Cache.set(RedisUtils.buildKey("session", String.valueOf(userId)), requestTime);
//    }
//
//    public static Long getLastUserRequestTime(Integer userId) {
//        return (Long)Cache.get(RedisUtils.buildKey("session", String.valueOf(userId)));
//    }




}
