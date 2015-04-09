package common.utils;

import common.play.plugin.RedisPlugin;
import redis.clients.jedis.Jedis;

import java.util.function.Function;

/**
 * Created by liubin on 15-4-9.
 */
public class RedisUtils {

    public static <T> T withJedisClient(Function<Jedis, T> callback) {

        Jedis j = play.Play.application().plugin(RedisPlugin.class).jedisPool().getResource();

        try {
            T result = callback.apply(j);

            return result;
        } finally {
            play.Play.application().plugin(RedisPlugin.class).jedisPool().returnResource(j);
        }

    }

}
