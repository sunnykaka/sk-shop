package api.response.order;

import common.utils.Money;
import ordercenter.constants.VoucherStatus;
import ordercenter.constants.VoucherType;
import ordercenter.models.Voucher;
import org.joda.time.DateTime;

/**
 * User: liubin
 * Date: 14-11-5
 */
public class VoucherDto{

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

    public static VoucherDto build(Voucher voucher) {
        if (null == voucher) {
            return null;
        }

        VoucherDto voucherDto = new VoucherDto();
        voucherDto.setAmount(voucher.getAmount());
        voucherDto.setCreateTime(voucher.getCreateTime());
        voucherDto.setDeadline(voucher.getDeadline());
        voucherDto.setId(voucher.getId());
        voucherDto.setMinOrderAmount(voucher.getMinOrderAmount());
        voucherDto.setStatus(voucher.getStatus());
        voucherDto.setType(voucher.getType());
        voucherDto.setUniqueNo(voucher.getUniqueNo());
        voucherDto.setUseTime(voucher.getUseTime());

        return voucherDto;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public VoucherStatus getStatus() {
        return status;
    }

    public void setStatus(VoucherStatus status) {
        this.status = status;
    }

    public VoucherType getType() {
        return type;
    }

    public void setType(VoucherType type) {
        this.type = type;
    }

    public Money getAmount() {
        return amount;
    }

    public void setAmount(Money amount) {
        this.amount = amount;
    }

    public String getUniqueNo() {
        return uniqueNo;
    }

    public void setUniqueNo(String uniqueNo) {
        this.uniqueNo = uniqueNo;
    }

    public DateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(DateTime deadline) {
        this.deadline = deadline;
    }

    public Money getMinOrderAmount() {
        return minOrderAmount;
    }

    public void setMinOrderAmount(Money minOrderAmount) {
        this.minOrderAmount = minOrderAmount;
    }

    public DateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(DateTime createTime) {
        this.createTime = createTime;
    }

    public DateTime getUseTime() {
        return useTime;
    }

    public void setUseTime(DateTime useTime) {
        this.useTime = useTime;
    }
}
