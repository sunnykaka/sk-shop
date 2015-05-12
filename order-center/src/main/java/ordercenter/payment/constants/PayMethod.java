package ordercenter.payment.constants;

import common.models.utils.ViewEnum;

/**
 * 支付方式
 * User: lidujun
 * Date: 2015-04-29
 */
public enum PayMethod implements ViewEnum {

    directPay("支付宝"),
    creditPay("信用卡"),
    bankPay("银行卡"),
    Tenpay("财付通");

    public String value;

    PayMethod(String value) {
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
