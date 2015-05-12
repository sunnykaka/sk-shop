package ordercenter.payment.constants;

import common.models.utils.ViewEnum;

/**
 * 调用回调处理类的方式
 * User: lidujun
 * Date: 2015-04-28
 */
public enum ResponseType implements ViewEnum {
    /**
     * 正常返回
     */
    RETURN("正常返回"), //return
    /**
     * Notify回来
     */
    NOTIFY("Notify回来"); //notify

    public String value;

    ResponseType(String value) {
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
