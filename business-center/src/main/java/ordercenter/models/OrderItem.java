package ordercenter.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import common.models.utils.EntityClass;
import common.models.utils.OperableData;
import common.utils.Money;
import ordercenter.constants.OrderState;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import productcenter.constants.StoreStrategy;
import productcenter.models.SkuProperty;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单项
 * User: lidujun
 * Date: 2015-05-06
 */
@Table(name = "OrderItem")
@Entity
public class OrderItem extends TradeItem implements EntityClass<Integer>, OperableData {
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 订单ID
     */
    private int orderId;

    /**
     * skuId
     */
    private int skuId;

    /**
     * 订单状态
     */
    private OrderState orderState;

    /**
     * 订单创建时使用的库存策略(NormalStrategy.普通策略, 创建即扣减库存, 取消则回加库存; PayStrategy.付款策略, 付款成功后才会扣减)
     */
    private StoreStrategy storeStrategy;

    /**
     * sku说明（sku属性）
     */
    private String skuExplain;

    /**
     * 编号
     */
    private String itemNo;

    /**
     * 品牌ID
     */
    private int brandId;

    /**
     * 是否已评价(0 false 表示未评价, 1 true 表示已评价), 默认是 0
     */
    private boolean appraise = false;


/*
    `number` int(10) unsigned NOT NULL COMMENT '购买时的 sku 数量',

*/

//    /**
//     * 实际发货数量
//     */
//    private int shipmentNum;
//
//    /**
//     * 已退款数量
//     */
//    private int backNum;
//


    /**
     * 创建时间
     */
    private DateTime createTime;

    /**
     * 更新时间
     */
    private DateTime updateTime;

    @JsonIgnore
    private Order order;

    /** 页面展示数据需求(不存数据库) */
    private List<SkuProperty> properties = new ArrayList<>();

    /** 商品的评论(不存数据库) */
    private Valuation valuation;

    /**
     * 原先的订单状态, 只在更新 SQL 时有效, 不写入数据库
     */
    private OrderState mustPreviousState;

    @Transient
    public OrderState getMustPreviousState() {
        return mustPreviousState;
    }

    public void setMustPreviousState(OrderState mustPreviousState) {
        this.mustPreviousState = mustPreviousState;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "id=" + id +
                ", orderId=" + orderId +
                ", skuId=" + skuId +
                ", orderState=" + orderState +
                ", storeStrategy=" + storeStrategy +
                ", skuExplain='" + skuExplain + '\'' +
                ", itemNo='" + itemNo + '\'' +
                ", brandId=" + brandId +
                ", appraise=" + appraise +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", order=" + order +
                ", valuation=" + valuation +
                ", mustPreviousState=" + mustPreviousState +
                "} " + super.toString();
    }

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "orderId")
    @Basic
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    @Column(name = "skuId")
    @Basic
    @Override
    public int getSkuId() {
        return skuId;
    }

    @Override
    public void setSkuId(int skuId) {
        this.skuId = skuId;
    }

    @Column(name = "unitPrice")
    @Type(type="common.utils.hibernate.MoneyType")
    @Override
    public Money getCurUnitPrice() {
        return curUnitPrice;
    }

    @Override
    public void setCurUnitPrice(Money curUnitPrice) {
        this.curUnitPrice = curUnitPrice;
    }

    @Column(name = "itemTotalPrice")
    @Type(type="common.utils.hibernate.MoneyType")
    public Money getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Money totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Column(name = "orderState")
    @Enumerated(EnumType.STRING)
    public OrderState getOrderState() {
        return orderState;
    }

    public void setOrderState(OrderState orderState) {
        this.orderState = orderState;
    }

    @Column(name = "storeStrategy")
    @Enumerated(EnumType.STRING)
    public StoreStrategy getStoreStrategy() {
        return storeStrategy;
    }

    public void setStoreStrategy(StoreStrategy storeStrategy) {
        this.storeStrategy = storeStrategy;
    }

    @Column(name = "skuExplain")
    @Basic
    public String getSkuExplain() {
        return skuExplain;
    }

    public void setSkuExplain(String skuExplain) {
        this.skuExplain = skuExplain;
    }

    @Column(name = "skuMainPicture")
    @Basic
    @Override
    public String getMainPicture() {
        return mainPicture;
    }

    public void setMainPicture(String mainPicture) {
        this.mainPicture = mainPicture;
    }

    @Column(name = "skuName")
    @Basic
    @Override
    public String getProductName() {
        return productName;
    }

    @Override
    public void setProductName(String productName) {
        this.productName = productName;
    }

    @Column(name = "barCode")
    @Basic
    @Override
    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    @Column(name = "itemNo")
    @Basic
    public String getItemNo() {
        return itemNo;
    }

    public void setItemNo(String itemNo) {
        this.itemNo = itemNo;
    }

    @Column(name = "storageId")
    @Basic
    @Override
    public int getStorageId() {
        return storageId;
    }

    @Override
    public void setStorageId(int storageId) {
        this.storageId = storageId;
    }

    @Column(name = "productId")
    @Basic
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    @Column(name = "categoryId")
    @Basic
    @Override
    public int getCategoryId() {
        return categoryId;
    }

    @Override
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    @Column(name = "customerId")
    @Basic
    @Override
    public int getCustomerId() {
        return customerId;
    }

    @Override
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    @Column(name = "brandId")
    @Basic
    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    @Column(name = "appraise")
    @Basic
    public boolean isAppraise() {
        return appraise;
    }

    public void setAppraise(boolean appraise) {
        this.appraise = appraise;
    }

    @Column(name = "createDate")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Override
    public DateTime getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(DateTime createTime) {
        this.createTime = createTime;
    }

    @Column(name = "updateDate")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Override
    public DateTime getUpdateTime() {
        return updateTime;
    }

    @Override
    public void setUpdateTime(DateTime updateTime) {
        this.updateTime = updateTime;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderId", insertable = false, updatable = false)
    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    @Column(name = "number")
    @Basic
    @Override
    public int getNumber() {
        return number;
    }

    @Override
    public void setNumber(int number) {
        this.number = number;
    }

    @Transient
    public List<SkuProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<SkuProperty> properties) {
        this.properties = properties;
    }

    @Transient
    public Valuation getValuation() {
        return valuation;
    }

    public void setValuation(Valuation valuation) {
        this.valuation = valuation;
    }
}
