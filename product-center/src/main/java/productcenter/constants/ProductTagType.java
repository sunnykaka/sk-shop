package productcenter.constants;

import common.models.utils.ViewEnum;

/**
 * 订单类型
 * User: lidujun
 * Date: 2015-04-24
 */
public enum ProductTagType implements ViewEnum {

    /**
     * 默认 既不是新品也不是热销
     */
    DEFAULT("无"),
    /**
     * 新品
     */
    NEW("新品"),
    /**
     * 热销
     */
    HOT("热销");

    public String value;

    ProductTagType(String value) {
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
