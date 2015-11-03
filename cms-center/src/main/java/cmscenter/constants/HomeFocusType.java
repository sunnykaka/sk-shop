package cmscenter.constants;

import common.models.utils.ViewEnum;

/**
 * @auth zhb
 * 15-10-28.
 */
public enum HomeFocusType implements ViewEnum {

    /**
     * 设计师
     */
    designer("设计师"),

    /**
     * 手机专题
     */
    appTheme("手机专题"),

    /**
     * 产品
     */
    product("产品"),

    /**
     * 网页
     */
    webView("网页");

    public String value;

    HomeFocusType(String value) {
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
