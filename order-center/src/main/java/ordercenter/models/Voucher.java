package ordercenter.models;

import common.models.utils.EntityClass;
import common.models.utils.OperableData;
import common.utils.Money;
import ordercenter.constants.VoucherBatchStatus;
import ordercenter.constants.VoucherStatus;
import ordercenter.constants.VoucherType;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import usercenter.models.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User: liubin
 * Date: 14-11-5
 */
@Table(name = "voucher")
@Entity
public class Voucher implements EntityClass<Integer>, OperableData {

    private Integer id;

    private VoucherStatus status;

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
     * 代金券截止日期
     */
    private DateTime deadline;

    /**
     * 代金券使用的最小订单金额
     */
    private Money minOrderAmount = Money.valueOf(0);

    /**
     * 领取时间
     */
    private DateTime createTime;

    /**
     * 使用时间
     */
    private DateTime useTime;

    /**
     * 关联用户
     */
    private Integer userId;

    private User user;

    private Integer batchId;

    private VoucherBatch voucherBatch;

    /**
     * 代金券使用
     */
    private List<VoucherUse> voucherUseList = new ArrayList<>(0);


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
    public VoucherStatus getStatus() {
        return status;
    }

    public void setStatus(VoucherStatus status) {
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

    @Column(name = "useTime")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getUseTime() {
        return useTime;
    }

    public void setUseTime(DateTime useTime) {
        this.useTime = useTime;
    }

    @Column(name = "deadline")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(DateTime deadline) {
        this.deadline = deadline;
    }

    @Column(name = "minOrderAmount")
    @Type(type="common.utils.hibernate.MoneyType")
    public Money getMinOrderAmount() {
        return minOrderAmount;
    }

    public void setMinOrderAmount(Money minOrderAmount) {
        this.minOrderAmount = minOrderAmount;
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

    @Column(name = "userId")
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Column(name = "batchId")
    public Integer getBatchId() {
        return batchId;
    }

    public void setBatchId(Integer batchId) {
        this.batchId = batchId;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batchId", insertable = false, updatable = false)
    public VoucherBatch getVoucherBatch() {
        return voucherBatch;
    }

    public void setVoucherBatch(VoucherBatch voucherBatch) {
        this.voucherBatch = voucherBatch;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "voucher")
    public List<VoucherUse> getVoucherUseList() {
        return voucherUseList;
    }

    public void setVoucherUseList(List<VoucherUse> voucherUseList) {
        this.voucherUseList = voucherUseList;
    }
}
