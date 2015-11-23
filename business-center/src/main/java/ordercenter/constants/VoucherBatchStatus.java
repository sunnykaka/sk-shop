package ordercenter.constants;

import common.models.utils.ViewEnum;

/**
 * 代金券批次状态
 * User: liubin
 * Date: 15-11-5
 */
public enum VoucherBatchStatus implements ViewEnum {

    VALID("有效"),

    INVALID("无效"),

    DELETED("已删除");

    public String value;

    VoucherBatchStatus(String value) {
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
