package ordercenter.constants;


/**
 * 订单明细状态
 * User: liubin
 * Date: 13-12-30
 */
public enum OrderItemStatus {

    NOT_SIGNED("未签收"),

    SIGNED("已签收");


    public String value;

    OrderItemStatus(String value) {
        this.value = value;
    }


}
