package ordercenter.payment.constants;

/**
 * 支付方式
 * User: lidujun
 * Date: 2015-04-29
 */
public enum PayMethod {

    directPay("支付宝"),
    creditPay("信用卡"),
    bankPay("银行卡"),
    Tenpay("财付通");

    private String value;

    PayMethod(String value) {
        this.value = value;
    }
    public String toDesc() {
        return value;
    }
}
