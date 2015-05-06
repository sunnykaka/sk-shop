package ordercenter.payment.constants;

/**
 * 支付类型
 * User: lidujun
 * Date: 2015-04-28
 */
public enum PayType {

    /**
     * 支付宝
     */
    Alipay("支付宝"), //alipay
    /**
     * 财富通
     */
    TenPay("财富通"); //tenpay

    private String value;

    PayType(String value) {
        this.value = value;
    }

    public String getValue(){
        return this.value;
    }
}
