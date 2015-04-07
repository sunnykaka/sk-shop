package ordercenter.constants;


/**
 * 订单明细类型
 * User: liubin
 * Date: 13-12-30
 */
public enum OrderItemType  {

    PRODUCT("商品"),

    MEALSET("套餐商品"),

    GIFT("赠品");

    public String value;

    OrderItemType(String value) {
        this.value = value;
    }


}
