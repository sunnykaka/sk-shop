package ordercenter.models;

import common.models.utils.EntityClass;
import common.utils.Money;
import ordercenter.constants.OrderItemStatus;
import ordercenter.constants.OrderItemType;
import ordercenter.constants.PlatformType;
import org.hibernate.annotations.Type;

import javax.persistence.*;

/**
 * User: liubin
 * Date: 14-3-28
 */
@Table(name = "order_item")
@Entity
public class OrderItem implements EntityClass<Integer> {

    private Integer id;

    /**
     * 平台类型
     */
    private PlatformType platformType;

    /**
     * 产品ID
     */
    private Integer productId;


    /**
     * 产品编码
     */
    private String productCode;

    /**
     * 产品SKU
     */
    private String productSku;

    /**
     * 产品名称
     */
    private String productName;


    //订单ID
    private Integer orderId;

    private Order order;

    /**
     * 订单项状态
     */
    private OrderItemStatus status;

    /**
     * 订单项类型
     */
    private OrderItemType type;

    /**
     * 一口价
     */
    private Money price = Money.valueOf(0);

    /**
     * 购买数量
     */
    private Integer buyCount;

    /**
     * 订单项优惠
     */
    private Money discountFee = Money.valueOf(0);


    /**
     * 平台整单优惠
     */
    private Money sharedDiscountFee = Money.valueOf(0);

    /**
     * 实付金额
     */
    private Money actualFee = Money.valueOf(0);


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }


    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "price")
    @Type(type="common.utils.hibernate.MoneyType")
    public Money getPrice() {
        return price;
    }

    public void setPrice(Money price) {
        this.price = price;
    }


    @Column(name = "buy_count")
    @Basic
    public Integer getBuyCount() {
        return buyCount;
    }

    public void setBuyCount(Integer buyCount) {
        this.buyCount = buyCount;
    }

    @Column(name = "actual_fee")
    @Type(type="common.utils.hibernate.MoneyType")
    public Money getActualFee() {
        return actualFee;
    }

    public void setActualFee(Money actualFee) {
        this.actualFee = actualFee;
    }


    @Column(name = "shared_discount_fee")
    @Type(type="common.utils.hibernate.MoneyType")
    public Money getSharedDiscountFee() {
        return sharedDiscountFee;
    }

    public void setSharedDiscountFee(Money sharedDiscountFee) {
        this.sharedDiscountFee = sharedDiscountFee;
    }


    @Column(name = "product_id")
    @Basic
    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }


    @Column(name = "product_code")
    @Basic
    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }


    @Column(name = "product_sku")
    @Basic
    public String getProductSku() {
        return productSku;
    }

    public void setProductSku(String productSku) {
        this.productSku = productSku;
    }

    @Column(name = "product_name")
    @Basic
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }


    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    public OrderItemType getType() {
        return type;
    }

    public void setType(OrderItemType type) {
        this.type = type;
    }


    @Column(name = "order_id")
    @Basic
    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }


    @Column(name = "platform_type")
    @Enumerated(EnumType.STRING)
    public PlatformType getPlatformType() {
        return platformType;
    }

    public void setPlatformType(PlatformType platformType) {
        this.platformType = platformType;
    }

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    public OrderItemStatus getStatus() {
        return status;
    }

    public void setStatus(OrderItemStatus status) {
        this.status = status;
    }

    @Column(name = "discount_fee")
    @Type(type="common.utils.hibernate.MoneyType")
    public Money getDiscountFee() {
        return discountFee;
    }

    public void setDiscountFee(Money discountFee) {
        this.discountFee = discountFee;
    }

    /**
     * 计算促销价
     */
    public Money calculateDiscountPrice() {
        return calculateOrderItemFee().divide(getBuyCount());
    }

    /**
     * 计算订单项金额, 一口价 * 数量 - 订单项优惠
     */
    public Money calculateOrderItemFee() {
        return getPrice().multiply(getBuyCount()).subtract(getDiscountFee());
    }

    /**
     * 获取买家实付金额（发货单上订单项实付金额）
     *
     * @return
     */
    @Transient
    public Money getUserActualPrice() {

        return calculateDiscountPrice().multiply(getBuyCount()).subtract(getSharedDiscountFee());

    }

}
