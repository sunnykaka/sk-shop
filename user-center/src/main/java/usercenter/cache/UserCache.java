package usercenter.cache;

import com.google.common.collect.Sets;
import common.utils.DateUtils;
import common.utils.RedisUtils;
import common.utils.play.BaseGlobal;
import org.apache.commons.lang3.StringUtils;
import play.cache.CacheApi;
import usercenter.domain.SmsSender;
import usercenter.models.User;

import java.util.Set;

/**
 * Created by liubin on 15-4-27.
 */
public class UserCache {

    private static CacheApi cacheApi() {
        return BaseGlobal.injector.instanceOf(CacheApi.class);
    }

    public static Set<String> UNLIMITED_IP_SET = Sets.newHashSet("localhost", "127.0.0.1");

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

    public static void incrMessageSendTimesInDay(String phone, SmsSender.Usage usage) {

        RedisUtils.withJedisClient(jedis -> {
            jedis.incr(RedisUtils.buildKey("message_send_times", phone, usage.toString(), toadyInString()));
            return null;
        });

    }

//    public static void incrMessageSendIpCountInDay(String ip, SmsSender.Usage usage) {
//
//        if(StringUtils.isNoneBlank(ip) && !UNLIMITED_IP_SET.contains(ip)) {
//            RedisUtils.withJedisClient(jedis -> {
//                jedis.incr(RedisUtils.buildKey("message_send_ip", ip, usage.toString(), toadyInString()));
//                return null;
//            });
//        }
//
//    }
//
//    public static int getMessageSendIpCountInDay(String ip, SmsSender.Usage usage) {
//
//        if(StringUtils.isBlank(ip)) {
//            return 0;
//        }
//
//        String count = RedisUtils.withJedisClient(jedis ->
//            jedis.get(RedisUtils.buildKey("message_send_ip", ip, usage.toString(), toadyInString()))
//        );
//        if(count == null) {
//            return 0;
//        } else {
//            return Integer.parseInt(count);
//        }
//
//    }

    public static void incrAllMessageSendTimesInDay(SmsSender.Usage usage) {

        RedisUtils.withJedisClient(jedis -> {
            jedis.incr(RedisUtils.buildKey("all_message_send", usage.toString(), toadyInString()));
            return null;
        });

    }

    public static int getAllMessageSendTimesInDay(SmsSender.Usage usage) {

        String count = RedisUtils.withJedisClient(jedis ->
            jedis.get(RedisUtils.buildKey("all_message_send", usage.toString(), toadyInString()))
        );
        if(count == null) {
            return 0;
        } else {
            return Integer.parseInt(count);
        }

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
