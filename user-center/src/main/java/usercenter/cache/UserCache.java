package usercenter.cache;

import common.utils.DateUtils;
import common.utils.RedisUtils;
import common.utils.play.BaseGlobal;
import play.cache.CacheApi;
import usercenter.domain.SmsSender;
import usercenter.models.User;

/**
 * Created by liubin on 15-4-27.
 */
public class UserCache {

    private static CacheApi cacheApi() {
        return BaseGlobal.injector.instanceOf(CacheApi.class);
    }

    public static void setUserInSession(User user, int expiration) {

        cacheApi().set(RedisUtils.buildKey("session", "users", String.valueOf(user.getId())), user, expiration);

    }

    public static User getUserInSession(Integer userId) {

        return (User)cacheApi().get(RedisUtils.buildKey("session", "users", String.valueOf(userId)));

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
        return (String)cacheApi().get(RedisUtils.buildKey("register_phone", phone, usage.toString()));
    }

    public static void setPhoneVerificationCode(String phone, SmsSender.Usage usage, String value, int expiration) {
        cacheApi().set(RedisUtils.buildKey("register_phone", phone, usage.toString()), value, expiration);
    }

    private static String toadyInString() {
        return DateUtils.print(DateUtils.current(), "yyyyMMdd");
    }




}
