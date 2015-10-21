package api.response.shop;

import common.utils.Money;
import ordercenter.models.Cart;
import ordercenter.models.CartItem;

import java.util.List;
import java.util.stream.Collectors;

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
     * 购物车的总价，在加载车的时候动态计算，不需要入库
     */
    private Money totalMoney;

    /**
     * 商品条目
     */
    private List<CartItemDto> cartItemList;

    public static CartDto buildUserCart(Cart cart) {
        if(cart == null)  {
            return  null;
        }

        CartDto cartDto = new CartDto();
        cartDto.setId(cart.getId());
        cartDto.setTotalMoney(cart.getTotalMoney());

        List<CartItem> cartItemList = cart.getNotDeleteCartItemList();
        if(!cartItemList.isEmpty()) {
            List<CartItemDto> cartItemDtoList = cartItemList.stream().
                    map(CartItemDto::buildCartItemDto).collect(Collectors.toList());
            cartDto.setCartItemList(cartItemDtoList);
        }
        return cartDto;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
