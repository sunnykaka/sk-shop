package common.constants;

import common.models.utils.ViewEnum;

/**
 * 消息类型
 */
public enum MessageJobType implements ViewEnum {

    EMAIL,
    SMS;

    @Override
    public String getName() {
        return this.toString();
    }

    @Override
    public String getValue() {
        return this.toString();
    }

}
