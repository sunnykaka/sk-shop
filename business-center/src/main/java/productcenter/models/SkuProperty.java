package productcenter.models;

import productcenter.util.PropertyValueUtil;

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


    private long pidvid;

    /**
     * 属性名称
     */
    private String propertyName;

    /**
     *  属性值
     */
    private String propertyValue;


    public SkuProperty(int skuId, long pidvid) {
        this.skuId = skuId;
        this.pidvid = pidvid;

        PropertyValueUtil.PV pv = PropertyValueUtil.parseLongToPidVid(pidvid);
        this.propertyId = pv.pid;
        this.valueId = pv.vid;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    public int getSkuId() {
        return skuId;
    }

    public int getPropertyId() {
        return propertyId;
    }

    public int getValueId() {
        return valueId;
    }

    public long getPidvid() {
        return pidvid;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SkuProperty)) return false;

        SkuProperty that = (SkuProperty) o;

        if (pidvid != that.pidvid) return false;
        if (skuId != that.skuId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = skuId;
        result = 31 * result + (int) (pidvid ^ (pidvid >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "SkuProperty{" +
                "skuId=" + skuId +
                ", propertyId=" + propertyId +
                ", valueId=" + valueId +
                ", pidvid=" + pidvid +
                ", propertyName='" + propertyName + '\'' +
                ", propertyValue='" + propertyValue + '\'' +
                '}';
    }
}
