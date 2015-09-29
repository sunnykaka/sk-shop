package ordercenter.services;

import common.services.GeneralDao;
import common.utils.DateUtils;
import common.utils.Money;
import common.utils.page.Page;
import ordercenter.models.Cart;
import ordercenter.models.CartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import play.Logger;
import productcenter.constants.SaleStatus;
import productcenter.models.Product;
import productcenter.models.ProductPicture;
import productcenter.models.SkuStorage;
import productcenter.models.StockKeepingUnit;
import productcenter.services.ProductPictureService;
import productcenter.services.ProductService;
import productcenter.services.SkuAndStorageService;
import usercenter.models.Designer;
import usercenter.services.DesignerService;

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
    private GeneralDao generalDao;

    @Autowired
    private SkuAndStorageService skuService;

    @Autowired
    private ProductService productService;

    @Autowired
    private SkuAndStorageService skuAndStorageService;

    @Autowired
    private ProductPictureService pictureService;

    @Autowired
    private DesignerService designerService;

    //@Autowired
    //private CmsService cmsService;






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
        cart.setCreateDate(DateUtils.current());
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
     * 更新购物车项
     * @param cartItemList
     */
    public void updateCartItemList(List<CartItem> cartItemList) {
        for(CartItem cartItem : cartItemList) {
            updateCartItem(cartItem);
        }
    }

    /**
     * 通过购物车主键id获取购物车项，不包括已经删除的购物项
     * @param cartId
     * @return
     */
    @Transactional(readOnly = true)
    public List<CartItem> queryCarItemsByCartId(int cartId) {
        play.Logger.info("--------CartService queryAllProducts begin exe-----------");

        String jpql = "select o from CartItem o where 1=1 and o.isDelete=:isDelete and o.cartId=:cartId";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("isDelete", false);
        queryParams.put("cartId", cartId);
        return generalDao.query(jpql, Optional.<Page<CartItem>>empty(), queryParams);
    }

    /**
     * 通过购物车主键id获取购物车项，不包括已经删除的购物项
     * @param cartId
     * @return
     */
    @Transactional(readOnly = true)
    public List<CartItem> queryUserSelCarItemsByCartId(int cartId) {
        play.Logger.info("--------CartService queryAllProducts begin exe-----------");

        String jpql = "select o from CartItem o where 1=1 and o.isDelete=:isDelete and selected=:selected and o.cartId=:cartId";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("isDelete", false);
        queryParams.put("selected", true);
        queryParams.put("cartId", cartId);
        return generalDao.query(jpql, Optional.<Page<CartItem>>empty(), queryParams);
    }

    /**
     * 通过购物车主键id，和选中的购物车项id获取购物车项，不包括已经删除的购物项
     * @param cartId
     * @return
     */
    @Transactional(readOnly = true)
    public List<CartItem> queryUserSelCarItemsByIds(int cartId, String cartItems) {
        play.Logger.info("--------CartService queryUserSelCarItemsByIds begin exe-----------");

        String jpql = "select o from CartItem o where 1=1 and o.id in(" + cartItems + ") and o.cartId=:cartId";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("cartId", cartId);
        return generalDao.query(jpql, Optional.<Page<CartItem>>empty(), queryParams);
    }

    /**
     * 通过cartId来删除购物车项，只删除支付时选中的购物车项(假删除)
     * @param cartId
     */
    public void deleteSelectCartItemBySelIds(int cartId, String selItems) {
        play.Logger.info("--------CartService deleteSelectCartItemBySelIds begin exe-----------" + cartId + " : " + selItems);

        String jpql = "update CartItem v set v.isDelete=:isDelete where v.id in(" + selItems + ") and cartId=:cartId ";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("isDelete", true);
        queryParams.put("cartId", cartId);
        generalDao.update(jpql, queryParams);
    }

    /**
     * 通过skuId和cartId组合来删除购物车项
     * @param cartItemId
     */
    public void deleteCartItemById(int cartItemId) {
        String jpql = "update CartItem v set v.isDelete=:isDelete where v.id=:cartItemId ";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("isDelete", true);
        queryParams.put("cartItemId", cartItemId);
        generalDao.update(jpql, queryParams);
    }




















