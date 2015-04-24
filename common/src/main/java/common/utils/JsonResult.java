package common.utils;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liubin on 15-4-24.
 */
public class JsonResult {

    private boolean result;

    private Object data;

    private String message;

    public JsonResult(boolean result) {
        this(result, null, null);
    }

    public JsonResult(boolean result, String message) {
        this(result, null, message);
    }

    public JsonResult(boolean result, Object data) {
        this(result, data, null);
    }

    public JsonResult(boolean result, Object data, String message) {
        this.result = result;
        this.data = data;
        this.message = message;
    }

    public JsonNode toNode() {
        Map<String, Object> map = new HashMap<>();
        map.put("result", result);
        map.put("data", data);
        map.put("message", message);

        return JsonUtils.object2Node(map);
    }

}
