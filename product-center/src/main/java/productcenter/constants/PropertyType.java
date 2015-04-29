package productcenter.constants;

import common.models.utils.ViewEnum;

/**
 * 类目属性类型，它表示某个属性在特定类目下是什么类型，有
 * 关键属性，销售属性等
 * <p/>
 * 销售属性一般是多值，用来在商品详情页筛选SKU
 * 关键属性就是表示商品的参数，也有可能多值，比如锅的适用炉灶：电磁炉，煤气灶
 *
 * User: lidujun
 * Date: 2015-04-27
 */
public enum PropertyType implements ViewEnum {

    /**
     *  销售属性
     */
    SELL_PROPERTY("销售属性"),
    /**
     * 关键属性
     */
    KEY_PROPERTY("关键属性");

    public String value;

    PropertyType(String value) {
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
