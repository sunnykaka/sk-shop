package productcenter.constants;

import common.models.utils.ViewEnum;

/**
 * SEO推广类型
 * User: Alec
 * Date: 13-10-9
 * Time: 下午2:15
 */
public enum SeoType implements ViewEnum {

    PAGE("页面"),

    CATEGORY("类目"),

    CHANNEL("频道"),

    PRODUCT("商品");

    public String value;

    SeoType(String value) {
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
