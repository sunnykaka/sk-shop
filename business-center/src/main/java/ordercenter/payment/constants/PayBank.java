package ordercenter.payment.constants;

import common.models.utils.ViewEnum;

/**
 * 支付银行
 * User: lidujun
 * Date: 2015-04-29
 */
public enum PayBank implements ViewEnum {

//    /**
//     * 如果是货到付款，则没有支付银行,暂时先屏蔽掉
//     */
//    NOBank("不在线支付", PayMethod.bankPay),
    /**
     * 工商银行
     */
    ICBCB2C("中国工商银行", PayMethod.bankPay),
    /**
     * 交通银行
     */
    COMM("交通银行", PayMethod.bankPay),
    /**
     * 建设银行
     */
    CCB("中国建设银行", PayMethod.bankPay),
    /**
     * 农业银行
     */
    ABC("中国农业银行", PayMethod.bankPay),
    /**
     * 邮政储蓄
     */
    POSTGC("中国邮政储蓄银行", PayMethod.bankPay),
    /**
     * 招商银行
     */
    CMB("招商银行", PayMethod.bankPay),
    /**
     * 中国银行
     */
    BOCB2C("中国银行", PayMethod.bankPay),

    /**
     * 光大银行
     */
    CEBBANK("中国光大银行", PayMethod.bankPay),
    /**
     * 中信银行
     */
    CITIC("中信银行", PayMethod.bankPay),
    /**
     * 深发展银行
     */
    SDB("深圳发展银行", PayMethod.bankPay),
    /**
     * 浦发银行
     */
    SPDB("浦发银行", PayMethod.bankPay),
    /**
     * 民生银行
     */
    CMBC("民生银行", PayMethod.bankPay),
    /**
     * 兴业银行
     */
    CIB("兴业银行", PayMethod.bankPay),
    /**
     * 平安银行
     */
    SPABANK("平安银行", PayMethod.bankPay),
    /**
     * 广发银行
     */
    GDB("广发银行", PayMethod.bankPay),
    /**
     * 上海银行
     */
    SHBANK("上海银行", PayMethod.bankPay),
    /**
     * 宁波银行
     */
    NBBANK("宁波银行", PayMethod.bankPay),
    /**
     * 杭州银行
     */
    HZCBB2C("杭州银行", PayMethod.bankPay),
    /**
     * 北京银行
     */
    BJBANK("北京银行", PayMethod.bankPay),
    /**
     * 北京农商银行
     */
    BJRCB("北京农商银行", PayMethod.bankPay),

    /**
     * 支付宝
     */
    Alipay("支付宝", PayMethod.directPay),

    /**
     * 财付通
     */
    Tenpay("财付通", PayMethod.Tenpay),

    /**
     * 微信支付
     */
    WXSM("微信支付", PayMethod.WXSM);

    private String value;

    /**
     * 支付方式
     */
    private PayMethod payType;

    PayBank(String value, PayMethod payType) {
        this.value = value;
        this.payType = payType;
    }

    @Override
    public String getName() {
        return this.toString();
    }

    @Override
    public String getValue() {
        return value;
    }

    /**
     * 获取支付方式，银行卡，支付宝余额....
     *
     * @return
     */
    public PayMethod getPayMethod() {
        return this.payType;
    }

    /**
     * 获取第三方支付渠道（平台）
     *
     * @return
     */
    public String getPayChannel() {
        return this.payType.getChannel();
    }
}
