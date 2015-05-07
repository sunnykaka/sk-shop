package controllers.test;

import base.PrepareTestObject;
import common.play.plugin.RedisPlugin;
import common.utils.RedisUtils;
import ordercenter.models.TestObject;
import ordercenter.services.TestObjectService;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Ignore;
import org.junit.Test;
import play.cache.Cache;
import play.mvc.Result;
import play.test.FakeRequest;
import play.test.WithApplication;
import redis.clients.jedis.Jedis;
import utils.Global;

import java.util.List;

import static java.util.Optional.empty;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static play.test.Helpers.*;


/**
 * Created by liubin on 15-4-2.
 */
public class CacheControllerTest extends WithApplication implements PrepareTestObject {

    @Test
    @Ignore
    public void testCacheString() {
        String key = RandomStringUtils.randomAlphabetic(6);
        FakeRequest request = new FakeRequest(GET, routes.CacheController.getCache(key).url());
        Result result = route(request);
        assertThat(status(result), is(OK));
        assertThat(contentAsString(result), is("set default value"));

        String value = "你好";
        request = new FakeRequest(PUT, routes.CacheController.setCache(key, value).url());
        result = route(request);
        assertThat(status(result), is(OK));
        assertThat(contentAsString(result), is(value));

        key = "我是key";
        value = "张三";
        request = new FakeRequest(PUT, routes.CacheController.setCache(key, value).url());
        result = route(request);
        assertThat(status(result), is(OK));
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

        Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();

        try {
            /// ... do stuff here
            j.set("foo", "bar");
        } finally {
            play.Play.application().plugin(RedisPlugin.class).jedisPool().returnResource(j);
        }


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




}
