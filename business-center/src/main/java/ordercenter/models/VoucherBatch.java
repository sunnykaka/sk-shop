package ordercenter.models;

import common.models.utils.EntityClass;
import common.models.utils.OperableData;
import common.utils.Money;
import ordercenter.constants.VoucherBatchStatus;
import ordercenter.constants.VoucherType;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;

/**
 * User: liubin
 * Date: 14-11-5
 */
@Table(name = "voucher_batch")
@Entity
public class VoucherBatch implements EntityClass<Integer>, OperableData {

    private Integer id;

    private VoucherBatchStatus status;

    /**
     * 状态
     */
    private VoucherType type;


    /**
     * 代金券面额
     */
    private Money amount = Money.valueOf(0);

    /**
     * 唯一编号
     */
    private String uniqueNo;


    /**
     * 开始时间
     */
    private DateTime startTime;

    /**
     * 开始时间，可为空
     */
    private DateTime endTime;

    /**
     * 代金券截止日期
     */
    private DateTime deadline;

    /**
     * 代金券有效期（天），从领取时间算起
     */
    private Integer periodDay;

    /**
     * 代金券使用的最小订单金额
     */
    private Money minOrderAmount = Money.valueOf(0);

    /**
     * 代金券发放数量，为空则无限制
     */
    private Integer maxQuantity;


    private DateTime createTime;

    /**
     * 备注
     */
    private String remark;


    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    public VoucherBatchStatus getStatus() {
        return status;
    }

    public void setStatus(VoucherBatchStatus status) {
        this.status = status;
    }

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    public VoucherType getType() {
        return type;
    }

    public void setType(VoucherType type) {
        this.type = type;
    }

    @Column(name = "amount")
    @Type(type="common.utils.hibernate.MoneyType")
    public Money getAmount() {
        return amount;
    }

    public void setAmount(Money amount) {
        this.amount = amount;
    }

    @Column(name = "uniqueNo")
    public String getUniqueNo() {
        return uniqueNo;
    }

    public void setUniqueNo(String uniqueNo) {
        this.uniqueNo = uniqueNo;
    }

    @Column(name = "startTime")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(DateTime startTime) {
        this.startTime = startTime;
    }

    @Column(name = "endTime")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(DateTime endTime) {
        this.endTime = endTime;
    }

    @Column(name = "deadline")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(DateTime deadline) {
        this.deadline = deadline;
    }

    @Column(name = "periodDay")
    public Integer getPeriodDay() {
        return periodDay;
    }

    public void setPeriodDay(Integer periodDay) {
        this.periodDay = periodDay;
    }

    @Column(name = "minOrderAmount")
    @Type(type="common.utils.hibernate.MoneyType")
    public Money getMinOrderAmount() {
        return minOrderAmount;
    }

    public void setMinOrderAmount(Money minOrderAmount) {
        this.minOrderAmount = minOrderAmount;
    }

    @Column(name = "maxQuantity")
    public Integer getMaxQuantity() {
        return maxQuantity;
    }

    public void setMaxQuantity(Integer maxQuantity) {
        this.maxQuantity = maxQuantity;
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

    @Column(name = "remark")
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 计算代金券的截止时间
     * @param now
     * @return
     */
    public DateTime computeVoucherDeadline(DateTime now) {
        DateTime endOfDay = deadline;
        if(endOfDay == null) {
            endOfDay = now.plusDays(periodDay);
        }
        return endOfDay.withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59);
    }
}
