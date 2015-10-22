package ordercenter.models;

import common.models.utils.EntityClass;
import common.utils.Money;
import ordercenter.constants.BizType;
import ordercenter.payment.alipay.AlipayUtil;
import ordercenter.payment.constants.PayBank;
import ordercenter.payment.constants.PayChannel;
import ordercenter.payment.constants.ResponseType;
import ordercenter.payment.constants.TradeStatus;
import ordercenter.payment.tenpay.TenpayUtils;
import ordercenter.util.TradeSequenceUtil;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import usercenter.constants.MarketChannel;

import javax.persistence.*;
import java.util.List;
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
     * 主键 id
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
     * 第三方支付平台的支付类型：例如及时到账，网银网关，快捷支付
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
     * 支付商按照传递过去的币种支付的金额
     */
    private String payRetTotalFee;

    /**
     * 传递过去的币种,默认人民币
     */
    private String payCurrency = "CNY";

    private Integer version;


    /**
     * 发起交易的客户端ip地址，不是所有都有
     */
    private String clientIp;

    /**
     * 客户端，默认是浏览器
     */
    private MarketChannel client = MarketChannel.WEB;


    /**
     * 交易包含的订单
     */
    private List<TradeOrder> tradeOrder;

     //TODO 构造私有化
     //    private Trade(){}

    public static class TradeBuilder {


        /**
         * 交易对象的构建
         *
         * @param payTotalFee 支付金额
         * @param bizType     业务类型
         * @param payBank     支付银行
         * @return
         */
        public static Trade createNewTrade(Money payTotalFee, BizType bizType, PayBank payBank, String clientIp, MarketChannel client) {
            Trade trade = new Trade();
            //交易号是自动生成
            trade.setTradeNo(TradeSequenceUtil.getTradeNo());
            trade.setDefaultbank(payBank.toString());
            trade.setOuterPlatformType(payBank.getPayChannel());
            trade.setPayMethod(payBank.getPayMethod().toString());
            trade.setBizType(bizType.toString());   //业务类型
            trade.setGmtCreateTime(new DateTime());
            trade.setPayTotalFee(payTotalFee);
            trade.setTradeStatus(TradeStatus.INIT.toString());//状态为新建状态
            trade.setClientIp(clientIp);
            trade.setClient(client);
            return trade;
        }
    }


    /**
     * 交易签名是否真实
     */
    @Transient
    public boolean verify(Map<String, String> backParams, ResponseType type) {
        if (PayChannel.Alipay.getValue().equalsIgnoreCase(outerPlatformType)) {
            return AlipayUtil.verify(type.getValue(), backParams);
        }
        /**
         * 财付通和微信都是走财付通的通道，回调参数格式均为一样。
         */
        if (PayChannel.TenPay.getValue().equalsIgnoreCase(outerPlatformType) ||
                PayChannel.WXSM.getValue().equalsIgnoreCase(outerPlatformType)) {
            return TenpayUtils.verify(type.getValue(), backParams);
        }
        return false;
    }

    /**
     * 是否成功扣款
     */
    @Transient
    public boolean isSuccess() {
        if (TradeStatus.TRADE_SUCCESS.toString().equalsIgnoreCase(tradeStatus) ||
                TradeStatus.TRADE_FINISHED.toString().equalsIgnoreCase(tradeStatus)) {
            return true;
        }
//        if (PayChannel.Alipay.getValue().equalsIgnoreCase(outerPlatformType)) {
//            if (TradeStatus.TRADE_SUCCESS.toString().equalsIgnoreCase(tradeStatus)) {
//                return true;
//            }
//        }
//        if (PayChannel.TenPay.getValue().equalsIgnoreCase(outerPlatformType) ||
//                PayChannel.WXSM.getValue().equalsIgnoreCase(outerPlatformType)) {
//            if ("0".equalsIgnoreCase(tradeStatus)) {
//                return true;
//            }
//        }
        return false;
    }

    @Override
    public String toString() {
        return "Trade{" +
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
                ", payRetTotalFee='" + payRetTotalFee + '\'' +
                ", payCurrency='" + payCurrency + '\'' +
                '}';
    }

    @Version
    @Column(name="version")
    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Transient
    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    @Transient
    public List<TradeOrder> getTradeOrder() {
        return tradeOrder;
    }

    public void setTradeOrder(List<TradeOrder> tradeOrder) {
        this.tradeOrder = tradeOrder;
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
    @Type(type = "common.utils.hibernate.MoneyType")
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
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getTradeGmtCreateTime() {
        return tradeGmtCreateTime;
    }

    public void setTradeGmtCreateTime(DateTime tradeGmtCreateTime) {
        this.tradeGmtCreateTime = tradeGmtCreateTime;
    }

    @Column(name = "tradeGmtModifyTime")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getTradeGmtModifyTime() {
        return tradeGmtModifyTime;
    }

    public void setTradeGmtModifyTime(DateTime tradeGmtModifyTime) {
        this.tradeGmtModifyTime = tradeGmtModifyTime;
    }

    @Column(name = "gmtCreateTime")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getGmtCreateTime() {
        return gmtCreateTime;
    }

    public void setGmtCreateTime(DateTime gmtCreateTime) {
        this.gmtCreateTime = gmtCreateTime;
    }

    @Column(name = "gmtModifyTime")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getGmtModifyTime() {
        return gmtModifyTime;
    }

    public void setGmtModifyTime(DateTime gmtModifyTime) {
        this.gmtModifyTime = gmtModifyTime;
    }

    @Column(name = "payRetTotalFee")
    @Basic
    public String getPayRetTotalFee() {
        return payRetTotalFee;
    }

    public void setPayRetTotalFee(String payRetTotalFee) {
        this.payRetTotalFee = payRetTotalFee;
    }

    @Column(name = "payCurrency")
    @Basic
    public String getPayCurrency() {
        return payCurrency;
    }

    public void setPayCurrency(String payCurrency) {
        this.payCurrency = payCurrency;
    }

    @Column(name = "client")
    @Enumerated(EnumType.STRING)
    public MarketChannel getClient() {
        return client;
    }

    public void setClient(MarketChannel client) {
        this.client = client;
    }
}

