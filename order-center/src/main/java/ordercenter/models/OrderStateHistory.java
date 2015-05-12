package ordercenter.models;

import common.models.utils.EntityClass;
import common.utils.DateUtils;
import ordercenter.constants.OrderState;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;

/**
 * 状态历史
 * User: lidujun
 * Date: 2015-05-08
 */
@Table(name = "OrderStateHistory")
@Entity
public class OrderStateHistory implements EntityClass<Integer> {

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 关联订单
     */
    private int orderId;

    /**
     * 订单状态
     */
    private OrderState orderState;

    /**
     * 该状态的时间
     */
    private DateTime date;

    /**
     * 操作者
     */
    private String operator;

    /**
     * 做了何事?
     */
    private String doWhat;

    /**
     * 备注
     */
    private String remark;


    public OrderStateHistory() {

    }

    /**
     * 根据 订单 及 具体操作 创建历史记录
     */
    public OrderStateHistory(Order order) {
        this(order, order.getUserName(), order.getOrderState().getValue(), "");
    }

    /**
     * 根据 订单 及 具体操作 创建历史记录
     */
    public OrderStateHistory(Order order, String doWhat) {
        this(order, order.getUserName(), doWhat, "");
    }

    /**
     * 根据 订单、具体操作 及 备注 创建历史记录
     */
    public OrderStateHistory(Order order, String doWhat, String remark) {
        this(order, order.getUserName(), doWhat, remark);
    }

    /**
     * 根据 订单、操作者、具体操作 及 备注 创建历史记录
     */
    public OrderStateHistory(Order order, String operator, String doWhat, String remark) {
        this.orderState = order.getOrderState();
        this.date = DateUtils.current();
        this.orderId = order.getId();
        this.operator = operator;
        this.doWhat = doWhat;
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "OrderStateHistory{" +
                "id=" + id +
                ", orderId=" + orderId +
                ", orderState=" + orderState +
                ", date=" + date +
                ", operator='" + operator + '\'' +
                ", doWhat='" + doWhat + '\'' +
                ", remark='" + remark + '\'' +
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

    @Column(name = "orderId")
    @Basic
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    @Column(name = "orderState")
    @Enumerated(EnumType.STRING)
    public OrderState getOrderState() {
        return orderState;
    }

    public void setOrderState(OrderState orderState) {
        this.orderState = orderState;
    }

    @Column(name = "date")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    @Column(name = "operator")
    @Basic
    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    @Column(name = "doWhat")
    @Basic
    public String getDoWhat() {
        return doWhat;
    }

    public void setDoWhat(String doWhat) {
        this.doWhat = doWhat;
    }

    @Column(name = "remark")
    @Basic
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
