package ordercenter.constants;

import org.apache.commons.lang3.StringUtils;

/**
 * 订单操作
 * User: liubin
 * Date: 14-1-17
 */
public enum OrderFlow {


    /**
     * 作废
     */
    INVALID(OrderStatus.WAIT_APPROVE, OrderStatus.INVALID),

    /**
     * 审核
     */
    APPROVE(OrderStatus.WAIT_APPROVE, OrderStatus.WAIT_PROCESS),

    /**
     * 确认
     */
    CONFIRM(OrderStatus.WAIT_PROCESS, OrderStatus.CONFIRMED),

    /**
     * 打印
     */
    PRINT(OrderStatus.CONFIRMED, OrderStatus.PRINTED),

    /**
     * 验货
     */
    EXAMINE(OrderStatus.PRINTED, OrderStatus.EXAMINED),

    /**
     * 发货
     */
    INVOICE(OrderStatus.EXAMINED, OrderStatus.INVOICED),

    /**
     * 签收
     */
    SIGN(OrderStatus.INVOICED, OrderStatus.SIGNED);




    private OrderStatus from;
    private OrderStatus to;
    private String doMethodName;
    private String cancelMethodName;

    OrderFlow(OrderStatus from, OrderStatus to) {
        this.from = from;
        this.to = to;
        this.doMethodName = "do" + StringUtils.capitalize(this.toString().toLowerCase());
        this.cancelMethodName = "cancel" + StringUtils.capitalize(this.toString().toLowerCase());
    }

    /**
     * 根据首尾节点得到订单操作对象
     * @param from
     * @param to
     * @return
     */
    public static OrderFlow getOrderFlow(OrderStatus from, OrderStatus to) {
        if(from == null || to == null) return null;
        for(OrderFlow orderFlow : values()) {
            if((orderFlow.from == from && orderFlow.to == to) || (orderFlow.to == from && orderFlow.from == to)) {
                return orderFlow;
            }
        }
        return null;
    }

    /**
     * 得到do方法名字
     * @return
     */
    public String getDoMethodName() {
        return doMethodName;
    }

    /**
     * 得到cancel方法名字
     * @return
     */
    public String getCancelMethodName() {
        return cancelMethodName;
    }

}
