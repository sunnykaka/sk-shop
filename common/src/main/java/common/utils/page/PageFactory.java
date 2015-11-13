package common.utils.page;


import common.models.utils.EntityClass;
import common.utils.ParamUtils;
import org.apache.commons.lang3.StringUtils;
import play.mvc.Http;

import java.util.HashMap;
import java.util.Map;

/**
 * User: liubin
 * Date: 14-3-11
 */
public class PageFactory {

    public static <T extends EntityClass> Page<T> getPage(Http.Request request) {

        int pageNo = getKey(request, "pageNo", "page", 1);
        int pageSize = getKey(request, "pageSize", "limit", Page.DEFAULT_PAGE_SIZE);
        return new Page<>(pageNo, pageSize);

    }

    private static int getKey(Http.Request request, String key1, String key2, int defaultValue) {
        String value = ParamUtils.getByKey(request, key1);
        if(StringUtils.isBlank(value)) {
            value = ParamUtils.getByKey(request, key2);
        }
        if(StringUtils.isNotBlank(value)) {
            try {
                defaultValue = Integer.valueOf(value);
            }catch (Exception e){
                //异常不处理
            }
        }
        return defaultValue;
    }

}
