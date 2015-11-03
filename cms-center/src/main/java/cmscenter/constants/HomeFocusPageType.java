package cmscenter.constants;

import common.models.utils.ViewEnum;

/**
 * @auth zhb
 * 15-10-28.
 */
public enum HomeFocusPageType implements ViewEnum {

    /**
     * 焦点图位置
     */
    focus("焦点图位置"),

    /**
     * 精选图位置
     */
    selection("精选图位置");

    public String value;

    HomeFocusPageType(String value) {
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
