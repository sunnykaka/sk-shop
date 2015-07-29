package ordercenter.payment.constants;

import common.models.utils.ViewEnum;

/**
 * 支付类型
 * User: lidujun
 * Date: 2015-05-11
 */
public enum PayType implements ViewEnum {

    /**
     * 支付宝
     */
    Alipay("alipay"),
    /**
     * 财富通
     */
    TenPay("tenpay"),

    /**
     * 微信
     */
    WXSM("WXSM");

    public String value;

    PayType(String value) {
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
