package ordercenter.models;

import common.models.utils.EntityClass;
import common.utils.Money;
import ordercenter.constants.TradePayType;
import ordercenter.constants.OrderState;
import ordercenter.payment.constants.PayBank;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import usercenter.constants.AccountType;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单对象
 * 这个对象是否有效必须系统仔细确认，比如可能发生如下情况：
 * 1，没货了
 * 2，等待在线付款
 * 订单真正生效就进入实际的物流流程，订单从入库开始在它上面就会发生一系列事件，同时也开始了状态迁移
 * User: lidujun
 * Date: 2015-05-06
 */
@Table(name = "ordertable")
@Entity
public class Order implements EntityClass<Integer> {
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 订单编号
     */
    private long orderNo;

    /**
     * 仓库Id
     */
    private int storageId;

    /**
     * 仓库名(快照冗余)
     */
    private String storageName;

    /**
     * 设计师Id
     */
    private int customerId;

    /**
     * 设计师名(快照冗余)
     */
    private String supplierName;

    /**
     * 订单所发的物流公司(顺风, 中通等), 与订单物流表中冗余
     */
    //private DeliveryInfo.DeliveryType deliveryType;
    /**
     *   //物流
     private Logistics logistics;
     */

    /**
     * 是否是刷单, 0 表示不是刷单, 1 表示是刷单, 默认是 0
     */
    private boolean brush;

    /**
     * 是否删除(1 表示已删除, 0 表示未删除), 默认是 0
     */
    private boolean isDelete = false;

    /**
     * 订单状态
     */
    private OrderState orderState;

    /**
     * 必须的订单前置状态, 只在更新 SQL 时有效, 不写入数据库
     */
    private OrderState mustPreviousState;

    /**
     * 下的单人(客户)Id
     */
    private int userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     *  账户类型，可区别出来自什么网站，比如QQ，sina，KRQ, weibo等
     */
    private AccountType accountType;

    /**
     * 订单支付类型(在线支付、pos机刷卡或现金支付)
     */
    private TradePayType payType;

    /**
     * 订单总价(单位:元. 计算到分)：订单是永久存储的，所以这里保存了这次订单总价格
     */
    private Money totalMoney = Money.valueOf(0);

    /**
     * 支付银行
     */
    private PayBank payBank;

    /**
     * 支付时间
     */
    private DateTime payDate;

    /**
     * 是否开发票
     */
    private boolean invoice;

    /**
     * 发票类型(普通发票或增值税)
     */
    private String invoiceType; //ldj

    /**
     * 客服修改发票信息冗余(发票类型)
     */
    private String invoiceTypeRewrite;  //ldj

    /**
     * 单位名
     */
    private String companyName;

    /**
     * 客服修改发票信息冗余(单位名)
     */
    private String companyNameRewrite;

    /**
     * 发票抬头(个人或单位)
     */
    private String invoiceTitle;

    /**
     * 客服修改发票信息冗余(发票抬头)
     */
    private String invoiceTitleRewrite;

    /**
     * 发票内容(明细, 办公用品等)
     */
    private String invoiceContent;

    /**
     * 客服修改发票信息冗余(发票内容)
     */
    private String invoiceContentRewrite;

    /**
     * 修改发票的客服
     */
    private String editor;

    /**
     * 修改发票时间
     */
    private DateTime timeRewrite;

    /**
     * 用户取消订单时间
     */
    private DateTime cancelDate;

    /**
     * 最终时间(取消 关闭 交易完成)
     */
    private DateTime endDate;

    /**
     * 创建时间
     */
    private DateTime createTime;

    /**
     * 下单时间的long型表示('创建订单时的时间戳')
     */
    private long milliDate;

    /**
     * 更新时间
     */
    private DateTime updateTime;

    private String addressName;

    /**
     * 订单项（还没有做好）
     */
    private List<OrderItem> orderItemList = new ArrayList<OrderItem>();


