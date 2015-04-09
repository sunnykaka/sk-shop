package controllers.test;


import ordercenter.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import play.Logger;
import play.cache.Cache;
import play.mvc.Controller;
import play.mvc.Result;

@org.springframework.stereotype.Controller
public class CacheController extends Controller {

    @Autowired
    private OrderService orderService;


    public Result getCache(String key) {

        try {
            String value = Cache.getOrElse(key, () -> "default value", 0);
            return ok(value);
        } catch (Exception e) {
            Logger.error("", e);
            return internalServerError(e.getMessage());
        }
    }

    public Result setCache(String key, String value) {
        Cache.set(key, value);

        return ok("set ok");
    }

}