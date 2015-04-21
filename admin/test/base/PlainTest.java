package base;

import common.utils.DateUtils;
import common.utils.JsonUtils;
import common.utils.Money;
import ordercenter.constants.OrderStatus;
import ordercenter.constants.PlatformType;
import ordercenter.models.Order;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import java.lang.reflect.Field;

/**
 * Created by liubin on 15/4/11.
 */
public class PlainTest {

    @Test
    public void test() throws Exception {

        System.out.println(DateUtils.current());

        Order order = new Order();
        order.setBuyerId("你好啊");
        order.setOrderNo(RandomStringUtils.randomNumeric(8));
        order.setPlatformType(PlatformType.WEB);
        order.setStatus(OrderStatus.WAIT_PROCESS);
        order.setCreateTime(DateUtils.current());
        order.setUpdateTime(DateUtils.current());
        order.setBuyTime(DateUtils.current());
        order.setActualFee(Money.valueOf(3.23));

        String json = JsonUtils.object2Json(order);


//        String json = "{\"id\":null,\"orderNo\":\"92834494\",\"type\":{\"name\":\"NORMAL\",\"value\":\"正常订单\"},\"status\":{\"name\":\"WAIT_PROCESS\",\"value\":\"待处理\"},\"sharedDiscountFee\":\"0.00\",\"postFee\":\"0.00\",\"actualFee\":\"3.23\",\"buyerId\":null,\"buyerMessage\":null,\"remark\":null,\"buyTime\":null,\"payTime\":null,\"needReceipt\":false,\"receiptTitle\":null,\"receiptContent\":null,\"createTime\":\"2015-04-20 17:45:13\",\"updateTime\":\"2015-04-20 17:45:13\",\"operatorId\":null,\"invoiceId\":null,\"invoice\":null,\"platformType\":{\"name\":\"WEB\",\"value\":\"网站\"},\"orderItemList\":[]}";

        Order orderAfter = JsonUtils.json2Object(json, Order.class);

        System.out.println(orderAfter.getStatus());
        System.out.println(orderAfter.getCreateTime());
        System.out.println(orderAfter.getActualFee());

    }



}
