package common.cache;

import static common.utils.RedisUtils.*;
import static common.utils.DateUtils.*;
/**
 * Created by liubin on 15-10-20.
 */
public class ConfigCache {

    public static enum NoticeType {
        SMS_SEND_BEYOND_LIMIT
    }


    /**
     * 设置发送报警消息标识
     * @param noticeType
     * @return 判断如果今天已发送过，返回false，否则设置并返回true
     */
    public static boolean trySetNoticeAdminFlag(NoticeType noticeType) {

        return withJedisClient(jedis -> {
            Long result = jedis.setnx(buildKey("notice_admin", noticeType.toString(), toadyInString()), "true");
            return result != null && result == 1L;
        });

    }


}
