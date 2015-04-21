package ordercenter.constants;

import common.models.utils.ViewEnum;

/**
 * 订单类型
 * User: liubin
 * Date: 13-12-30
 */
public enum OrderType implements ViewEnum {

    NORMAL("正常订单");

    public String value;

    OrderType(String value) {
        this.value = value;
    }

    @Override
    public String getName() {
        return this.toString();
    }

    @Override
    public String getValue() {
        return value;
    }


}
