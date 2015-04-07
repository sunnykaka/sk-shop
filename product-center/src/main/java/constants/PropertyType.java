package constants;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.EnumSerializer;

/**
 * 类目属性类型，它表示某个属性在特定类目下是什么类型，有
 * 关键属性，销售属性等
 * <p/>
 * 销售属性一般是多值，用来在商品详情页筛选SKU
 * 关键属性就是表示商品的参数，也有可能多值，比如锅的适用炉灶：电磁炉，煤气灶
 *
 * Created by zhb on 15-4-1.
 * update by lidujun 2015-04-07
 */
@JsonSerialize(using = EnumSerializer.class)
public enum PropertyType {

    /**
     * 销售属性
     */
    SELL_PROPERTY("销售属性"),

    /**
     * 关键属性
     */
    KEY_PROPERTY("关键属性"),

    /**
     * 非关键属性
     */
    NOT_KEY_PROPERTY("非关键属性");

    public String value;

    private PropertyType(String value) {
        this.value = value;
    }

    public String getName() {
        return this.toString();
    }

    public String getValue() {
        return value;
    }
}
