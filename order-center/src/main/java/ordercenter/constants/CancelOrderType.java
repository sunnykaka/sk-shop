package ordercenter.constants;

/**
 * 取消订单类型
 *
 * User: Zhb
 * Date: 2015-05-11
 */
public enum CancelOrderType {

    Change("改选其他商品",0),

    ErrorMessage("收货信息/商品错误",1),

    Coupon("优惠券使用问题",2),

    Pay("支付遇到问题",3),

    Dont("不想买了",4),

    Sys("订单一小时未支付，系统定时取消订单",6),

    Other("其他原因",5);

    // 成员变量
    private String name;

    private int index;

    // 构造方法
    private CancelOrderType (String name, int index) {
        this.name = name;
        this.index = index;
    }
    // 普通方法
    public static CancelOrderType getByIndex(int index) {
        for (CancelOrderType c : CancelOrderType.values()) {
            if (c.getIndex() == index) {
                return c;
            }
        }
        return CancelOrderType.Other;
    }
    // get set 方法
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }
}
