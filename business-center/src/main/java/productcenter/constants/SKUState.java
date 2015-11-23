package productcenter.constants;

import common.models.utils.ViewEnum;

/**
 * 订单类型
 * User: lidujun
 * Date: 2015-04-24
 */
public enum SKUState implements ViewEnum {

    /**
     * 状态：正常
     */
    NORMAL("正常"),
    /**
     * 状态：REMOVED表示这个sku失效
     */
    REMOVED("失效");

    public String value;

    SKUState(String value) {
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
