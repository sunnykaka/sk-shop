package ordercenter.constants;

import common.models.utils.ViewEnum;

/**
 * 代金券类型
 * User: liubin
 * Date: 15-11-5
 */
public enum VoucherType implements ViewEnum {

    FIRE_BY_REGISTER("用户注册自动赠送"),

    RECEIVE_BY_ACTIVITY("活动页面领取");

    public String value;

    VoucherType(String value) {
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
