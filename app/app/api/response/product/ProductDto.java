package api.response.product;

import productcenter.constants.ProductTagType;

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
     * 所在的后台类目
     */
    private Integer categoryId;

    /**
     * 客户（设计师）id
     */
    private Integer customerId;

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

    /**
     * 设计师尺码表ID
     */
    private Integer designerSizeId;

    /**
     * 设计师尺码表
     */
    private DesignerSizeDto designerSize;


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

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
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

    public Integer getDesignerSizeId() {
        return designerSizeId;
    }

    public void setDesignerSizeId(Integer designerSizeId) {
        this.designerSizeId = designerSizeId;
    }

    public DesignerSizeDto getDesignerSize() {
        return designerSize;
    }

    public void setDesignerSize(DesignerSizeDto designerSize) {
        this.designerSize = designerSize;
    }
}
