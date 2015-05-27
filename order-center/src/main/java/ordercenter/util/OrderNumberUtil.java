package ordercenter.util;

import common.services.GeneralDao;
import common.utils.play.BaseGlobal;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import play.cache.Cache;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 使用 getOrderNo() 生成订单编号<br/>
 * 由多个部分组成, 且受限于 Long 的最大值 &lt;9223372036854775807&gt;<br/><br/>
 * btw: 调用此方法之前, 查询一下数据库的最大值并使用 setIncrementNum 进行赋值, 避免因为系统重启而导致订单号重复无法生成订单的错误.<br/><br/>
 *
 * <span style="color:red;">btw:如果在集群模式下, 要更改新的规则, 如配置机器编号等.</span>
 *
 * <br/>亦可以使用 SQL 中的自定义 getOrderNo() 函数进行数据库获取.
 *
 * User: lidujun
 * Date: 2015-04-29
 */
public class OrderNumberUtil {
    public static final String CUR_ORDER_NO_KEY = "CUR_ORDER_NO_KEY";

    /** 计数的初始值 */
    private static final int INIT_INCREMENT_NUM = 1;

    /** 前缀, 年月日格式 */
    private static final String PREFIX_PATTERN = "yyyyMMdd";

    /** 自增时需要用到锁. */
    private static final ReentrantLock LOCK = new ReentrantLock();

    /** 机器码 */
    private static AtomicInteger machineCode = new AtomicInteger(18);

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
     * 3. 机器码, 2 位. <br/>
     */
    public static long getOrderNo() {
        long curOrderNo = getCurOrderNo();
        return NumberUtils.toLong(getStringFromNow() + curOrderNo + machineCode);
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
                curOrderNo = getCurDbOrderNo();
            }
            Cache.set(OrderNumberUtil.CUR_ORDER_NO_KEY, curOrderNo + 1);
            return curOrderNo;
        } finally {
            LOCK.unlock();
        }
    }

    /**
     * 获取当前数据库中的最大的订单值
     */
    public static long getCurDbOrderNo() {
        long ret = 0L;
        String sqlStr = "select orderNo from OrderTable where id = (select max(id) from OrderTable)";
        GeneralDao dao = BaseGlobal.ctx.getBean(GeneralDao.class);
        EntityManager em = dao.getEm();
        Query query = em.createNativeQuery(sqlStr);
        Object obj = query.getSingleResult();
        if (obj != null && obj instanceof Long) {
            long maxOrderNo = (Long) obj;
            // 去掉前面的年月日
            String orderNo = String.valueOf(maxOrderNo);
            if (StringUtils.isBlank(orderNo))
                ret = INIT_INCREMENT_NUM;
            else if (orderNo.length() < PREFIX_PATTERN.length())
                ret = NumberUtils.toInt(orderNo);
            else
                // 传入订单编号赋值给 计数器, 截取后面的数字放在此处进行操作.
                ret =NumberUtils.toInt(orderNo.substring(PREFIX_PATTERN.length()));
        }
        return ret;
    }
}
