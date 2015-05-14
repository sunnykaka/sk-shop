package controllers.shop;

import common.utils.Money;
import ordercenter.models.Cart;
import ordercenter.models.CartItem;
import ordercenter.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import play.Logger;
import productcenter.models.*;
import productcenter.services.ProductPictureService;
import productcenter.services.ProductService;
import productcenter.services.PropertyAndValueService;
import productcenter.services.SkuAndStorageService;
import services.CmsService;

import java.util.List;

/**
 * 购物车通用处理方法类
 * User: lidujun
 * Date: 2015-05-06
 */
@Service
@Transactional(readOnly = true)
public class CartProcess {

    @Autowired
    private CartService cartService;

    @Autowired
    private SkuAndStorageService skuAndStorageService;

    @Autowired
    private SkuAndStorageService skuService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductPictureService pictureService;

    @Autowired
    private PropertyAndValueService propertyAndValueService;

    @Autowired
    private CmsService cmsService;

    /**
     * 将Cart对象构建完整，包含整个购物车界面展示需要的东东。
     * 需要界面展示时使用
     * @param userId
     * @return
     */
    public Cart buildUserCart(int userId) {
        Cart cart = cartService.getCartByUserId(userId);
        if (cart != null) {
            List<CartItem> cartItems = cartService.queryCarItemsByCartId(cart.getId());
            cart.setCartItemList(cartItems);
            //合计价格
            Money totalMoney = Money.valueOf(0);
            if(cartItems != null && cartItems.size() > 0) {
                for (CartItem cartItem : cartItems) {
                    this.setCartItemValues(cartItem,totalMoney);
                }
            }
            cart.setTotalMoney(totalMoney);
        }
        return cart;
    }

    /**
     * 将Cart对象构建完整，仅只包含用户选中的购物车项
     * 需要界面展示时使用
     * @param userId
     * @return
     */
    public Cart buildUserCartOnlySelItem(int userId) {
        Cart cart = cartService.getCartByUserId(userId);
        if (cart != null) {
            List<CartItem> cartItems = cartService.queryUserSelCarItemsByCartId(cart.getId());
            cart.setCartItemList(cartItems);
            //合计价格
            Money totalMoney = Money.valueOf(0);
            if(cartItems != null && cartItems.size() > 0) {
                for (CartItem cartItem : cartItems) {
                    this.setCartItemValues(cartItem,totalMoney);
                }
            }
            cart.setTotalMoney(totalMoney);
        }
        return cart;
    }

    /**
     * 设置订单项的各个需要的属性值
     * @param cartItem
     * @param totalMoney
     */
    @Transactional
    public void setCartItemValues(CartItem cartItem, Money totalMoney) {
        StockKeepingUnit stockKeepingUnit = skuService.getStockKeepingUnitById(cartItem.getSkuId());
        cartItem.setCurUnitPrice(Money.valueOf(0));
        cartItem.setOnline(false);
        cartItem.setHasStock(false);
        cartItem.setStockQuantity(0);
        cartItem.setTradeMaxNumber(0);
        if (stockKeepingUnit != null) {
            cartItem.setSku(stockKeepingUnit);
            Product product = productService.getProductById(stockKeepingUnit.getProductId());
            cartItem.setProductId(product.getId());
            cartItem.setProductName(product.getName());
            //图片
            ProductPicture picture = pictureService.getMainProductPictureByProductIdSKuId(stockKeepingUnit.getProductId(), stockKeepingUnit.getId());
            cartItem.setMainPicture(picture.getPictureUrl());

            //设置库存信息
            SkuStorage skuStorage = skuAndStorageService.getSkuStorage(stockKeepingUnit.getId());
            if(skuStorage != null) {
                int maxStockNum = skuStorage.getStockQuantity();
                cartItem.setStockQuantity(maxStockNum);
                if (maxStockNum > 0) {
                    cartItem.setHasStock(true);
                }
                cartItem.setTradeMaxNumber(skuStorage.getTradeMaxNumber());
            }

            //sku所属商品是否下架或被移除
            if (stockKeepingUnit.canBuy()) {
                if ((!product.getIsDelete()) && product.isOnline()) {
                    cartItem.setOnline(true);
                }
            }

            //根据判断是否是首发，当前价格要现算
            boolean isFirstPublish = cmsService.onFirstPublish(cartItem.getProductId());
            if(isFirstPublish) {
                cartItem.setCurUnitPrice(stockKeepingUnit.getMarketPrice());
            } else {
                cartItem.setCurUnitPrice(stockKeepingUnit.getPrice());
            }

            Money itemTotalMoney = cartItem.getCurUnitPrice().multiply(cartItem.getNumber());
            cartItem.setTotalPrice(itemTotalMoney);

            totalMoney = totalMoney.add(itemTotalMoney);

            List<SkuProperty> skuPropertyList = stockKeepingUnit.getSkuProperties();
            if(skuPropertyList != null && skuPropertyList.size() > 0) {
                for(SkuProperty p : skuPropertyList) {
                    Property property = propertyAndValueService.getPropertyById(p.getPropertyId());
                    p.setPropertyName(property.getName());
                    Value value = propertyAndValueService.getValueById(p.getValueId());
                    p.setPropertyValue(value.getValueName());
                }
            }
        } else {
            try {
                Logger.warn("构建购物车时发现sku被删除:" + + cartItem.getSkuId() + " : " + cartItem.getCartId());
                cartService.deleteCartItemBySkuIdAndCartId(cartItem.getSkuId(), cartItem.getCartId());
            } catch (Exception e) {
                Logger.warn("构建购物车删除sku失败:" + cartItem.getSkuId() + " : " + cartItem.getCartId());
            }
        }
    }

    /**
     * 按照支付订单号列表重新计算支付总金额
     * @param cartItemList
     * @return
     */
    public Money calculateTotalMoney(List<CartItem> cartItemList) {
        Money totalMoney = Money.valueOf(0);
        for(CartItem cartItem : cartItemList) {
            totalMoney = totalMoney.add(cartItem.getCurUnitPrice().multiply(cartItem.getNumber()));
        }
        return totalMoney;
    }

}