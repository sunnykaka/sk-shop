package api.response.shop;

import ordercenter.models.TradeItem;

/**
 * 购买条目，一个sku一个对应的物理商品
 * 表示一个购物项，如果没有cartId，则可能是直接购买
 * User: lidujun
 * Date: 2015-08-25
 */
public class CartItemDto extends TradeItem {

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 购物车id
     */
    private int cartId;

    /**
     * 是否被选中
     */
    private boolean selected = false;

    /**
     * 是否被删除
     */
    private boolean isDelete = false;


    /**
     * 产品id
     */
    protected int productId;


    /**
     * 产品名称，展示时使用
     */
    protected String productName;



    /**
     * 是否有库存，这个字段不保存，是在显示购物车的时候动态从数据库中查询的，这样可以实时查看是否有货
     */
    protected boolean hasStock;

    /**
     * 该商品是否上架：是否上架(0表示未上架, 1表示上架)
     */
    protected Boolean online;




    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setIsDelete(boolean isDelete) {
        this.isDelete = isDelete;
    }
}
