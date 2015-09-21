package cmscenter.dtos;

/**
 * Created by Administrator on 2015/9/15.
 */
public class ProductSimpleDto {

    private Integer productId;

    private String productUrl;

    private String productName;

    public ProductSimpleDto(int productId,String productName,String productUrl){
        this.productId = productId;
        this.productName = productName;
        this.productUrl = productUrl;
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
