package controllers.order;

import base.PrepareOrderData;
import common.models.utils.EntityClass;
import common.play.plugin.RedisPlugin;
import common.utils.DateUtils;
import common.utils.JsonUtils;
import common.utils.RedisUtils;
import controllers.test.routes;
import ordercenter.constants.OrderStatus;
import ordercenter.constants.PlatformType;
import ordercenter.models.Order;
import ordercenter.services.OrderService;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Ignore;
import org.junit.Test;
import play.cache.Cache;
import play.mvc.Result;
import play.test.FakeRequest;
import play.test.WithApplication;
import redis.clients.jedis.Jedis;
import utils.Global;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static play.test.Helpers.*;


/**
 * Created by liubin on 15-4-2.
 */
public class OrderControllerTest extends WithApplication implements PrepareOrderData {

    @Test
    public void testCreateOrderByJson() throws Exception {

        Order order = new Order();
        order.setOrderNo(RandomStringUtils.randomNumeric(8));
        order.setPlatformType(PlatformType.WEB);
        order.setStatus(OrderStatus.WAIT_PROCESS);
        order.setCreateTime(DateUtils.current());
        order.setUpdateTime(DateUtils.current());

        String json = JsonUtils.object2Json(order);
        System.out.println(json);

//        FakeRequest request = new FakeRequest(POST, controllers.order.routes.OrderController.createOrderByJson().url()).
//                withJsonBody(JsonUtils.OBJECT_MAPPER.readTree(json));
//        Result result = route(request);
//        assertThat(status(result), is(OK));
//        assertThat(contentType(result), is("application/json"));
//        assertThat(contentAsString(result), is("{\"result\":\"ok\"}"));


    }

    @Test
    public void testViewOrderByJson() throws Exception {

        prepareOrders(1, 3);

        Integer orderId = doInTransactionWithGeneralDao(generalDao -> {

            List<Integer> results = generalDao.query("select o.id from Order o", Optional.empty(), new HashMap<>());
            return results.get(0);

        });

        FakeRequest request = new FakeRequest(POST, controllers.order.routes.OrderController.viewOrderByJson(orderId).url());
        Result result = route(request);
        System.out.println(contentAsString(result));
        assertThat(status(result), is(OK));
        assertThat(contentType(result), is("application/json"));
//        assertThat(contentAsString(result), is("{\"result\":\"ok\"}"));

    }





}
