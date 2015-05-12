package ordercenter.models;

import common.models.utils.EntityClass;
import common.utils.Money;
import ordercenter.payment.alipay.AlipayUtil;
import ordercenter.payment.constants.PayType;
import ordercenter.payment.constants.ResponseType;
import ordercenter.payment.tenpay.TenpayUtils;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.util.Map;

/**
 * 交易信息
 * User: lidujun
 * Date: 2015-05-08
 */
@Table(name = "Trade")
@Entity
public class Trade implements EntityClass<Integer> {

    /**
     *主键 id
     */
    private Integer id;

    /**
     * 业务方式：订单or优惠券（交易的方式)
     */
    private String bizType;

    /**
     * 交易号
     */
    private String tradeNo;

    /**
     * 第三方平台交易号
     */
    private String outerTradeNo;

    /**
     * 买家在第三方支付平台的用户id
     */
    private String outerBuyerId;

    /**
     * 买家在第三方支付平台的账户
     */
    private String outerBuyerAccount;

    /**
     * 第三方支付平台类型：支付宝：alipay, 财付通：tenpay
     */
    private String outerPlatformType;

    /**
     *第三方支付平台的支付类型：例如及时到账，网银网关，快捷支付
     */
    private String payMethod;

    /**
     * 是那家银行
     */
    private String defaultbank;

    /**
     * 支付金额
     */
    private Money payTotalFee = Money.valueOf(0);

    /**
     * 交易状态
     */
    private String tradeStatus;

    /**
     * 第三方消息ID
     */
    private String notifyId;

    /**
     * 第三方消息类型
     */
    private String notifyType;

    /**
     * 第三方支付平台交易创建时间
     */
    private DateTime tradeGmtCreateTime;

    /**
     * 第三支付平台交易修改时间
     */
    private DateTime tradeGmtModifyTime;

    /**
     * 记录创建时间
     */
    private DateTime gmtCreateTime;

    /**
     * 记录修改时间
     */
    private DateTime gmtModifyTime;

    /**
     * 只用于后台展示
     */
    private String orderNo;


    /**
     * 交易签名是否真实
     */
    @Transient
    public boolean verify(Map<String, String> backParams, ResponseType type) {
        if (PayType.Alipay.getValue().equalsIgnoreCase(outerPlatformType)) {
            return AlipayUtil.verify(type.getValue(), backParams);
        }
        if (PayType.TenPay.getValue().equalsIgnoreCase(outerPlatformType)) {
            return TenpayUtils.verify(type.getValue(), backParams);
        }
        return false;
    }

    /**
     * 是否成功扣款
     */
    @Transient
    public boolean isSuccess() {
        if (PayType.Alipay.getValue().equalsIgnoreCase(outerPlatformType)) {
            if ("TRADE_FINISHED".equalsIgnoreCase(tradeStatus) || "TRADE_SUCCESS".equalsIgnoreCase(tradeStatus)) {
                return true;
            }
        }
        if (PayType.TenPay.getValue().equalsIgnoreCase(outerPlatformType)) {
            if ("0".equalsIgnoreCase(tradeStatus)) {
                return true;
            }
        }
        return false;
    }


//    public String getPayType() {
//        try {
//            if (outerPlatformType.equalsIgnoreCase(defaultbank))
//                return Enum.valueOf(PayMethod.class, payMethod).toDesc();
//
//            return Enum.valueOf(PayBank.class, defaultbank.substring(0, 1).toUpperCase() + defaultbank.substring(1)).toDesc();
//        } catch (Exception e) {
//            log.warn("转换时出错! defaultbank: " + defaultbank + ", payMethod: " + payMethod);
//            return defaultbank;
//        }
//    }



//    public String getTradeDate() {
//        return DateUtils.formatDate(gmtCreateTime, DateUtils.DateFormatType.DATE_FORMAT_STR);
//    }
//
//    public long getPayTotalFee() {
//        return payTotalFee;
//    }
//    public String getPayTotalFeeTOString(){
//        return Money.getMoneyString(payTotalFee);
//    }
//
//    public String getPayTotal() {
//        return Money.getMoneyString(payTotalFee);
//    }

