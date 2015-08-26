package api.response.shop;

import common.utils.Money;
import org.joda.time.DateTime;

import java.util.List;

/**
 * 购物车对象
 * 车中是SKU对象，比如红色XL,绿色XLL，对应不同的购物记录
 * User: lidujun
 * Date: 2015-08-25
 */
public class CartDto {
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
    private List<CartItemDto> cartItemList;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public DateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(DateTime createDate) {
        this.createDate = createDate;
    }

    public Money getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(Money totalMoney) {
        this.totalMoney = totalMoney;
    }

    public List<CartItemDto> getCartItemList() {
        return cartItemList;
    }

    public void setCartItemList(List<CartItemDto> cartItemList) {
        this.cartItemList = cartItemList;
    }
}