////////////////////////////把CartProcess类拷贝到CartService/////////////////////////////////////////////////////


    /**
     * 将Cart对象构建完整，包含整个购物车界面展示需要的东东。
     * 需要界面展示时使用
     * @param userId
     * @return
     */
    @Transactional
    public Cart buildUserCart(int userId) {
        Cart cart = this.getCartByUserId(userId);
        if (cart != null) {
            List<CartItem> cartItems = this.queryCarItemsByCartId(cart.getId());
            cart.setCartItemList(cartItems);
            //合计价格
            Money totalMoney = Money.valueOf(0);
            if(cartItems != null && cartItems.size() > 0) {
                for (CartItem cartItem : cartItems) {
                    totalMoney = totalMoney.add(this.setCartItemValues(cartItem,true));
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
    @Transactional
    public Cart buildUserCartBySelItem(int userId, String selCartItems) {
        Cart cart = this.getCartByUserId(userId);
        if (cart != null) {
            List<CartItem> cartItems = this.queryUserSelCarItemsByIds(cart.getId(), selCartItems);
            cart.setCartItemList(cartItems);
            //合计价格
            Money totalMoney = Money.valueOf(0);
            if(cartItems != null && cartItems.size() > 0) {
                for (CartItem cartItem : cartItems) {
                    totalMoney = totalMoney.add(this.setCartItemValues(cartItem));
                }
            }
            cart.setTotalMoney(totalMoney);
        }
        return cart;
    }

    /**
     * 设置订单项的各个需要的属性值
     * @param cartItem
     */
    @Transactional
    public Money setCartItemValues(CartItem cartItem) {
        return setCartItemValues(cartItem, false);
    }

    /**
     * 设置订单项的各个需要的属性值
     * @param cartItem
     * @param isForShowCart 是否是购物车展示界面
     */
    @Transactional
    public Money setCartItemValues(CartItem cartItem, boolean isForShowCart) {
        Money totalMoney = Money.valueOf(0);

        cartItem.setCurUnitPrice(Money.valueOf(0));
        cartItem.setOnline(false);
        cartItem.setHasStock(false);
        cartItem.setStockQuantity(0);
        cartItem.setTradeMaxNumber(0);
        cartItem.setCustomerName("");

        StockKeepingUnit stockKeepingUnit = skuService.getStockKeepingUnitById(cartItem.getSkuId());
        if (stockKeepingUnit != null) {
            cartItem.setSku(stockKeepingUnit);
            cartItem.setBarCode(stockKeepingUnit.getBarCode());

            Product product = productService.getProductById(stockKeepingUnit.getProductId());
            if(product != null) {
                cartItem.setProductId(product.getId());
                cartItem.setProductName(product.getName());
                cartItem.setCategoryId(product.getCategoryId());
                cartItem.setCustomerId(product.getCustomerId());
                Designer designer = product.getCustomer();
                if(designer != null) {
                    cartItem.setCustomerName(designer.getName());
                }
            }

            //图片
            ProductPicture picture = pictureService.getMinorProductPictureByProductId(stockKeepingUnit.getProductId());
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
                cartItem.setStorageId(skuStorage.getId());
            }

            //sku所属商品是否下架或被移除
            if (stockKeepingUnit.canBuy()) {
                if (product != null && (!product.getIsDelete()) && product.isOnline()) {
                    cartItem.setOnline(true);
                }
            }

            //根据判断是否是首发，当前价格要现算(这个地方需要改一下) ldj
            //boolean isFirstPublish = false;//cmsService.onFirstPublish(cartItem.getProductId());
            boolean isFirstPublish = false;
            if(product != null && product.getSaleStatus() != null) {
                isFirstPublish = product.getSaleStatus().equalsIgnoreCase(SaleStatus.FIRSTSELL.toString());
            }
            if(isFirstPublish) {
                cartItem.setCurUnitPrice(stockKeepingUnit.getPrice());
            } else {
                cartItem.setCurUnitPrice(stockKeepingUnit.getMarketPrice());
            }

            Money itemTotalMoney = cartItem.getCurUnitPrice().multiply(cartItem.getNumber());
            if(isForShowCart) {
                if(!cartItem.isOnline() || !cartItem.isHasStock()) {
                    itemTotalMoney = Money.valueOf(0);
                }
            }

            cartItem.setTotalPrice(itemTotalMoney);

            totalMoney = totalMoney.add(itemTotalMoney);

        } else {
            try {
                Logger.warn("构建购物车时发现sku被删除:" + +cartItem.getSkuId() + " : " + cartItem.getCartId());
                this.deleteCartItemById(cartItem.getId());
            } catch (Exception e) {
                Logger.warn("构建购物车删除sku失败:" + cartItem.getSkuId() + " : " + cartItem.getCartId());
            }
        }
        return totalMoney;
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