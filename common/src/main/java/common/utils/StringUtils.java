package common.utils;

/**
 * Created by zhb on 15-5-15.
 */
public class StringUtils {

    /**
     * 手机号码加密
     *
     * @param tel
     * @return
     */
    public static String getSecurityMobile(String tel) {
        return tel.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }

    /**
     * 用户名加密
     *
     * @param name
     * @return
     */
    public static String getSecurityName(String name) {

        if (null == name || "".equals(name)) {
            return name;
        }

        if (name.length() < 4) {
            return name.substring(0, 1) + "***" + name.substring(name.length() - 1, name.length());
        }

        if (name.length() >= 4) {
            return name.substring(0, 3) + "***" + name.substring(name.length() - 1, name.length());
        }

        return name;
    }

    /**
     * 截取字符串长度(中文2个字节，半个中文显示一个)
     *
     * @param str
     * @param len
     * @return
     */
    public static String subTextString(String str, int len) {
        if (str.length() < len / 2) {
            return str;
        }
        int count = 0;
        StringBuffer sb = new StringBuffer();
        String[] ss = str.split("");
        for (int i = 1; i < ss.length; i++) {
            count += ss[i].getBytes().length > 1 ? 2 : 1;
            sb.append(ss[i]);
            if (count >= len) break;
        }
        //不需要显示...的可以直接return sb.toString();
        return (sb.toString().length() < str.length()) ? sb.append("...").toString() : str;
    }

}
