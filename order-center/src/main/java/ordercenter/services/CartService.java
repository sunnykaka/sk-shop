package ordercenter.services;

import common.services.GeneralDao;
import common.utils.page.Page;
import ordercenter.models.Cart;
import ordercenter.models.CartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import play.Logger;
import productcenter.models.*;
import productcenter.services.ProductService;
import productcenter.services.PropertyAndValueService;
import productcenter.services.SkuAndStorageService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 购物Service
 * User: lidujun
 * Date: 2015-04-29
 */
@Service
@Transactional
public class CartService {

    @Autowired
    GeneralDao generalDao;

    @Autowired
    private SkuAndStorageService skuService;

    @Autowired
    private ProductService productService;

    @Autowired
    private PropertyAndValueService propertyAndValueService;

    //@Autowired
    //private CmsService skCmsService;

    /**
     * 向购物车中加入Sku商品
     *
     * @param skuId
     * @param cart
     *
     */
    public void addSkuToCart(Cart cart, int skuId, int number, boolean isReplace) {
        List<CartItem> cartItemList = cart.getCartItemList();
        boolean hasAdded = false;
        //判断购物车中是否加入过这个商品，如果加过则只改数量
        for (CartItem cartItem : cartItemList) {
            if (cartItem.getSkuId() == skuId) {
                if(isReplace) {
                    cartItem.setNumber(number);
                } else {
                    cartItem.setNumber(cartItem.getNumber() + number);
                }
                this.updateCartItem(cartItem);
                hasAdded = true;
                break;
            }
        }
        if (!hasAdded) {
            CartItem cartItem = new CartItem();
            cartItem.setSkuId(skuId);
            cartItem.setCartId(cart.getId());
            cartItem.setNumber(number);
            this.createCartItem(cartItem);
        }
    }

    /**
     * 通过跟踪号id初始化购物车和购物车项
     * @param trackId 预留 跟踪id，现在登陆才可以加入购物车，暂时用不上
     * @param skuId
     * @param number
     */
    public void initCartByTrackId(String trackId, int skuId, int number) {
        Cart cart = new Cart(trackId);
        this.createCart(cart);
        CartItem cartItem = new CartItem();
        cartItem.setSkuId(skuId);
        cartItem.setCartId(cart.getId());
        cartItem.setNumber(number);
        this.createCartItem(cartItem);
    }

    /**
     * 通过用户id初始化购物车和购物车项
     * @param userId
     * @param skuId
     * @param number
     */
    public void initCartByUserId(int userId, int skuId, int number) {
        Cart cart = new Cart(userId);
        this.createCart(cart);
        CartItem cartItem = new CartItem();
        cartItem.setSkuId(skuId);
        cartItem.setCartId(cart.getId());
        cartItem.setNumber(number);
        this.createCartItem(cartItem);
    }

    /**
     * 创建购物车
     * @param cart
     */
    public void createCart(Cart cart) {
        play.Logger.info("--------CartService createCart begin exe-----------" + cart);
        generalDao.persist(cart);
    }

    /**
     * 更新购物车内容
     * @param cart
     */
    public void updateCart(Cart cart) {
        play.Logger.info("--------CartService updateCart begin exe-----------" + cart);
        generalDao.merge(cart);
    }

    /**
     * 通过购物车id删除购物车
     * @param cartId
     */
    public void deleteCart(int cartId) {
        play.Logger.info("--------CartService deleteCart real delete  begin exe-----------" + cartId);
        generalDao.removeById(Cart.class, cartId);
    }

    /**
     * 通过购物车id获取购物车
     * @param cartId
     * @return
     */
    @Transactional(readOnly = true)
    public Cart getCart(int cartId) {
        play.Logger.info("--------CartService getCart begin exe-----------" + cartId);
        Cart cart = generalDao.get(Cart.class, cartId);
        return cart;
    }

    @Transactional(readOnly = true)
    private String getSelectAllCartSql() {
        return "select v from Cart v where 1=1 ";
    }

    /**
     * 通过cookie等跟踪id获取购物车
     * @param trackId
     * @return
     */
    @Transactional(readOnly = true)
    public Cart getCartByTrackId(String trackId) {
        String jpql = getSelectAllCartSql();
        Map<String, Object> queryParams = new HashMap<>();
        jpql += " and v.trackId = :trackId ";
        queryParams.put("trackId", trackId);

        Cart cart = null;
        List<Cart> itemList = generalDao.query(jpql, Optional.ofNullable(null), queryParams);
        if(itemList != null && itemList.size() > 0) {
            cart = itemList.get(0);
        }
        return cart;
    }

    /**
     * 通过用户id获取用户购物车
     * @param userId
     * @return
     */
    @Transactional
    public Cart getCartByUserId(int userId) {
        String jpql = getSelectAllCartSql();
        Map<String, Object> queryParams = new HashMap<>();
        jpql += " and v.userId = :userId ";
        queryParams.put("userId", userId);

        Cart cart = null;
        List<Cart> itemList = generalDao.query(jpql, Optional.ofNullable(null), queryParams);
        if(itemList != null && itemList.size() > 0) {
            cart = itemList.get(0);
        }
        return cart;
    }



    //////////////////////////////购物车项////////////////////////////////////////////////
    /**
     * 创建购物车项
     * @param cartItem
     */
    public void createCartItem(CartItem cartItem) {
        generalDao.persist(cartItem);
    }

    /**
     * 更新购物车项
     * @param cartItem
     */
    public void updateCartItem(CartItem cartItem) {
        generalDao.merge(cartItem);
    }

    /**
     * 通过购物车主键id获取购物车项，不包括已经删除的购物项
     * @param cartId
     * @return
     */
    @Transactional(readOnly = true)
    public List<CartItem> queryCarItemsByCartId(int cartId) {
        play.Logger.info("--------ProductService queryAllProducts begin exe-----------");

        String jpql = "select o from CartItem o where 1=1 and o.isDelete=:isDelete and o.cartId=:cartId";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("isDelete", false);
        queryParams.put("cartId", cartId);
        return generalDao.query(jpql, Optional.<Page<CartItem>>empty(), queryParams);
    }

    /**
     * 通过cartId来删除购物车项
     * @param cartId
     */
    public void deleteCartItemByCartId(int cartId) {
        String jpql = "delete from CartItem v where cartId=:cartId ";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("cartId", cartId);
        generalDao.update(jpql,queryParams);
    }

    /**
     * 通过skuId和cartId组合来删除购物车项
     * @param skuId
     * @param cartId
     */
    public void deleteCartItemBySkuIdAndCartId(int skuId, int cartId) {
        String jpql = "delete from CartItem v where skuId=:skuId and cartId=:cartId ";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("skuId", skuId);
        queryParams.put("cartId", cartId);
        generalDao.update(jpql, queryParams);
    }

}
