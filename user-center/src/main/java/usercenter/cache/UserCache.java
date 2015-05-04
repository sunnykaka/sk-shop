package usercenter.cache;

import common.utils.DateUtils;
import common.utils.RedisUtils;
import play.Play;
import play.cache.Cache;
import usercenter.domain.SmsSender;
import usercenter.models.User;

/**
 * Created by liubin on 15-4-27.
 */
public class UserCache {

    public static void setUserInSession(User user, int expiration) {

        Cache.set(RedisUtils.buildKey("session", "users", String.valueOf(user.getId())), user, expiration);

    }

    public static User getUserInSession(Integer userId) {

        return (User)Cache.get(RedisUtils.buildKey("session", "users", String.valueOf(userId)));

    }

    public static int getMessageSendTimesInDay(String phone, SmsSender.Usage usage) {

        String count = RedisUtils.withJedisClient(jedis ->
            jedis.get(RedisUtils.buildKey("message_send_times", phone, usage.toString(), toadyInString()))
        );
        if(count == null) {
            return 0;
        } else {
            return Integer.parseInt(count);
        }

    }

    public static void setMessageSendTimesInDay(String phone, SmsSender.Usage usage) {

        RedisUtils.withJedisClient(jedis -> {
            jedis.incr(RedisUtils.buildKey("message_send_times", phone, usage.toString(), toadyInString()));
            return null;
        });

    }

    public static String getPhoneVerificationCode(String phone, SmsSender.Usage usage) {
        return (String)Cache.get(RedisUtils.buildKey("register_phone", phone, usage.toString()));
    }

    public static void setPhoneVerificationCode(String phone, SmsSender.Usage usage, String value, int expiration) {
        Cache.set(RedisUtils.buildKey("register_phone", phone, usage.toString()), value, expiration);
    }

    private static String toadyInString() {
        return DateUtils.print(DateUtils.current(), "yyyyMMdd");
    }


//    public static void setUserRequestTime(Integer userId, long requestTime) {
//        Cache.set(RedisUtils.buildKey("session", String.valueOf(userId)), requestTime);
//    }
//
//    public static Long getLastUserRequestTime(Integer userId) {
//        return (Long)Cache.get(RedisUtils.buildKey("session", String.valueOf(userId)));
//    }




}
