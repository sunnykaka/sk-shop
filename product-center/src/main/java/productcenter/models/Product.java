package productcenter.models;

import common.models.utils.EntityClass;
import common.models.utils.OperableData;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import productcenter.constants.ProductTagType;
import productcenter.constants.StoreStrategy;
import usercenter.models.Designer;

import javax.persistence.*;
import java.util.List;

/**
 * 商品对象，商品是一个抽象的对象，真正对应物理商品的是SKU
 * User: lidujun
 * Date: 2015-04-24
 */
@Table(name = "Product")
@Entity
public class Product implements EntityClass<Integer>, OperableData {
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
     * 客户（设计师）
     */
    private Designer customer;


    /**
     * 版本id
     */
    private Integer brandId;

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
     * 库存策略(0表示普通模式, 1表示付款成功后扣减模式), 默认为 0
     * NormalStrategy.普通策略, 创建即扣减库存, 取消则回加库存; PayStrategy.付款策略, 付款成功后才会扣减
     */
    private StoreStrategy storeStrategy;

    /**
     * 推荐理由
     */
    private String description;

    /**
     * 该商品是否上架：是否上架(0表示未上架, 1表示上架)
     */
    private Boolean online;

    /**
     * 上架时间
     */
    private DateTime onlineTime;

    /**
     * 上架时间的long型表示，搜索引擎读取
     */
    private Long onLineTimeLong;

    /**
     * 下架时间
     */
    private DateTime offlineTime;

    /**
     * 是否删除(0表示未删除, 1表示已删除)
     */
    private Boolean isDelete;

    /**
     * 搭配描述
     */
    private String recommendDesc;

    /**
     * 创建时间
     */
    private DateTime createTime;

    /**
     * 更新时间可用于搜索引擎重dump数据
     */
    private DateTime updateTime;

    /**
     * 最后操作人

    private Integer operatorId;
     */

    /**
     * 视频链接
     */
    private String video;

    /**
     * 模特信息
     */
    private String modelInfo;

    /**
     * 设计师尺码表ID
     */
    private Integer designerSizeId;

    private String saleStatus;

    /**
     * 一个商品对应一个或者多个SKU对象,一个SKU对应一个物理单品
     */
    private List<StockKeepingUnit> stockKeepingUnits;

    /**
     * 搭配商品
     */
    private List<Product> matchProductList;

    private String mainPic;

    @Transient
    public List<StockKeepingUnit> getStockKeepingUnits() {
        return stockKeepingUnits;
    }

    public void setStockKeepingUnits(List<StockKeepingUnit> stockKeepingUnits) {
        this.stockKeepingUnits = stockKeepingUnits;
    }

    @Transient
    public List<productcenter.models.Product> getMatchProductList() {
        return matchProductList;
    }

    public void setMatchProductList(List<productcenter.models.Product> matchProductList) {
        this.matchProductList = matchProductList;
    }

    @Transient
    public String getMainPic() {
        return mainPic;
    }

    public void setMainPic(String mainPic) {
        this.mainPic = mainPic;
    }

    /**
     * 筛选某个SKU
     *
     * @param skuId
     * @return
     */
    public StockKeepingUnit getStockKeepingUnit(int skuId) {
        for (StockKeepingUnit stockKeepingUnit : stockKeepingUnits) {
            if (stockKeepingUnit.getId() == skuId) {
                return stockKeepingUnit;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", productCode='" + productCode + '\'' +
                ", categoryId=" + categoryId +
                ", customerId=" + customerId +
                ", brandId=" + brandId +
                ", name='" + name + '\'' +
                ", enName='" + enName + '\'' +
                ", tagType=" + tagType +
                ", storeStrategy=" + storeStrategy +
                ", description='" + description + '\'' +
                ", online=" + online +
                ", onlineTime=" + onlineTime +
                ", onLineTimeLong=" + onLineTimeLong +
                ", offlineTime=" + offlineTime +
                ", isDelete=" + isDelete +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", stockKeepingUnits=" + stockKeepingUnits +
                ", recommendDesc=" + recommendDesc +
                '}';
    }

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    @Basic
    @Column(name="saleStatus")
    public String getSaleStatus() {
        return saleStatus;
    }

    public void setSaleStatus(String saleStatus) {
        this.saleStatus = saleStatus;
    }

    @Column(name = "productCode")
    @Basic
    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    @Column(name = "categoryId")
    @Basic
    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    @Column(name = "customerId")
    @Basic
    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    @Column(name = "brandId")
    @Basic
    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    @Column(name = "name")
    @Basic
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "enName")
    @Basic
    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    @Column(name = "description")
    @Basic
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "storeStrategy")
    @Enumerated(EnumType.STRING)
    public StoreStrategy getStoreStrategy() {
        return storeStrategy;
    }

    public void setStoreStrategy(StoreStrategy storeStrategy) {
        this.storeStrategy = storeStrategy;
    }

    @Column(name = "online")
    @Basic
    public Boolean isOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }

    @Column(name = "tagType")
    @Enumerated(EnumType.STRING)
    public ProductTagType getTagType() {
        return tagType;
    }

    public void setTagType(ProductTagType tagType) {
        this.tagType = tagType;
    }

    @Column(name = "onlineTime")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getOnlineTime() {
        return onlineTime;
    }

    public void setOnlineTime(DateTime onlineTime) {
        this.onlineTime = onlineTime;
    }

    @Column(name = "onLineTimeLong")
    @Basic
    public Long getOnLineTimeLong() {
        return onLineTimeLong;
    }

    public void setOnLineTimeLong(Long onLineTimeLong) {
        this.onLineTimeLong = onLineTimeLong;
    }

    @Column(name = "offlineTime")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getOfflineTime() {
        return offlineTime;
    }

    public void setOfflineTime(DateTime offlineTime) {
        this.offlineTime = offlineTime;
    }

    @Column(name = "isDelete")
    @Basic
    public Boolean getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Boolean isDelete) {
        this.isDelete = isDelete;
    }

    @Column(name = "createTime")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Override
    public DateTime getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(DateTime createTime) {
        this.createTime = createTime;
    }

    @Column(name = "updateTime")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getUpdateTime() {
        return updateTime;
    }

    @Override
    public void setUpdateTime(DateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Column(name = "recommendDesc")
    @Basic
    public String getRecommendDesc() {
        return recommendDesc;
    }

    public void setRecommendDesc(String recommendDesc) {
        this.recommendDesc = recommendDesc;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerId", insertable = false, updatable = false)
    public Designer getCustomer() {
        return customer;
    }

    public void setCustomer(Designer customer) {
        this.customer = customer;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getModelInfo() {
        return modelInfo;
    }

    public void setModelInfo(String modelInfo) {
        this.modelInfo = modelInfo;
    }

    public Integer getDesignerSizeId() {
        return designerSizeId;
    }

    public void setDesignerSizeId(Integer designerSizeId) {
        this.designerSizeId = designerSizeId;
    }
}