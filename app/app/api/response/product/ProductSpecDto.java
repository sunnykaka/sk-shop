package api.response.product;

import productcenter.models.ProductSpec;

/**
 * Created by zhb on 2015/9/30.
 */
public class ProductSpecDto {

    private String name;
    private String value;

    public static ProductSpecDto build(ProductSpec productSpec){
        ProductSpecDto productSpecDto = new ProductSpecDto();
        productSpecDto.setName(productSpec.getName());
        productSpecDto.setValue(productSpec.getValue());
        return productSpecDto;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