    @Override
    public String toString() {
        return "Order{" +
                "addressName='" + addressName + '\'' +
                ", id=" + id +
                ", orderNo=" + orderNo +
                ", storageId=" + storageId +
                ", storageName='" + storageName + '\'' +
                ", customerId=" + customerId +
                ", supplierName='" + supplierName + '\'' +
                ", brush=" + brush +
                ", isDelete=" + isDelete +
                ", orderState=" + orderState +
                ", mustPreviousState=" + mustPreviousState +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", accountType=" + accountType +
                ", payType=" + payType +
                ", totalMoney=" + totalMoney +
                ", payBank=" + payBank +
                ", payDate=" + payDate +
                ", invoice=" + invoice +
                ", invoiceType='" + invoiceType + '\'' +
                ", invoiceTypeRewrite='" + invoiceTypeRewrite + '\'' +
                ", companyName='" + companyName + '\'' +
                ", companyNameRewrite='" + companyNameRewrite + '\'' +
                ", invoiceTitle='" + invoiceTitle + '\'' +
                ", invoiceTitleRewrite='" + invoiceTitleRewrite + '\'' +
                ", invoiceContent='" + invoiceContent + '\'' +
                ", invoiceContentRewrite='" + invoiceContentRewrite + '\'' +
                ", editor='" + editor + '\'' +
                ", timeRewrite=" + timeRewrite +
                ", cancelDate=" + cancelDate +
                ", endDate=" + endDate +
                ", createTime=" + createTime +
                ", milliDate=" + milliDate +
                ", updateTime=" + updateTime +
                '}';
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order")
    public List<OrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<OrderItem> orderItemList) {
        this.orderItemList = orderItemList;
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

    @Column(name = "orderNo")
    @Basic
    public long getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(long orderNo) {
        this.orderNo = orderNo;
    }

    @Column(name = "storageId")
    @Basic
    public int getStorageId() {
        return storageId;
    }

    public void setStorageId(int storageId) {
        this.storageId = storageId;
    }



    @Transient
    public String getStorageName() {
        return storageName;
    }

    public void setStorageName(String storageName) {
        this.storageName = storageName;
    }



    @Column(name = "customerId")
    @Basic
    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    @Transient
    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    @Column(name = "brush")
    @Basic
    public boolean isBrush() {
        return brush;
    }

    public void setBrush(boolean brush) {
        this.brush = brush;
    }

    @Column(name = "isDelete")
    @Basic
    public boolean getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(boolean isDelete) {
        this.isDelete = isDelete;
    }

    @Column(name = "orderState")
    @Enumerated(EnumType.STRING)
    public OrderState getOrderState() {
        return orderState;
    }

    public void setOrderState(OrderState orderState) {
        this.orderState = orderState;
    }



    @Transient
    public OrderState getMustPreviousState() {
        return mustPreviousState;
    }

    public void setMustPreviousState(OrderState mustPreviousState) {
        this.mustPreviousState = mustPreviousState;
    }



    @Column(name = "userId")
    @Basic
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }



    @Column(name = "userName")
    @Basic
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    @Column(name = "accountType")
    @Enumerated(EnumType.STRING)
    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    @Column(name = "payType")
    @Enumerated(EnumType.STRING)
    public TradePayType getPayType() {
        return payType;
    }

    public void setPayType(TradePayType payType) {
        this.payType = payType;
    }

    @Column(name = "totalPrice")
    @Type(type="common.utils.hibernate.MoneyType")
    public Money getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(Money totalMoney) {
        this.totalMoney = totalMoney;
    }

    @Column(name = "payBank")
    @Enumerated(EnumType.STRING)
    public PayBank getPayBank() {
        return payBank;
    }

    public void setPayBank(PayBank payBank) {
        this.payBank = payBank;
    }

    @Column(name = "payDate")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getPayDate() {
        return payDate;
    }

    public void setPayDate(DateTime payDate) {
        this.payDate = payDate;
    }

    @Column(name = "invoice")
    @Basic
    public boolean isInvoice() {
        return invoice;
    }

    public void setInvoice(boolean invoice) {
        this.invoice = invoice;
    }

    @Column(name = "invoiceType")
    @Basic
    public String getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }

    @Column(name = "invoiceTypeRewrite")
    @Basic
    public String getInvoiceTypeRewrite() {
        return invoiceTypeRewrite;
    }

    public void setInvoiceTypeRewrite(String invoiceTypeRewrite) {
        this.invoiceTypeRewrite = invoiceTypeRewrite;
    }

    @Column(name = "companyName")
    @Basic
    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    @Column(name = "companyNameRewrite")
    @Basic
    public String getCompanyNameRewrite() {
        return companyNameRewrite;
    }

    public void setCompanyNameRewrite(String companyNameRewrite) {
        this.companyNameRewrite = companyNameRewrite;
    }

    @Column(name = "invoiceTitle")
    @Basic
    public String getInvoiceTitle() {
        return invoiceTitle;
    }

    public void setInvoiceTitle(String invoiceTitle) {
        this.invoiceTitle = invoiceTitle;
    }

    @Column(name = "invoiceTitleRewrite")
    @Basic
    public String getInvoiceTitleRewrite() {
        return invoiceTitleRewrite;
    }

    public void setInvoiceTitleRewrite(String invoiceTitleRewrite) {
        this.invoiceTitleRewrite = invoiceTitleRewrite;
    }

    @Column(name = "invoiceContent")
    @Basic
    public String getInvoiceContent() {
        return invoiceContent;
    }

    public void setInvoiceContent(String invoiceContent) {
        this.invoiceContent = invoiceContent;
    }

    @Column(name = "invoiceContentRewrite")
    @Basic
    public String getInvoiceContentRewrite() {
        return invoiceContentRewrite;
    }

    public void setInvoiceContentRewrite(String invoiceContentRewrite) {
        this.invoiceContentRewrite = invoiceContentRewrite;
    }

    @Column(name = "editor")
    @Basic
    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    @Column(name = "timeRewrite")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getTimeRewrite() {
        return timeRewrite;
    }

    public void setTimeRewrite(DateTime timeRewrite) {
        this.timeRewrite = timeRewrite;
    }

    @Column(name = "cancelDate")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getCancelDate() {
        return cancelDate;
    }

    public void setCancelDate(DateTime cancelDate) {
        this.cancelDate = cancelDate;
    }

    @Column(name = "endDate")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(DateTime endDate) {
        this.endDate = endDate;
    }

    @Column(name = "milliDate")
    @Basic
    public long getMilliDate() {
        return milliDate;
    }

    public void setMilliDate(long milliDate) {
        this.milliDate = milliDate;
    }

    @Column(name = "createDate")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(DateTime createTime) {
        this.createTime = createTime;
    }

    @Column(name = "modifyDate")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(DateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Transient
    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }
}
