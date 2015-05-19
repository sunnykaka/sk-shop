package ordercenter.models;

import common.models.utils.EntityClass;
import common.models.utils.OperableData;
import ordercenter.constants.TradeType;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;

/**
 * 交易订单
 * User: lidujun
 * Date: 2015-05-11
 */
@Table(name = "TradeOrder")
@Entity
public class TradeOrder implements EntityClass<Integer>, OperableData {

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 交易流水号
     */
    private String tradeNo;

    /**
     *订单id
     */
    private Integer orderId;

    /**
     *订单编号
     */
    private Long orderNo;

    /**
     * 状态(0.创建, 1.付款成功), 默认是 0
     */
    private Boolean payFlag;

    private TradeType tradeType;

    /**
     * 创建时间
     */
    private DateTime createTime;

    /**
     * 更新时间
     */
    private DateTime updateTime;

    /**
     * 是否删除(0表示未删除, 1表示已删除)
     */
    private Boolean isDelete;

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

    @Column(name = "tradeNo")
    @Basic
    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    @Column(name = "orderId")
    @Basic
    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    @Column(name = "orderNo")
    @Basic
    public Long getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    @Column(name = "payFlag")
    @Basic
    public Boolean isPayFlag() {
        return payFlag;
    }

    public void setPayFlag(Boolean payFlag) {
        this.payFlag = payFlag;
    }

    @Column(name = "tradeType")
    @Enumerated(EnumType.STRING)
    public TradeType getTradeType() {
        return tradeType;
    }

    public void setTradeType(TradeType tradeType) {
        this.tradeType = tradeType;
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

    @Column(name = "isDelete")
    @Basic
    public Boolean getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Boolean isDelete) {
        this.isDelete = isDelete;
    }
}
