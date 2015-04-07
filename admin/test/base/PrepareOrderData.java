package base;

import common.utils.DateUtils;
import common.utils.Money;
import ordercenter.constants.OrderItemStatus;
import ordercenter.constants.OrderItemType;
import ordercenter.constants.OrderStatus;
import ordercenter.constants.PlatformType;
import ordercenter.models.Order;
import ordercenter.models.OrderItem;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * Created by liubin on 15/4/6.
 */
public interface PrepareOrderData extends BaseTest {

    default void prepareOrders(int orderSize, int itemPerOrderSize) {
        doInTransaction(em -> {

            em.createQuery("delete from OrderItem").executeUpdate();
            em.createQuery("delete from Order").executeUpdate();

            //创建订单
            for (int i = 0; i < orderSize; i++) {
                Order order = new Order();
                order.setOrderNo(RandomStringUtils.randomNumeric(8));
                order.setPlatformType(PlatformType.WEB);
                order.setStatus(OrderStatus.WAIT_PROCESS);
                order.setCreateTime(DateUtils.current());
                order.setUpdateTime(DateUtils.current());

                em.persist(order);

                for (int j = 0; j < itemPerOrderSize; j++) {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrderId(order.getId());
                    orderItem.setBuyCount(5);
                    orderItem.setPlatformType(order.getPlatformType());
                    orderItem.setDiscountFee(Money.valueOf(10));
                    orderItem.setPrice(Money.valueOf(20));
                    orderItem.setStatus(OrderItemStatus.NOT_SIGNED);
                    orderItem.setType(OrderItemType.PRODUCT);
                    orderItem.setProductId(Integer.parseInt(RandomStringUtils.randomNumeric(8)));
                    orderItem.setProductSku(RandomStringUtils.randomAlphabetic(8));

                    em.persist(orderItem);
                }
            }

            return null;
        });
    }
}
