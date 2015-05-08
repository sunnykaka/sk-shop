package ordercenter.models;

import common.models.utils.EntityClass;
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
public class Cart implements EntityClass<Integer> {

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

    /**
     * 创建时间，配合客户端的cookie过期时间可以用于清除无用数据
     */
    private DateTime createDate;

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
                ", createDate=" + createDate +
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
    public DateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(DateTime createDate) {
        this.createDate = createDate;
    }

}
