package ordercenter.dtos;

import ordercenter.constants.OrderItemStatus;
import ordercenter.constants.OrderStatus;
import ordercenter.constants.OrderType;
import org.joda.time.DateTime;

/**
 * Created by liubin on 15-4-3.
 */
public class TestObjectSearcher {

    public Integer orderNo;

    public OrderStatus status;

    public DateTime createTimeStart;

    public DateTime createTimeEnd;

    public OrderItemStatus orderItemStatus;

    public String productSku;

}
