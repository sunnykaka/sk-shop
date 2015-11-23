package usercenter.constants;

import common.models.utils.ViewEnum;

/**
 * 订单类型
 * User: liubin
 * Date: 13-12-30
 */
public enum Gender implements ViewEnum {

    UNKNOWN("未知", 0),

    MALE("男", 1),

    FEMALE("女", 2);

    public String value;

    public int userSex;

    Gender(String value, int userSex) {
        this.value = value;
        this.userSex = userSex;
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
