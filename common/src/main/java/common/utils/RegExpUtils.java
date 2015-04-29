package common.utils;


import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * Created by liubin on 15-4-27.
 */
public class RegExpUtils {

    private static Pattern PHONE_PATTERN = Pattern.compile("^[1][\\d]{10}");

    private static Pattern EMAIL_PATTERN = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");

    public static boolean isPhone(String str) {
        if(StringUtils.isBlank(str)) {
            return false;
        }

        return PHONE_PATTERN.matcher(str).matches();

    }

    public static boolean isEmail(String str) {
        if(StringUtils.isBlank(str)) {
            return false;
        }

        return EMAIL_PATTERN.matcher(str).matches();

    }

    public static void main(String[] args) {
        String phone1 = "18622223333";
        String email1 = "123@1.com";
        String email2 = "123@.com";

        System.out.println(isPhone(phone1));
        System.out.println(isEmail(email1));
        System.out.println(isEmail(email2));

    }

}
