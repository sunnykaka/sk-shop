package ordercenter.constants;

import common.models.utils.ViewEnum;

/**
 * 代金券状态
 * User: liubin
 * Date: 15-11-5
 */
public enum VoucherStatus implements ViewEnum {

    UNUSE("未使用"),

    USED("已使用"),

    OVERDUE("已过期");

    public String value;

    VoucherStatus(String value) {
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
