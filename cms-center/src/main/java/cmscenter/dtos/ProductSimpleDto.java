package cmscenter.dtos;

import common.utils.StringUtils;
import productcenter.models.Product;

/**
 * Created by Administrator on 2015/9/15.
 */
public class ProductSimpleDto {

    private Integer productId;

    private String productUrl;

    private String productName;

    public static ProductSimpleDto build(Product product,String url){

        ProductSimpleDto productSimpleDto = new ProductSimpleDto();
        productSimpleDto.setProductId(product.getId());
        productSimpleDto.setProductName(StringUtils.subTextString(product.getName(), 46));
        productSimpleDto.setProductUrl(url);

        return productSimpleDto;

    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
