package ordercenter.constants;


import common.models.utils.ViewEnum;

/**
 * 订单明细类型
 * User: liubin
 * Date: 13-12-30
 */
public enum OrderItemType implements ViewEnum {

    PRODUCT("商品"),

    MEALSET("套餐商品"),

    GIFT("赠品");

    public String value;

    OrderItemType(String value) {
        this.value = value;
    }

    @Override
    public String getName() {
        return toString();
    }

    @Override
    public String getValue() {
        return value;
    }


}
