package controllers.api;

import com.fasterxml.jackson.databind.JsonNode;
import common.utils.JsonUtils;
import controllers.BaseController;
import play.mvc.Controller;
import play.mvc.Result;
import usercenter.models.User;
import utils.secure.SecuredAction;

import java.util.HashMap;
import java.util.Map;

@org.springframework.stereotype.Controller
public class TestApiController extends BaseController {

    private Map<String, String> map = new HashMap<String, String>() {{
        put("key1", "value1");
        put("key2", "value2");
    }};

    public Result publicResource() {

        JsonNode jsonNode = JsonUtils.object2Node(map);
        return ok(jsonNode);
    }

    @SecuredAction
    public Result protectedResource() {

        Map<String, String> newMap = new HashMap<>(map);
        User user = currentUser();
        newMap.put("phone", user.getPhone());
        return ok(JsonUtils.object2Node(newMap));
    }
}