package usercenter.cache;

import common.utils.RedisUtils;
import play.cache.Cache;

/**
 * Created by zhb on 15-4-30.
 */
public class SecurityCache {

    public static int SECURITY_TOKEN_EXPIRE_TIME = 60 * 60 * 24;

    public static final String SECURITY_TOKEN_EMAIL_ACTIVITY_KEY = "activity";

    public static final String SECURITY_TOKEN_EMAIL_CHANGE_KEY = "change";

    public static final String SECURITY_TOKEN_PHONE_KEY = "changePhone";

    /**
     * 添加
     * @param key
     * @param value
     */
    public static void setToken(String staticKey, String key, String value) {

        Cache.set(RedisUtils.buildKey("token", staticKey, key), value, SECURITY_TOKEN_EXPIRE_TIME);

    }

    /**
     * 获取
     * @param key
     * @return
     */
    public static String getToken(String staticKey, String key) {

        return (String) Cache.get(RedisUtils.buildKey("token", staticKey, key));

    }

    /**
     * 删除
     * @param key
     */
    public static void removeToken(String staticKey, String key){

        Cache.remove(RedisUtils.buildKey("token", staticKey, key));

    }
}
