package api.response.shop;

import productcenter.models.SkuProperty;
import productcenter.models.StockKeepingUnit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lidujun on 15-8-27.
 */
public class SkuDto {
    /**
     * SKU主键  ID
     */
    private Integer id;

    /**
     * 商品ID，一个商品可以有多个sku
     */
    private Integer productId;

    /**
     * SKU 属性列表
     */
    private List<SkuPropertyDto> skuProperties = new ArrayList<>();

    public SkuDto build(StockKeepingUnit sku) {
        if(sku == null) {
            return  null;
        }
        SkuDto skuDto = new SkuDto();
        skuDto.setId(sku.getId());
        skuDto.setProductId(sku.getProductId());

        List<SkuProperty> skuPropertyList = sku.getSkuProperties();
        if(skuPropertyList != null) {
            List<SkuPropertyDto> skuPropertyDtoList = new ArrayList();
            for(SkuProperty skuProperty : skuPropertyList) {
                SkuPropertyDto dto = new SkuPropertyDto();
                skuPropertyDtoList.add(dto.build(skuProperty));
            }
            skuDto.setSkuProperties(skuPropertyDtoList);
        }
        return skuDto;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public List<SkuPropertyDto> getSkuProperties() {
        return skuProperties;
    }

    public void setSkuProperties(List<SkuPropertyDto> skuProperties) {
        this.skuProperties = skuProperties;
    }
}
