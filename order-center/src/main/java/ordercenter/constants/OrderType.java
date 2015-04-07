package ordercenter.constants;

/**
 * 订单类型
 * User: liubin
 * Date: 13-12-30
 */
public enum OrderType {

    NORMAL("正常订单");

    public String value;

    OrderType(String value) {
        this.value = value;
    }


}
