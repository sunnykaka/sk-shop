package constants;

import common.models.utils.ViewEnum;

/**
 * 消息来源
 */
public enum MessageJobSource implements ViewEnum {

    REGISTER_VERIFICATION_CODE("注册验证码"),
    CHANGE_VERIFICATION_CODE("修改绑定验证码"),
    INVOICE_NOTIFICATION("发货通知"),
    PAY_REMINDER("支付提醒"),
    CHANGE_EMAIL("修改邮箱");

    public String value;

    MessageJobSource(String value) {
        this.value = value;
    }


    @Override
    public String getName() {
        return this.toString();
    }

    @Override
    public String getValue() {
        return this.toString();
    }

}
