package ordercenter.models;

import common.models.utils.EntityClass;
import common.models.utils.OperableData;
import common.utils.Money;
import ordercenter.constants.OrderState;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import productcenter.constants.StoreStrategy;

import javax.persistence.*;

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
     * 主图
     */
    private String skuMainPicture;

    /**
     * 条形码
     */
    private String barCode;

    /**
     * 编号
     */
    private String itemNo;

    /**
     * 库位ID(库存位置)
     */
    private int storageId;

    /**
     * 冗余商品ID
     */
    private int productId;

    /**
     *类目ID
     */
    private int categoryId;

    /**
     * 商家ID
     */
    private int customerId;

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
//    /**
//     * 原先的订单状态, 只在更新 SQL 时有效, 不写入数据库
//     */
//    private OrderState mustPreviousState;

    /**
     * 创建时间
     */
    private DateTime createTime;

    /**
     * 更新时间
     */
    private DateTime updateTime;

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
    public String getSkuMainPicture() {
        return skuMainPicture;
    }

    public void setSkuMainPicture(String skuMainPicture) {
        this.skuMainPicture = skuMainPicture;
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
    public int getStorageId() {
        return storageId;
    }

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
    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    @Column(name = "customerId")
    @Basic
    public int getCustomerId() {
        return customerId;
    }

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
}
