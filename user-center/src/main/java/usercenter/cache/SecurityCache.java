package usercenter.cache;

import common.utils.RedisUtils;
import common.utils.play.BaseGlobal;
import play.cache.CacheApi;

/**
 * Created by zhb on 15-4-30.
 */
public class SecurityCache {

    public static int SECURITY_TOKEN_EXPIRE_TIME = 60 * 60 * 24;

    public static final String SECURITY_TOKEN_EMAIL_ACTIVITY_KEY = "emailActivity";

    public static final String SECURITY_TOKEN_PSW_CHANGE_KEY = "changePsw";

    public static final String SECURITY_TOKEN_PHONE_KEY = "changePhone";

    private static CacheApi cacheApi = BaseGlobal.injector.instanceOf(CacheApi.class);

    /**
     * 添加
     * @param key
     * @param value
     */
    public static void setToken(String staticKey, String key, String value) {

        cacheApi.set(RedisUtils.buildKey("token", staticKey, key), value, SECURITY_TOKEN_EXPIRE_TIME);

    }

    /**
     * 获取
     * @param key
     * @return
     */
    public static String getToken(String staticKey, String key) {

        return (String) cacheApi.get(RedisUtils.buildKey("token", staticKey, key));

    }

    /**
     * 删除
     * @param key
     */
    public static void removeToken(String staticKey, String key){

        cacheApi.remove(RedisUtils.buildKey("token", staticKey, key));

    }
}
