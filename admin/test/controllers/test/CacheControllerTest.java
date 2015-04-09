package controllers.test;

import org.junit.Test;
import play.test.FakeRequest;
import play.test.WithApplication;

/**
 * Created by liubin on 15-4-2.
 */
public class CacheControllerTest extends WithApplication {

    @Test
    public void test() {
        String key = "mytest";
        FakeRequest request = new FakeRequest("GET", routes.CacheController.getCache(key).url());

    }


}
