package common.utils;

/**
 * Created by zhb on 15-5-15.
 */
public class StringUtils {

    public static String getSecurityMobile(String tel){
        return tel.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }

}
