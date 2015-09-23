package ordercenter.constants;

import common.models.utils.ViewEnum;

/**
 * 客户端
 * User: lidujun
 * Date: 2015-09-23
 */
public enum Client implements ViewEnum {

    /**
     * 浏览器
     */
    Browser("Browser"),

    /**
     * IOS APP
     */
    IOSApp("IOSApp"),

    /**
     * Android App
     */
    AndroidApp("AndroidApp");

    public String value;

    Client(String value) {
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
