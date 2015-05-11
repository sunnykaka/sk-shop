package ordercenter.payment;

import ordercenter.payment.constants.PayBank;
import ordercenter.payment.constants.PayMethod;
import ordercenter.util.TradeSequenceUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * 支付请求包装对象
 * User: lidujun
 * Date: 2015-04-29
 */
public class PayInfoWrapper {
    /**
     * 支付银行
     */
    private String defaultbank;

    /**
     * 支付回调类
     */
    private String callBackClass;

    /**
     * 交易总金额，以分为单位
     */
    private long totalFee;

    /**
     * 交易流水号
     */
    private String tradeNo = TradeSequenceUtil.getTradeNo();

    /**
     *支付方式
     */
    private PayMethod payMethod;

    /**
     * 购买的方式：是order还是coupon
     */
    private String bizType;

    public String getDefaultbank() {
        return defaultbank;
    }

    public void setDefaultbank(String defaultbank) {
        this.defaultbank = defaultbank;
    }

    public PayMethod getPayMethod() {
        return payMethod;
    }

    /**
     * 设置支付方式
     * @param payMethod
     */
    public void setPayMethod(PayMethod payMethod) {
        if (payMethod == null) {
            payMethod = PayMethod.directPay;
        }
        this.payMethod = payMethod;
    }

    /**
     * 是否是阿里支付宝支付
     * @return
     */
    public boolean isAlipay() {
        return payMethod != null && payMethod == PayMethod.directPay;
    }

    /**
     * 是否是腾讯财付通支付
     * @return
     */
    public boolean isTenpay() {
        return payMethod != null && payMethod == PayMethod.Tenpay;
    }

    /**
     * 是否是银行支付
     * @return
     */
    public boolean isBank() {
        return payMethod != null && payMethod == PayMethod.bankPay && StringUtils.isNotEmpty(defaultbank)
                && Arrays.asList(PayBank.values()).contains(Enum.valueOf(PayBank.class, defaultbank));
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public long getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(long totalFee) {
        this.totalFee = totalFee;
    }

    public String getCallBackClass() {
        return callBackClass;
    }

    public void setCallBackClass(Class<? extends PayCallback> callBackClass) {
        this.callBackClass = callBackClass.getName();
    }

    public String getBizType() {
        return bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType;
    }
}