package common.services;

import base.PrepareOrderData;
import ordercenter.constants.OrderStatus;
import ordercenter.constants.PlatformType;
import ordercenter.models.Order;
import ordercenter.models.OrderItem;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import play.test.WithApplication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liubin on 15-4-2.
 */
public class GeneralDaoTest extends WithApplication implements PrepareOrderData {


    /**
     * 测试merge和update
     */
    @Test
    public void testGeneralDaoMergeAndUpdate() {

        prepareOrders(0, 0);

        Order order1 = doInTransactionWithGeneralDao(generalDao -> {

            //创建订单
            Order order = new Order();
            order.setOrderNo(RandomStringUtils.randomAlphanumeric(8));
            order.setPlatformType(PlatformType.WEB);
            order.setStatus(OrderStatus.WAIT_PROCESS);

            generalDao.persist(order);
            generalDao.flush();
            generalDao.detach(order);

            assert order.getCreateTime() != null;
            assert order.getUpdateTime() == null;
            assert order.getId() > 0;

            //此时更新无用,因为对象已没有被session管理(显式detach)
            order.setStatus(OrderStatus.INVALID);

            return order;
        });

        doInTransactionWithGeneralDao(generalDao -> {

            //校验之前的更新确实没起作用
            Order order = generalDao.get(Order.class, order1.getId());
            assert order.getStatus() == OrderStatus.WAIT_PROCESS;
            return null;
        });

        doInTransactionWithGeneralDao(generalDao -> {

            //merge,会根据order1的id从数据库load出order2对象,再把order1的属性拷给order2
            order1.setStatus(OrderStatus.INVALID);
            Order order2 = generalDao.merge(order1);
            assert order2.getStatus() == order1.getStatus();

            //对order1的更新无用,对order2的更新有用.因为order1没有被session管理
            order1.setStatus(OrderStatus.PRINTED);
            order2.setStatus(OrderStatus.INVOICED);

            return null;
        });

        doInTransactionWithGeneralDao(generalDao -> {
            //校验确实是order2的更新起作用
            Order order = generalDao.get(Order.class, order1.getId());
            assert order.getStatus() == OrderStatus.INVOICED;
            return null;
        });

        //测试update方法
        doInTransactionWithGeneralDao(generalDao -> {

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("status", OrderStatus.SIGNED);
            params.put("id", order1.getId());


            int update = generalDao.update(" update Order o set o.status = :status where o.id = :id ", params);
            assert update == 1;

            return null;
        });

        doInTransactionWithGeneralDao(generalDao -> {

            Order order = generalDao.get(Order.class, order1.getId());
            assert order.getStatus() == OrderStatus.SIGNED;
            return null;
        });

    }

    /**
     * 测试findAll
     */
    @Test
    public void testGeneralDaoFindAll() {

        prepareOrders(50, 3);

        doInTransactionWithGeneralDao(generalDao -> {

            List<Order> orders = generalDao.findAll(Order.class);
            List<OrderItem> orderItems = generalDao.findAll(OrderItem.class);

            assert orders.size() == 50;
            assert orderItems.size() == 50 * 3;

            return null;

        });

    }

}
