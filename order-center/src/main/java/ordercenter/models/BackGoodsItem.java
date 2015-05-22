package ordercenter.models;

import common.models.utils.EntityClass;
import common.models.utils.OperableData;
import common.utils.Money;
import ordercenter.constants.OrderState;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;

@Table(name = "backgoodsitem")
@Entity
public class BackGoodsItem implements EntityClass<Integer>, OperableData {

    private Integer id;

    /**
     * 退货单编号
     */
    private Integer backGoodsId;

    /**
     * 退货数量
     */
    private Integer number;

    /**
     * 创建时间
     */
    private DateTime createTime;

    /**
     * 更新时间
     */
    private DateTime updateTime;

    /**
     * 订单详情编号
     */
    private Integer orderItemId;

    /**
     * 订单项退货时的单价
     */
    private Money unitPrice = Money.valueOf(0);;

    /**
     * 记录提交退款单时订单项对应的状态(方便取消退货单时状态还原), 目前已无用处! 因此以 Cancel 做为默认值.
     */
    private OrderState orderState = OrderState.Cancel;

    /**
     * 传递数据的载体, 不与数据库关联
     */
    private OrderItem orderItem;

//    /**
//     * 创建退货单时, 检查退货项与订单项的相关数据.<br/>
//     * 1. 退单项是否有对应的订单项;<br/>
//     * 2. 退货的数量是否有误;<br/>
//     * 3. 订单项是否退过货.
//     *
//     * @param backGoodsIdList  此订单所有已经退过货未取消的退货单编号
//     * @param orderService     查询订单项
//     * @param backGoodsService 查询退货单对应的订单项是否有退过货
//     *
//     */
//    public void checkSpecification(List<Integer> backGoodsIdList, OrderService orderService, BackGoodsService backGoodsService){
//        orderItem = orderService.getOrderItemById(orderItemId);
//        if (orderItem == null) {
//            throw new AppBusinessException("没有对应的订单项信息!");
//        }
//        if (number <= 0 || orderItem.getNumber() < number) {
//            throw new AppBusinessException("商品[" + orderItem.getProductName() + "]的退货数量有误!");
//        }
//        // 若允许一个 订单项 退多次, 则此处的检查需要更改.
//        for (Long backGoodsId : backGoodsIdList) {
//            if (backGoodsService.queryByBackOrderItemId(backGoodsId, orderItemId) != 0) {
//                throw new AppBusinessException("商品[" + orderItem.getProductName() + "]已经退过货!");
//            }
//        }
//
//        unitPrice = orderItem.getCurUnitPrice();
//        orderState = orderItem.getOrderState();
//    }

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

    @Column(name = "backGoodsId")
    @Basic
    public Integer getBackGoodsId() {
        return backGoodsId;
    }

    public void setBackGoodsId(Integer backGoodsId) {
        this.backGoodsId = backGoodsId;
    }

    @Column(name = "orderItemId")
    @Basic
    public Integer getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Integer orderItemId) {
        this.orderItemId = orderItemId;
    }

    @Column(name = "unitPrice")
    @Type(type="common.utils.hibernate.MoneyType")
    public Money getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Money unitPrice) {
        this.unitPrice = unitPrice;
    }

    @Column(name = "number")
    @Basic
    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    @Column(name = "orderState")
    @Enumerated(EnumType.STRING)
    public OrderState getOrderState() {
        return orderState;
    }

    public void setOrderState(OrderState orderState) {
        this.orderState = orderState;
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
    @Override
    public DateTime getUpdateTime() {
        return updateTime;
    }

    @Override
    public void setUpdateTime(DateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Transient
    public OrderItem getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }
}