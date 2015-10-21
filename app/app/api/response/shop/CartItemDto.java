package api.response.shop;

import ordercenter.models.CartItem;
import productcenter.models.StockKeepingUnit;

/**
 * Created by lidujun on 15-8-27.
 */
public class CartItemDto extends TradeItemDto{
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 是否被选中
     */
    private boolean selected = false;

    /**
     * 是否被删除
     */
    private boolean isDelete = false;

    public static CartItemDto buildCartItemDto(CartItem cartItem) {
        if(cartItem == null)  {
            return  null;
        }
        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setId(cartItem.getId());
        cartItemDto.setSelected(cartItem.isSelected());
        cartItemDto.setIsDelete(cartItem.getIsDelete());

        //tradeItem
        cartItemDto.setCustomerId(cartItem.getCustomerId());
        cartItemDto.setCustomerName(cartItem.getCustomerName());
        cartItemDto.setProductId(cartItem.getProductId());
        cartItemDto.setProductName(cartItem.getProductName());
        cartItemDto.setMainPicture(cartItem.getMainPicture());

        StockKeepingUnit sku = cartItem.getSku();
        SkuDto skuDto = new SkuDto();
        cartItemDto.setSku(skuDto.build(sku));

        cartItemDto.setCurUnitPrice(cartItem.getCurUnitPrice());
        cartItemDto.setNumber(cartItem.getNumber());
        cartItemDto.setTotalPrice(cartItem.getTotalPrice());
        cartItemDto.setStockQuantity(cartItem.getStockQuantity());
        cartItemDto.setTradeMaxNumber(cartItem.getTradeMaxNumber());
        cartItemDto.setHasStock(cartItem.isHasStock());
        cartItemDto.setOnline(cartItem.isOnline());

        return cartItemDto;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
