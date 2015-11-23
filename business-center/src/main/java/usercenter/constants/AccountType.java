package usercenter.constants;

import common.models.utils.ViewEnum;

/**
 * 订单类型
 * User: liubin
 * Date: 13-12-30
 */
public enum AccountType implements ViewEnum {

    Anonymous("未知"),

    KRQ("商城"),

    QQ("QQ"),

    RenRen("人人"),

    Sina("新浪"),

    TaoBao("淘宝"),

    Alipay("支付宝"),

    NetEase("网易"),

    WeiXin("微信"),

    JD("京东");


    public String value;

    AccountType(String value) {
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
