package ordercenter.models;

import common.models.utils.EntityClass;
import common.models.utils.OperableData;
import common.utils.Money;
import ordercenter.constants.VoucherStatus;
import ordercenter.constants.VoucherType;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import usercenter.models.User;

import javax.persistence.*;

/**
 * User: liubin
 * Date: 14-11-5
 */
@Table(name = "voucher_use")
@Entity
public class VoucherUse implements EntityClass<Integer>, OperableData {

    private Integer id;

    /**
     * 创建时间
     */
    private DateTime createTime;

    private Integer orderId;

    private Order order;

    private Integer voucherId;

    private Voucher voucher;


    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    @Column(name = "orderId")
    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderId", insertable = false, updatable = false)
    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    @Column(name = "voucherId")
    public Integer getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(Integer voucherId) {
        this.voucherId = voucherId;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucherId", insertable = false, updatable = false)
    public Voucher getVoucher() {
        return voucher;
    }

    public void setVoucher(Voucher voucher) {
        this.voucher = voucher;
    }
}
