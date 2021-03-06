package common.utils;


import common.exceptions.AppBusinessException;
import common.exceptions.ErrorCode;
import play.mvc.Http;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhb on 15-4-30.
 */
public class ParamUtils {

    /**
     * 取多个参数
     *
     * @param request
     * @param key
     * @return
     */
    private static String[] getAllByKey(Http.Request request,String key) {

        Map<String, String[]> params = request.body().asFormUrlEncoded();
        if(params == null) {
            params = new HashMap<>();
        }

        return params.getOrDefault(key, new String[0]);

    }

    /**
     * 取单个参数
     *
     * @param request
     * @param key
     * @return
     */
    public static String getByKey(Http.Request request,String key) {

        String s = request.getQueryString(key);

        if(org.apache.commons.lang3.StringUtils.isNotBlank(s)) {

            return s;
        } else {

            String[] result = getAllByKey(request, key);

            if(result.length == 0){
                return "";
            }

            return result[0];
        }

    }

    /**
     * 单个POST取id参数
     *
     * @param request
     * @return
     */
    public static int getObjectId(Http.Request request){

        String objectId = getByKey(request, "id");

        try {
            return Integer.parseInt(objectId);
        }catch (Exception e){
            throw new AppBusinessException(ErrorCode.Conflict, "id传参错误");
        }
    }

}
