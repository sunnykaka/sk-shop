package ordercenter.constants;

/**
 * 订单退货状态
 * User: liubin
 * Date: 13-12-30
 */
public enum OrderReturnStatus {

    NORMAL("正常"),

    PART_RETURN("部分退货"),

    RETURNED("已退货");

    public String value;

    OrderReturnStatus(String value) {
        this.value = value;
    }


}
