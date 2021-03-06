package controllers.test;

import base.BaseTest;
import base.PrepareTestObject;
import common.utils.RedisUtils;
import ordercenter.models.TestObject;
import ordercenter.services.TestObjectService;
import ordercenter.util.OrderNumberUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Ignore;
import org.junit.Test;
import play.cache.Cache;
import play.mvc.Http;
import play.mvc.Result;
import utils.Global;

import java.util.List;

import static java.util.Optional.empty;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static play.test.Helpers.*;


/**
 * Created by liubin on 15-4-2.
 */
public class CacheControllerTest extends BaseTest implements PrepareTestObject {

    @Test
    @Ignore
    public void testCacheString() {
        String key = RandomStringUtils.randomAlphabetic(6);
        Http.RequestBuilder request = new Http.RequestBuilder().method(GET).uri(routes.CacheController.getCache(key).url());
        Result result = route(request);
        assertThat(result.status(), is(OK));
        assertThat(contentAsString(result), is("set default value"));

        String value = "你好";
        request = new Http.RequestBuilder().method(PUT).uri(routes.CacheController.setCache(key, value).url());
        result = route(request);
        assertThat(result.status(), is(OK));
        assertThat(contentAsString(result), is(value));

        key = "我是key";
        value = "张三";
        request = new Http.RequestBuilder().method(PUT).uri(routes.CacheController.setCache(key, value).url());
        result = route(request);
        assertThat(result.status(), is(OK));
        assertThat(contentAsString(result), is(value));


    }

    @Test
    public void testCacheObject() {
        String key = RandomStringUtils.randomAlphabetic(6);
        Cache.set(key, 999);
        assertThat(Cache.get(key), is(999));


        prepareTestObjects(10, 3);

        TestObjectService testObjectService = Global.ctx.getBean(TestObjectService.class);
        List<TestObject> testObjects = testObjectService.findByKey(empty(), empty(), empty(), empty(), empty());
        assertThat(testObjects.size(), is(10));
        TestObject testObject = testObjects.get(0);
        key = RandomStringUtils.randomAlphabetic(6);
        Cache.set(key, testObject);
        TestObject testObjectRetrieve = (TestObject)Cache.get(key);

        assertThat(testObjectRetrieve.getId(), is(testObject.getId()));

        RedisUtils.withJedisClient(jedis -> {

            /// ... do stuff here
            jedis.set("foo", "bar");

            return null;
        });

    }

    @Test
    public void testWithJedisClient() {

        RedisUtils.withJedisClient(jedis -> {

            String key = RandomStringUtils.randomAlphabetic(6);
            String value = "你好啊";

            jedis.set(key, value);
            assertThat(jedis.get(key), is(value));

            return null;
        });


    }

    @Test
    public void testAutoInc() {
      long curValue =  RedisUtils.withJedisClient(jedis -> {
            String key = RedisUtils.buildKey(OrderNumberUtil.CUR_ORDER_NO_KEY);
            return jedis.incr(key);
        });
       System.out.println("----------------: " + curValue);
    }

}
