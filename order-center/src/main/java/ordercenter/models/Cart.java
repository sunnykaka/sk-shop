package ordercenter.models;

import common.models.utils.EntityClass;
import common.models.utils.OperableData;
import common.utils.Money;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.util.List;

/**
 * 购物车对象
 * 车中是SKU对象，比如红色XL,绿色XLL，对应不同的购物记录
 * 购物车可以在用户没有登录的时候创建，跟踪这个购物车使用客户端的cookie，当用户登陆之后会合并到用户的ID进行跟踪
 * User: lidujun
 * Date: 2015-04-28
 */
@Table(name = "Cart")
@Entity
public class Cart implements EntityClass<Integer>, OperableData {

    /**
     * 主键
     */
    private Integer id;

    /**
     * 谁的购物车
     */
    private Integer userId;

    /**
     * 跟踪，比如用cookie
     */
    private String trackId = "";



    //新加的
    /**
     * 创建时间
     */
    private DateTime createTime;

    /**
     * 更新时间
     */
    private DateTime updateTime;

    /**
     * 最后操作人
     */
    private Integer operatorId;










    //创建时间，配合客户端的cookie过期时间可以用于清除无用数据
    //private Date createDate = new Date(); //ldj

    /**
     * 购物车的总价，在加载车的时候动态计算，不需要入库
     */
    private Money totalMoney;

    /**
     * 商品条目
     */
    private List<CartItem> cartItemList;

    @Transient
    public Money getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(Money totalMoney) {
        this.totalMoney = totalMoney;
    }

    @Transient
    public List<CartItem> getCartItemList() {
        return cartItemList;
    }

    public void setCartItemList(List<CartItem> cartItemList) {
        this.cartItemList = cartItemList;
    }

















    public Cart() {
    }

    public Cart(int userId) {
        this.userId = userId;
    }

    public Cart(String trackId) {
        this.trackId = trackId;
    }


    @Override
    public String toString() {
        return "Cart{" +
                "id=" + id +
                ", userId=" + userId +
                ", trackId='" + trackId + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", operatorId=" + operatorId +
                ", totalMoney=" + totalMoney +
                ", cartItemList=" + cartItemList +
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

    @Column(name = "userId")
    @Basic
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Column(name = "trackId")
    @Basic
    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
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

    @Column(name = "operatorId")
    @Basic
    @Override
    public Integer getOperatorId() {
        return operatorId;
    }

    @Override
    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }


/*
先注释掉

    public List<OrderItem> convertToOrderItemList() {
        if (cartItemList != null) {
            List<OrderItem> orderItemList = new ArrayList<OrderItem>(cartItemList.size());
            for (CartItem cartItem : cartItemList) {
                OrderItem orderItem = new OrderItem();
                orderItem.setSkuId(cartItem.getSkuId());
                orderItem.setNumber(cartItem.getNumber());
                orderItemList.add(orderItem);
            }
            return orderItemList;
        } else {
            return Collections.EMPTY_LIST;
        }
    }
*/

}
