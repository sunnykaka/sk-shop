package ordercenter.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import common.models.utils.EntityClass;
import common.utils.Money;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    @JsonIgnore
    private List<CartItem> cartItemList = new ArrayList<>(0);


    @Transient
    public Money getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(Money totalMoney) {
        this.totalMoney = totalMoney;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "cart")
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

    @Column(name = "createDate")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(DateTime createDate) {
        this.createDate = createDate;
    }

    public int calcTotalNum() {
        int totalNum = 0;
        for(CartItem cartItem : getValidCartItemList()) {
            totalNum += cartItem.getNumber();
        }
        return totalNum;
    }

    /**
     * 得到有效的(未删除)的购物车项
     * @return
     */
    @Transient
    public List<CartItem> getValidCartItemList() {
        return cartItemList.stream().filter(x -> !x.getIsDelete()).collect(Collectors.toList());
    }

}
