package ordercenter.util;

import common.utils.RedisUtils;
import org.apache.commons.lang3.math.NumberUtils;
import play.cache.Cache;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 使用 getOrderNo() 生成订单编号<br/>
 *
 * User: lidujun
 * Date: 2015-04-29
 */
public class OrderNumberUtil {
    public static final String CUR_ORDER_NO_KEY = "CUR_ORDER_NO_KEY";

    /** 前缀, 年月日格式 */
    private static final String PREFIX_PATTERN = "yyyyMMdd";

    /** 自增时需要用到锁. */
    private static final ReentrantLock LOCK = new ReentrantLock();

    /**
     * 年月日, 异常则返回 空字符串
     */
    public static String getStringFromNow() {
        try {
            return new SimpleDateFormat(PREFIX_PATTERN).format(new Date());
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 订单号由多部分组成, 受限于 Long.MAX_VALUE, 只能有 19 位.<br/>
     * 1. 年月日, 8 位. <br/>
     * 2. 自增数, 最多 8 位. <br/>
     */
    public static long getOrderNo() {
        long curOrderNo = getCurOrderNo();
        return NumberUtils.toLong(getStringFromNow() + curOrderNo);
    }

    /**
     * 获取当前的订单号，且把加1的新值放到缓存中
     * @return
     */
    private static long getCurOrderNo() {
        LOCK.lock();
        try {
            Long curOrderNo = (Long) Cache.get(OrderNumberUtil.CUR_ORDER_NO_KEY);
            if(curOrderNo == null) {
                curOrderNo = getCurCacheOrderNo();
            }
            Cache.set(OrderNumberUtil.CUR_ORDER_NO_KEY, curOrderNo + 1);
            return curOrderNo;
        } finally {
            LOCK.unlock();
        }
    }

    /**
     * 获取当前缓存中的最大的订单值
     */
    public static long getCurCacheOrderNo() {
        long curValue =  RedisUtils.withJedisClient(jedis -> {
            String key = RedisUtils.buildKey(OrderNumberUtil.CUR_ORDER_NO_KEY);
            jedis.incr(key);
            long value = Long.valueOf(jedis.get(key));
            return value;
        });
        return curValue;
    }
}
