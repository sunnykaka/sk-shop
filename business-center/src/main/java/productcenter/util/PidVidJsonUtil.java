package productcenter.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import common.utils.JsonUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

/**
 * PidVidJson工具类
 * User: lidujun
 * Date: 2015-04-27
 */
public class PidVidJsonUtil {

    private static final Log logger = LogFactory.getLog(PidVidJsonUtil.class);

    /**
     * 将pidvid对象转为json字符串
     *
     * @param pidVid
     * @return
     */
    public static String toJson(PidVid pidVid) {
        return JsonUtils.object2Json(pidVid);
    }

    /**
     * 将Json还原为pidvid对象
     *
     * @param json
     * @return
     */
    public static PidVid restore(String json) {
        ObjectMapper mapper = new ObjectMapper();
        PidVid pidVid;
        try {
            pidVid = mapper.readValue(json, PidVid.class);
        } catch (IOException e) {
            logger.error("不能解析PidVid Json字符串", e);
            throw new RuntimeException("不能解析PidVid JSON字符串", e);
        }
        return pidVid;
    }

}
