package ordercenter.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import common.models.utils.EntityClass;
import common.utils.Money;

import javax.persistence.*;

/**
 * 购买条目，一个sku一个对应的物理商品
 * 表示一个购物项，如果没有cartId，则可能是直接购买
 * User: lidujun
 * Date: 2015-04-28
 */
@Table(name = "CartItem")
@Entity
public class CartItem extends TradeItem implements EntityClass<Integer> {

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 购物车id
     */
    private int cartId;

    @JsonIgnore
    private Cart cart;

    /**
     * 是否被选中
     */
    private boolean selected = false;

    /**
     * 是否被删除
     */
    private boolean isDelete = false;

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @Column(name = "cartItemId")
    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "cartId")
    @Basic
    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    @Column(name = "skuId")
    @Basic
    @Override
    public int getSkuId() {
        return skuId;
    }

    @Override
    public void setSkuId(int skuId) {
        this.skuId = skuId;
    }

    @Column(name = "number")
    @Basic
    @Override
    public int getNumber() {
        return number;
    }

    @Override
    public void setNumber(int number) {
        this.number = number;
    }

    @Column(name = "selected")
    @Basic
    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Column(name = "isDelete")
    @Basic
    public Boolean getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Boolean isDelete) {
        this.isDelete = isDelete;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cartId", insertable = false, updatable = false)
    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    /**
     * 计算购物车项金额合计
     * @return
     */
    public Money calTotalPrice() {
        Money totalPrice = getCurUnitPrice().multiply(getNumber());
        setTotalPrice(totalPrice);
        return totalPrice;
    }

}
