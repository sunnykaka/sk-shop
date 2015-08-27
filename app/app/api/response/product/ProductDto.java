package api.response.product;

import api.response.user.DesignerDto;
import productcenter.constants.ProductTagType;
import productcenter.models.Product;
import usercenter.models.DesignerSize;

/**
 * Created by liubin on 15-8-21.
 */
public class ProductDto {

    /**
     * 主键
     */
    private Integer id;

    /**
     * 产品编码
     */
    private String productCode;

    /**
     * 设计师
     */
    private DesignerDto designer;

    /**
     * 产品名称
     */
    private String name;

    /**
     * 英文名称
     */
    private String enName;

    /**
     * 新品或热销,默认(既不是新品也不是热销)
     */
    private ProductTagType tagType;

    /**
     * 推荐理由
     */
    private String description;

    /**
     * 该商品是否上架：是否上架(0表示未上架, 1表示上架)
     */
    private Boolean online;

    /**
     * 是否删除(0表示未删除, 1表示已删除)
     */
    private Boolean isDelete;



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public DesignerDto getDesigner() {
        return designer;
    }

    public void setDesigner(DesignerDto designer) {
        this.designer = designer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public ProductTagType getTagType() {
        return tagType;
    }

    public void setTagType(ProductTagType tagType) {
        this.tagType = tagType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }

    public Boolean getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Boolean isDelete) {
        this.isDelete = isDelete;
    }


    public static ProductDto build(Product product) {
        if(product == null) return null;
        ProductDto productDto = new ProductDto();
        productDto.setDescription(product.getDescription());
        productDto.setDesigner(DesignerDto.build(product.getCustomer()));
        productDto.setEnName(product.getEnName());
        productDto.setId(product.getId());
        productDto.setIsDelete(product.getIsDelete());
        productDto.setName(product.getName());
        productDto.setOnline(product.isOnline());
        productDto.setProductCode(product.getProductCode());
        productDto.setTagType(product.getTagType());

        return productDto;
    }
}
