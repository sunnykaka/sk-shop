package api.response.shop;

import productcenter.models.SkuProperty;

/**
 * Created by lidujun on 15-8-27.
 */
public class SkuPropertyDto {
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


    public SkuPropertyDto build(SkuProperty skuProperty) {
        if(skuProperty == null) {
           return  null;
        }
        SkuPropertyDto skuPropertyDto = new SkuPropertyDto();
        skuPropertyDto.setSkuId(skuProperty.getSkuId());
        skuPropertyDto.setPropertyId(skuProperty.getPropertyId());
        skuPropertyDto.setValueId(skuProperty.getValueId());
        skuPropertyDto.setPidvid(skuProperty.getPidvid());
        skuPropertyDto.setPropertyName(skuProperty.getPropertyName());
        skuPropertyDto.setPropertyValue(skuProperty.getPropertyValue());
        return skuPropertyDto;
    }

    public int getSkuId() {
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

    public long getPidvid() {
        return pidvid;
    }

    public void setPidvid(long pidvid) {
        this.pidvid = pidvid;
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
