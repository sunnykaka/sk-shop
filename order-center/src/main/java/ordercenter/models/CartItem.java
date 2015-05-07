package ordercenter.models;

import common.models.utils.EntityClass;
import common.utils.Money;
import productcenter.models.StockKeepingUnit;

import javax.persistence.*;

/**
 * 购买条目，一个sku一个对应的物理商品
 * 表示一个购物项，如果没有cartId，则可能是直接购买
 * User: lidujun
 * Date: 2015-04-28
 */
@Table(name = "CartItem")
@Entity
public class CartItem implements EntityClass<Integer> {

    /**
     * 主键
     */
    private Integer id;

    /**
     * 购物车id
     */
    private int cartId;

    /**
     * 商品skuId
     */
    protected int skuId;

    /**
     * 购买数量
     */
    protected int number;

    /**
     * 是否被选中
     */
    private boolean selected = false;

    /**
     * 是否被删除
     */
    private boolean isDelete = false;


    /**
     * 产品名称，展示时使用
     */
    private String productName;

    /**
     * 购买价格，展示时使用
     */
    private Money curUnitPrice;

    /**
     * 单品sku
     */
    private StockKeepingUnit sku;

    @Transient
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    @Transient
    public Money getCurUnitPrice() {
        return curUnitPrice;
    }


    public void setCurUnitPrice(Money curUnitPrice) {
        this.curUnitPrice = curUnitPrice;
    }

    @Transient
    public StockKeepingUnit getSku() {
        return sku;
    }

    public void setSku(StockKeepingUnit sku) {
        this.sku = sku;
    }

    /**
     * 获取购物车项总价
     * @return
     */
    @Transient
    public Money getBuyTotalMoney() {
        return curUnitPrice.multiply(number);
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "id=" + id +
                ", cartId=" + cartId +
                ", selected=" + selected +
                ", isDelete=" + isDelete +
                '}';
    }

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
    public int getSkuId() {
        return skuId;
    }

    public void setSkuId(int skuId) {
        this.skuId = skuId;
    }

    @Column(name = "number")
    @Basic
    public int getNumber() {
        return number;
    }

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

}
