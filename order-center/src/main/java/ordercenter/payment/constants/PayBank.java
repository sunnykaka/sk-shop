package ordercenter.payment.constants;

import common.models.utils.ViewEnum;

/**
 * 支付银行
 * User: lidujun
 * Date: 2015-04-29
 */
public enum PayBank implements ViewEnum {

    /**
     * 如果是货到付款，则没有支付银行
     */
    NOBank("不在线支付","NOBank"),
    /**
     * 工商银行
     */
    ICBCB2C("中国工商银行","debitcard-icbc-mb2c"),
    /**
     * 交通银行
     */
    COMM("交通银行","debitcard-comm-mb2c"),
    /**
     * 建设银行
     */
    CCB("中国建设银行","debitcard-ccb-mb2c"),
    /**
     * 农业银行
     */
    ABC("中国农业银行","ABC"),
    /**
     * 邮政储蓄
     */
    POSTGC("中国邮政储蓄银行","POSTGC"),
    /**
     * 招商银行
     */
    CMB("招商银行","debitcard-cmb-mb2c"),
    /**
     * 中国银行
     */
    BOCB2C("中国银行","debitcard-boc-mb2c"),

    /**
     * 光大银行
     */
    CEBBANK("中国光大银行","debitcard-ceb-mb2c"),
    /**
     * 中信银行
     */
    CITIC("中信银行","debitcard-citic-mb2c"),
    /**
     * 深发展银行
     */
    SDB("深圳发展银行","debitcard-sdb-mb2c"),
    /**
     * 浦发银行
     */
    SPDB("浦发银行","debitcard-spdb-mb2c"),
    /**
     * 民生银行
     */
    CMBC("民生银行","CMBC"),
    /**
     * 兴业银行
     */
    CIB("兴业银行","debitcard-cib-mb2c"),
    /**
     * 平安银行
     */
    SPABANK("平安银行","SPABANK"),
    /**
     * 广发银行
     */
    GDB("广发银行","debitcard-gdb-mb2c"),
    /**
     * 上海银行
     */
    SHBANK("上海银行","debitcard-shbank-mb2c"),
    /**
     * 宁波银行
     */
    NBBANK("宁波银行","debitcard-nbbank-mb2c"),
    /**
     * 杭州银行
     */
    HZCBB2C("杭州银行","debitcard-hzcb-mb2c"),
    /**
     * 北京银行
     */
    BJBANK("北京银行","BJBANK"),
    /**
     * 北京农商银行
     */
    BJRCB("北京农商银行","BJRCB"),

    /**
     * 支付宝
     */
    Alipay("支付宝","Alipay"),

    /**
     * 财付通
     */
    Tenpay("财付通","Tenpay"),

    /**
     * 微信支付
     */
    WXSM("微信支付","WXSM");

    public String value;

    public String forexBankName;

    PayBank(String value, String forexBankName) {
        this.value = value;
        this.forexBankName =forexBankName;
    }

    @Override
    public String getName() {
        return this.toString();
    }

    @Override
    public String getValue() {
        return value;
    }

    public String getForexBankName() {
        return forexBankName;
    }
}