    @Override
    public String toString() {
        return "TradeInfo{" +
                "id=" + id +
                ", bizType='" + bizType + '\'' +
                ", tradeNo='" + tradeNo + '\'' +
                ", outerTradeNo='" + outerTradeNo + '\'' +
                ", outerBuyerId='" + outerBuyerId + '\'' +
                ", outerBuyerAccount='" + outerBuyerAccount + '\'' +
                ", outerPlatformType='" + outerPlatformType + '\'' +
                ", payMethod='" + payMethod + '\'' +
                ", defaultbank='" + defaultbank + '\'' +
                ", payTotalFee=" + payTotalFee +
                ", tradeStatus='" + tradeStatus + '\'' +
                ", notifyId='" + notifyId + '\'' +
                ", notifyType='" + notifyType + '\'' +
                ", tradeGmtCreateTime=" + tradeGmtCreateTime +
                ", tradeGmtModifyTime=" + tradeGmtModifyTime +
                ", gmtCreateTime=" + gmtCreateTime +
                ", gmtModifyTime=" + gmtModifyTime +
                ", orderNo='" + orderNo + '\'' +
                '}';
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

    @Column(name = "bizType")
    @Basic
    public String getBizType() {
        return bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType;
    }

    @Column(name = "tradeNo")
    @Basic
    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    @Column(name = "outerTradeNo")
    @Basic
    public String getOuterTradeNo() {
        return outerTradeNo;
    }

    public void setOuterTradeNo(String outerTradeNo) {
        this.outerTradeNo = outerTradeNo;
    }

    @Column(name = "outerBuyerId")
    @Basic
    public String getOuterBuyerId() {
        return outerBuyerId;
    }

    public void setOuterBuyerId(String outerBuyerId) {
        this.outerBuyerId = outerBuyerId;
    }

    @Column(name = "outerBuyerAccount")
    @Basic
    public String getOuterBuyerAccount() {
        return outerBuyerAccount;
    }

    public void setOuterBuyerAccount(String outerBuyerAccount) {
        this.outerBuyerAccount = outerBuyerAccount;
    }

    @Column(name = "outerPlatformType")
    @Basic
    public String getOuterPlatformType() {
        return outerPlatformType;
    }

    public void setOuterPlatformType(String outerPlatformType) {
        this.outerPlatformType = outerPlatformType;
    }

    @Column(name = "payMethod")
    @Basic
    public String getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(String payMethod) {
        this.payMethod = payMethod;
    }

    @Column(name = "defaultbank")
    @Basic
    public String getDefaultbank() {
        return defaultbank;
    }

    public void setDefaultbank(String defaultbank) {
        this.defaultbank = defaultbank;
    }

    @Column(name = "payTotalFee")
    @Type(type="common.utils.hibernate.MoneyType")
    public Money getPayTotalFee() {
        return payTotalFee;
    }

    public void setPayTotalFee(Money payTotalFee) {
        this.payTotalFee = payTotalFee;
    }

    @Column(name = "tradeStatus")
    @Basic
    public String getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    @Column(name = "notifyId")
    @Basic
    public String getNotifyId() {
        return notifyId;
    }

    public void setNotifyId(String notifyId) {
        this.notifyId = notifyId;
    }

    @Column(name = "notifyType")
    @Basic
    public String getNotifyType() {
        return notifyType;
    }

    public void setNotifyType(String notifyType) {
        this.notifyType = notifyType;
    }

    @Column(name = "tradeGmtCreateTime")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getTradeGmtCreateTime() {
        return tradeGmtCreateTime;
    }

    public void setTradeGmtCreateTime(DateTime tradeGmtCreateTime) {
        this.tradeGmtCreateTime = tradeGmtCreateTime;
    }

    @Column(name = "tradeGmtModifyTime")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getTradeGmtModifyTime() {
        return tradeGmtModifyTime;
    }

    public void setTradeGmtModifyTime(DateTime tradeGmtModifyTime) {
        this.tradeGmtModifyTime = tradeGmtModifyTime;
    }

    @Column(name = "gmtCreateTime")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getGmtCreateTime() {
        return gmtCreateTime;
    }

    public void setGmtCreateTime(DateTime gmtCreateTime) {
        this.gmtCreateTime = gmtCreateTime;
    }

    @Column(name = "gmtModifyTime")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getGmtModifyTime() {
        return gmtModifyTime;
    }

    public void setGmtModifyTime(DateTime gmtModifyTime) {
        this.gmtModifyTime = gmtModifyTime;
    }

    @Transient
    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
}

