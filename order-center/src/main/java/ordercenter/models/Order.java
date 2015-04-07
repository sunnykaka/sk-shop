package ordercenter.models;

import common.models.utils.EntityClass;
import common.models.utils.OperableData;
import common.utils.Money;
import ordercenter.constants.OrderStatus;
import ordercenter.constants.OrderType;
import ordercenter.constants.PlatformType;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User: liubin
 * Date: 14-3-28
 */
@Table(name = "order_base")
@Entity
public class Order implements EntityClass<Integer>, OperableData {

    private Integer id;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 订单类型
     */
    private OrderType type = OrderType.NORMAL;

    /**
     * 订单状态
     */
    private OrderStatus status;

    /**
     * 平台整单优惠
     */
    private Money sharedDiscountFee = Money.valueOf(0);

    /**
     * 邮费
     */
    private Money postFee = Money.valueOf(0);

    /**
     * 实付金额
     */
    private Money actualFee = Money.valueOf(0);

    /**
     * 买家ID
     */
    private String buyerId;

    /**
     * 买家留言
     */
    private String buyerMessage;

    /**
     * 备注
     */
    private String remark;

    /**
     * 下单时间
     */
    private DateTime buyTime;

    /**
     * 支付时间
     */
    private DateTime payTime;

    /**
     * 是否需要发票
     */
    private Boolean needReceipt = false;

    /**
     * 发票抬头
     */
    private String receiptTitle;

    /**
     * 发票内容
     */
    private String receiptContent;

    private DateTime createTime;

    private DateTime updateTime;

    //操作人ID
    private Integer operatorId;

    /**
     * 发货ID
     */
    private Integer invoiceId;

    /**
     * 发货实体
     */
    private Invoice invoice;


    /**
     * 平台类型
     */
    private PlatformType platformType;

    //订单项
    private List<OrderItem> orderItemList = new ArrayList<OrderItem>(0);


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order")
    public List<OrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<OrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", insertable = false, updatable = false)
    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    @Column(name = "order_no")
    @Basic
    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }


    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    public OrderType getType() {
        return type;
    }

    public void setType(OrderType type) {
        this.type = type;
    }

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    @Column(name = "shared_discount_fee")
    @Type(type="common.utils.hibernate.MoneyType")
    public Money getSharedDiscountFee() {
        return sharedDiscountFee;
    }

    public void setSharedDiscountFee(Money sharedDiscountFee) {
        this.sharedDiscountFee = sharedDiscountFee;
    }


    @Column(name = "actual_fee")
    @Type(type="common.utils.hibernate.MoneyType")
    public Money getActualFee() {
        return actualFee;
    }

    public void setActualFee(Money actualFee) {
        this.actualFee = actualFee;
    }

    @Column(name = "post_fee")
    @Type(type="common.utils.hibernate.MoneyType")
    public Money getPostFee() {
        return postFee;
    }

    public void setPostFee(Money postFee) {
        this.postFee = postFee;
    }

    @Column(name = "buyer_id")
    @Basic
    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    @Column(name = "buyer_message")
    @Basic
    public String getBuyerMessage() {
        return buyerMessage;
    }

    public void setBuyerMessage(String buyerMessage) {
        this.buyerMessage = buyerMessage;
    }


    @Column(name = "remark")
    @Basic
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }



    @Column(name = "buy_time")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getBuyTime() {
        return buyTime;
    }

    public void setBuyTime(DateTime buyTime) {
        this.buyTime = buyTime;
    }


    @Column(name = "pay_time")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getPayTime() {
        return payTime;
    }

    public void setPayTime(DateTime payTime) {
        this.payTime = payTime;
    }


    @Column(name = "need_receipt")
    @Basic
    public Boolean getNeedReceipt() {
        return needReceipt;
    }

    public void setNeedReceipt(Boolean needReceipt) {
        this.needReceipt = needReceipt;
    }


    @Column(name = "receipt_title")
    @Basic
    public String getReceiptTitle() {
        return receiptTitle;
    }

    public void setReceiptTitle(String receiptTitle) {
        this.receiptTitle = receiptTitle;
    }


    @Column(name = "receipt_content")
    @Basic
    public String getReceiptContent() {
        return receiptContent;
    }

    public void setReceiptContent(String receiptContent) {
        this.receiptContent = receiptContent;
    }




    @Column(name = "create_time")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(DateTime createTime) {
        this.createTime = createTime;
    }


    @Column(name = "update_time")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(DateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Column(name = "operator_id")
    @Basic
    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }


    @Column(name = "invoice_id")
    @Basic
    public Integer getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Integer invoiceId) {
        this.invoiceId = invoiceId;
    }


    @Column(name = "platform_type")
    @Enumerated(EnumType.STRING)
    public PlatformType getPlatformType() {
        return platformType;
    }

    public void setPlatformType(PlatformType platformType) {
        this.platformType = platformType;
    }


}
