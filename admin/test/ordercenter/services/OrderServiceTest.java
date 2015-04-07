package ordercenter.services;

import base.PrepareOrderData;
import common.utils.DateUtils;
import common.utils.Money;
import common.utils.page.Page;
import ordercenter.constants.*;
import ordercenter.dtos.OrderSearcher;
import ordercenter.models.Order;
import ordercenter.models.OrderItem;
import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
import org.junit.Test;
import utils.Global;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

/**
 * Created by liubin on 15-4-2.
 */
public class OrderServiceTest implements PrepareOrderData{

    @Test
    public void testCRUDInOrder() {
        running(fakeApplication(), () -> {

            prepareOrders(0, 0);

            OrderService orderService = Global.ctx.getBean(OrderService.class);
            String orderNo = RandomStringUtils.randomNumeric(8);
            //创建订单
            Order order1 = new Order();
            Integer orderId = doInTransaction(em -> {
                order1.setOrderNo(orderNo);
                order1.setPlatformType(PlatformType.WEB);
                order1.setStatus(OrderStatus.WAIT_PROCESS);
                order1.setCreateTime(DateUtils.current());
                order1.setUpdateTime(DateUtils.current());

                em.persist(order1);
//            em.flush();

                assert order1.getId() != null;
                assert order1.getId() >= 0;
//            assertThat(order1.getId(), notNullValue());
//            assertThat(order1.getId(), greaterThan(0));

                try {
                    Thread.sleep(500L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                DateTime createTime2 = DateUtils.current();
                order1.setCreateTime(createTime2);
                em.flush();

                em.detach(order1);

                Order order2 = em.find(Order.class, order1.getId());
//            assertThat(order2.getCreateTime(), is(createTime2));
                assert DateUtils.equals(order2.getCreateTime(), createTime2);

                //创建订单项
                OrderItem orderItem = new OrderItem();
                orderItem.setOrderId(order1.getId());
                orderItem.setBuyCount(5);
                orderItem.setPlatformType(order1.getPlatformType());
                orderItem.setDiscountFee(Money.valueOf(10));
                orderItem.setPrice(Money.valueOf(20));
                orderItem.setStatus(OrderItemStatus.NOT_SIGNED);
                orderItem.setType(OrderItemType.PRODUCT);
                orderItem.setProductId(Integer.parseInt(RandomStringUtils.randomNumeric(8)));
                orderItem.setProductSku(RandomStringUtils.randomAlphabetic(8));

                em.persist(orderItem);

                return order1.getId();
            });

            //测试findByKey方法
            List<Order> orders = orderService.findByKey(empty(), of(orderNo), empty(), empty(), empty());
            assert orders.size() == 1;
            Order order = orders.get(0);
            assert order.getId().equals(orderId);

            orders = orderService.findByKey(empty(), of(RandomStringUtils.randomAlphanumeric(10)), empty(), empty(), empty());
            assert orders.size() == 0;
            orders = orderService.findByKey(empty(), empty(), empty(), empty(), empty());
            assert orders.size() > 0;

            orders = orderService.findByKey(empty(), empty(), empty(), of(order.getCreateTime()), of(order.getCreateTime()));
            assert orders.size() == 1;
            order = orders.get(0);
            assert order.getId().equals(orderId);

            orders = orderService.findByKey(empty(), empty(), empty(), of(order.getCreateTime()), empty());
            assert orders.size() == 1;
            order = orders.get(0);
            assert order.getId().equals(orderId);

        });
    }

    @Test
    public void testOrderPage() {
        running(fakeApplication(), () -> {

            OrderService orderService = Global.ctx.getBean(OrderService.class);

            prepareOrders(50, 3);

            //测试分页方法
            List<Order> orders = orderService.findByKey(of(new Page<>(1, Page.DEFAULT_PAGE_SIZE)), empty(), of(OrderStatus.WAIT_PROCESS), empty(), empty());
            assert orders.size() == Page.DEFAULT_PAGE_SIZE;
            orders = orderService.findByKey(of(new Page<>(2, 20)), empty(), empty(), empty(), empty());
            assert orders.size() == 20;


        });
    }

    @Test
    public void testFindByComplicateKey() {
        running(fakeApplication(), () -> {
            OrderService orderService = Global.ctx.getBean(OrderService.class);

            prepareOrders(50, 3);
            runTestFindByComplicateMethodJpql(orderService::findByComplicateKey);
            prepareOrders(50, 1);
            runTestFindByComplicateMethodJpql(orderService::findByComplicateKey);
        });
    }

    @Test
    public void testFindByComplicateKeyWithJpql() {
        running(fakeApplication(), () -> {
            OrderService orderService = Global.ctx.getBean(OrderService.class);

            prepareOrders(50, 3);
            runTestFindByComplicateMethodJpql(orderService::findByComplicateKeyWithJpql);
            prepareOrders(50, 1);
            runTestFindByComplicateMethodJpql(orderService::findByComplicateKeyWithJpql);

        });
    }

    @Test
    public void testFindByComplicateKeyWithGeneralDaoQuery() {
        running(fakeApplication(), () -> {
            OrderService orderService = Global.ctx.getBean(OrderService.class);

            prepareOrders(50, 3);
            runTestFindByComplicateMethodJpql(orderService::findByComplicateKeyWithGeneralDaoQuery);
            prepareOrders(50, 1);
            runTestFindByComplicateMethodJpql(orderService::findByComplicateKeyWithGeneralDaoQuery);

        });
    }

    private void runTestFindByComplicateMethodJpql(BiFunction<Optional<Page<Order>>, OrderSearcher, List<Order>> method) {

        OrderSearcher orderSearcher = new OrderSearcher();
        orderSearcher.status = OrderStatus.WAIT_PROCESS;
        orderSearcher.type = OrderType.NORMAL;
        orderSearcher.orderItemStatus = OrderItemStatus.NOT_SIGNED;

        List<Order> orders = method.apply(of(new Page<>(1, Page.DEFAULT_PAGE_SIZE)), orderSearcher);
        assert orders.size() == Page.DEFAULT_PAGE_SIZE;

        Page<Order> page = new Page<>(4, Page.DEFAULT_PAGE_SIZE);
        orders = method.apply(of(page), orderSearcher);
        System.out.println(orders.size());
        assert orders.size() == 5;
        assert page.getResult().size() == orders.size();
        assert page.getTotalCount() == 50;

        orderSearcher.orderItemStatus = OrderItemStatus.SIGNED;
        page = new Page<>(1, Page.DEFAULT_PAGE_SIZE);
        orders = method.apply(of(page), orderSearcher);
        assert orders.size() == 0;
        assert page.getResult().size() == orders.size();
        assert page.getTotalCount() == 0;

    }

}
//import static org.hamcrest.MatcherAssert.*;


//import static org.hamcrest.Matchers.*;
