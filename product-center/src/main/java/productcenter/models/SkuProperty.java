package productcenter.models;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * sku 属性
 * 比如颜色红色，尺寸XL
 *
 * User: lidujun
 * Date: 2015-04-27
 */
public class SkuProperty {

    /**
     * SKU主键  ID
     */
    private long skuId;

    /**
     * 属性  id
     */
    private int propertyId;

    /**
     * 属性值id
     */
    private int valueId;

    public long getSkuId() {
        return skuId;
    }

    public void setSkuId(long skuId) {
        this.skuId = skuId;
    }

    public int getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(int propertyId) {
        this.propertyId = propertyId;
    }

    public int getValueId() {
        return valueId;
    }

    public void setValueId(int valueId) {
        this.valueId = valueId;
    }
}
