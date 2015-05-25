package common.utils;

import org.apache.commons.lang3.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * Created by liubin on 15-5-22.
 */
public class UrlUtils {

    /**
     * 创建查询参数字符串
     * @param params
     * @return
     * @throws java.io.UnsupportedEncodingException
     */
    public static String buildQueryString(Map<String, List<String>> params) {
        if(params == null || params.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String, List<String>> entry : params.entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();
            if(values == null || values.isEmpty()) {
                continue;
            }
            for (String value : values) {
                sb.append(key).append("=");
                if(!org.apache.commons.lang3.StringUtils.isBlank(value)) {
                    try {
                        sb.append(URLEncoder.encode(value, "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        play.Logger.error("", e);
                    }
                }
                sb.append("&");
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }


}
