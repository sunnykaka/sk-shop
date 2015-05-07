package productcenter.models;

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
    private int skuId;

    /**
     * 属性  id
     */
    private int propertyId;

    /**
     * 属性值id
     */
    private int valueId;

    /**
     * 属性名称
     */
    private String propertyName;

    /**
     *  属性值
     */
    private String propertyValue;

    @Override
    public String toString() {
        return "SkuProperty{" +
                "skuId=" + skuId +
                ", propertyId=" + propertyId +
                ", valueId=" + valueId +
                ", propertyName='" + propertyName + '\'' +
                ", propertyValue='" + propertyValue + '\'' +
                '}';
    }

    public long getSkuId() {
        return skuId;
    }

    public void setSkuId(int skuId) {
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

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }
}
