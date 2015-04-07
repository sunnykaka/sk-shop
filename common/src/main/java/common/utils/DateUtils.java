package common.utils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * 日期时间处理工具
 * User: Asion
 * Date: 12-3-15
 * Time: 下午2:36
 */
public class DateUtils {

    /**
     * 获取当前系统时间(原始格式)
     */
    public static DateTime current() {
        return new DateTime();
    }

    public static DateTime parseDateTime(String str) {
        return parse(str, "yyyy-MM-dd HH:mm:ss");
    }

    public static DateTime parseDate(String str) {
        return parse(str, "yyyy-MM-dd");
    }

    public static DateTime parse(String str, String pattern) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(pattern);
        return formatter.parseDateTime(str);
    }

    public static String printDateTime(DateTime dateTime) {
        return print(dateTime, "yyyy-MM-dd HH:mm:ss");
    }

    public static String printDate(DateTime dateTime) {
        return print(dateTime, "yyyy-MM-dd");
    }

    /**
     * 判断两个日期是否相等,忽略毫秒,因为mysql不存储毫秒
     * @param dateTime1
     * @param dateTime2
     * @return
     */
    public static boolean equals(DateTime dateTime1, DateTime dateTime2) {
        if(dateTime1 == null || dateTime2 == null) return false;
        return dateTime1.withMillisOfSecond(0).equals(dateTime2.withMillisOfSecond(0));
    }

    public static String print(DateTime dateTime, String pattern) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(pattern);
        return formatter.print(dateTime);
    }


}
