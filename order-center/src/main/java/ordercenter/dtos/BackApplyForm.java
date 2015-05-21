package ordercenter.dtos;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 申请退单内容
 *
 * Created by zhb on 15-5-20.
 */
public class BackApplyForm {

    /** 订单ID */
    private int orderId;

    /** 订单项ID */
    private List<String> orderItem = new ArrayList<>();

    /** 退货原因 */
    private String backReason;

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public List<String> getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(List<String> orderItem) {
        this.orderItem = orderItem;
    }

    public String getBackReason() {
        return backReason;
    }

    public void setBackReason(String backReason) {
        this.backReason = backReason;
    }
}
