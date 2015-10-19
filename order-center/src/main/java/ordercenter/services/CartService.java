package ordercenter.services;

import com.google.common.collect.Lists;
import common.exceptions.ErrorCode;
import common.services.GeneralDao;
import common.utils.DateUtils;
import common.utils.Money;
import ordercenter.excepiton.CartException;
import ordercenter.models.Cart;
import ordercenter.models.CartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import play.Logger;
import productcenter.models.Product;
import productcenter.models.ProductPicture;
import productcenter.models.SkuStorage;
import productcenter.models.StockKeepingUnit;
import productcenter.services.ProductPictureService;
import productcenter.services.ProductService;
import productcenter.services.SkuAndStorageService;
import usercenter.models.Designer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 购物Service
 * User: lidujun
 * Date: 2015-04-29
 */
@Service
public class CartService {

    @Autowired
    private GeneralDao generalDao;

    @Autowired
    private SkuAndStorageService skuService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductPictureService pictureService;


    /**
     * 通过购物车id获取购物车
     *
     * @param cartId
     * @return
     */
    @Transactional(readOnly = true)
    public Cart getCart(int cartId) {
        return generalDao.get(Cart.class, cartId);
    }

    /**
     * 通过用户id获取用户购物车
     *
     * @param userId
     * @return
     */
    @Transactional(readOnly = true)
    public Cart getCartByUserId(int userId) {
        String jpql = "select c from Cart c where c.userId = :userId ";
        Map<String, Object> queryParams = new HashMap<>();

        queryParams.put("userId", userId);

        List<Cart> itemList = generalDao.query(jpql, Optional.empty(), queryParams);
        if (itemList.isEmpty()) {
            return null;
        } else {
            return itemList.get(0);
        }
    }


    /**
     * 通过cartId来删除购物车项，只删除支付时选中的购物车项(假删除)
     *
     * @param cartId
     */
    @Transactional
    public void deleteSelectCartItemBySelIds(int cartId, String selItems) {
        String jpql = "update CartItem v set v.isDelete=:isDelete where v.id in(" + selItems + ") and cartId=:cartId ";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("isDelete", true);
        queryParams.put("cartId", cartId);
        generalDao.update(jpql, queryParams);
    }

    /**
     * 通过skuId和cartId组合来删除购物车项
     *
     * @param cartItemId
     */
    @Transactional
    public void deleteCartItemById(int cartItemId) {
        CartItem cartItem = getCartItem(cartItemId);
        if(cartItem != null) {
            cartItem.setIsDelete(true);
            generalDao.persist(cartItem);
        }
    }

    /**
     * 通过id获取购物车项
     *
     * @param cartItemId
     * @return
     */
    @Transactional(readOnly = true)
    public CartItem getCartItem(Integer cartItemId) {
        return generalDao.get(CartItem.class, cartItemId);
    }


    /**
     * 将Cart对象构建完整，包含整个购物车界面展示需要的东东。
     * 需要界面展示时使用
     *
     * @param userId
     * @return
     */
    @Transactional
    public Cart buildUserCart(int userId) {
        Cart cart = this.getCartByUserId(userId);
        if (cart != null) {
            fillCartItemValues(cart);
        }
        return cart;
    }

    /**
     * 将Cart对象构建完整，仅只包含用户选中的购物车项
     * 需要界面展示时使用
     *
     * @param userId
     * @return
     */
    @Transactional
    public Cart buildUserCartBySelItem(int userId, List<Integer> selCartItemIdList) {
        Cart cart = this.getCartByUserId(userId);
        if (cart != null) {
            cart.filterSelectedCartItem(selCartItemIdList);
            fillCartItemValues(cart);
        }
        return cart;
    }


    /**
     * 设置订单项的各个需要的属性值
     *
     * @param cart
     */
    private void fillCartItemValues(Cart cart) {

        for(CartItem cartItem : cart.getNotDeleteCartItemList()) {

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
                if (product != null) {
                    cartItem.setProductId(product.getId());
                    cartItem.setProductName(product.getName());
                    cartItem.setCategoryId(product.getCategoryId());
                    cartItem.setCustomerId(product.getCustomerId());
                    Designer designer = product.getCustomer();
                    if (designer != null) {
                        cartItem.setCustomerName(designer.getName());
                    }
                }

                //图片
                ProductPicture picture = pictureService.getMinorProductPictureByProductId(stockKeepingUnit.getProductId());
                cartItem.setMainPicture(picture.getPictureUrl());

                //设置库存信息
                SkuStorage skuStorage = skuService.getSkuStorage(stockKeepingUnit.getId());
                if (skuStorage != null) {
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

                /**
                 * 获取当前售价，首发，预售，即将开售则采用首发价
                 * 其它的则采用市场价
                 */
                cartItem.setCurUnitPrice(skuService.getSkuCurrentPrice(stockKeepingUnit.getId()));

                cartItem.setTotalPrice(cartItem.calTotalPrice());

            } else {
                Logger.warn("构建购物车时发现sku被删除:" + +cartItem.getSkuId() + " : " + cartItem.getCartId());
                this.deleteCartItemById(cartItem.getId());
            }

        }

        cart.setTotalMoney(cart.calcTotalMoney());

    }


    /**
     * 添加sku到购物车
     * @param userId
     * @param skuId
     * @param number 购买数量
     * @param isReplace 如果为true， 代表用number替换购物车中sku的数量，否则为叠加
     * @return
     */
    @Transactional
    public int addSkuToCart(Integer userId, int skuId, int number, boolean isReplace) {

        Cart cart = getCartByUserId(userId);

        int skuBuyNumber = number;
        if(cart != null && !isReplace) {
            for (CartItem cartItem : cart.getNotDeleteCartItemList()) {
                if (cartItem.getSkuId() == skuId) {
                    skuBuyNumber = number + cartItem.getNumber();
                    break;
                }
            }
        }

        verifySkuToBuy(skuId, number, skuBuyNumber);

        if(cart == null) {
            cart = new Cart(userId);
            cart.setCreateDate(DateUtils.current());
            generalDao.persist(cart);

            CartItem cartItem = new CartItem();
            cartItem.setSkuId(skuId);
            cartItem.setCartId(cart.getId());
            cartItem.setNumber(skuBuyNumber);
            generalDao.persist(cartItem);

            cart.getCartItemList().add(cartItem);

        } else {

            boolean hasAdded = false;
            for (CartItem cartItem : cart.getNotDeleteCartItemList()) {
                if (cartItem.getSkuId() == skuId) {
                    cartItem.setNumber(skuBuyNumber);
                    generalDao.persist(cartItem);
                    hasAdded = true;
                    break;
                }
            }
            if (!hasAdded) {
                CartItem cartItem = new CartItem();
                cartItem.setSkuId(skuId);
                cartItem.setCartId(cart.getId());
                cartItem.setNumber(number);
                generalDao.persist(cartItem);

                cart.getCartItemList().add(cartItem);
            }
        }

        return cart.calcTotalNum();
    }

    /**
     * 选择购物车中的项
     * @param cart
     * @param selCartItems 选中的购物车ID, 多个ID以","分隔
     */
    @Transactional
    public void selectCartItems(Cart cart, String selCartItems) {

        if(selCartItems == null || selCartItems.trim().length() == 0) {
            throw new CartException(ErrorCode.Conflict, "请先选择购物车中的商品");
        }

        List<Integer> selCartItemIdList = Lists.newArrayList(selCartItems.split(",")).stream().
                map(Integer::parseInt).collect(Collectors.toList());

        verifyCart(cart, selCartItemIdList);

        for (CartItem cartItem : cart.getNotDeleteCartItemList()) {
            cartItem.setSelected(selCartItemIdList.contains(cartItem.getId()));
            generalDao.persist(cartItem);
        }
    }

    /**
     * 为直接购买构造一个购物车，实际在数据库不存在
     * @param skuId
     * @param number
     * @return
     */
    @Transactional
    public Cart fakeCartForPromptlyPay(int skuId, int number) {
        Cart cart = new Cart();
        CartItem cartItem = new CartItem();
        cartItem.setSkuId(skuId);
        cartItem.setNumber(number);
        cartItem.setSelected(true);
        cart.getCartItemList().add(cartItem);

        fillCartItemValues(cart);

        return cart;
    }

    /**
     * 校验添加的sku是否符合购买条件
     * @param skuId
     * @param number 添加的数量
     * @param skuBuyNumber 购物车内该sku总购买数量
     */
    @Transactional(readOnly = true)
    public void verifySkuToBuy(int skuId, int number, int skuBuyNumber) {

        if (skuId <= 0) {
            throw new CartException(ErrorCode.Conflict, "无法找到商品信息");
        }

        if (number < 1) {
            throw new CartException(ErrorCode.Conflict, "添加商品到购物车失败, 添加的数量必须大于或等于1");
        }

        if (!skuService.isSkuUsable(skuId)) {
            throw new CartException(ErrorCode.SkuNotAvailable);
        }

        SkuStorage skuStorage = skuService.getSkuStorage(skuId);
        int maxCanBuyNum = Math.min(skuStorage.getTradeMaxNumber(), skuStorage.getStockQuantity());

        if (skuBuyNumber > maxCanBuyNum) {
            CartException ex = new CartException(ErrorCode.Conflict,
                    String.format("超过最大购买商品数量,最多能够购买%d件", maxCanBuyNum));
            ex.setMaxCanBuyNum(maxCanBuyNum);
            throw ex;
        }
    }


    /**
     * 校验购物车内的商品是否符合购买条件
     * @param cart
     * @param selCartItemIdList
     * @throws CartException
     */
    @Transactional(readOnly = true)
    public void verifyCart(Cart cart, List<Integer> selCartItemIdList) {
        if (cart == null || cart.getNotDeleteCartItemList().isEmpty()) {
            throw new CartException(ErrorCode.Conflict, "您的购物车为空，请先选择商品");
        }

        if(selCartItemIdList == null || selCartItemIdList.isEmpty()) {
            throw new CartException(ErrorCode.Conflict, "请先选择购物车中的商品");
        }

        for (CartItem cartItem : cart.getNotDeleteCartItemList()) {
            if(selCartItemIdList.contains(cartItem.getId())) {
                if(!cartItem.isHasStock()) {
                    throw new CartException(ErrorCode.SkuNotAvailable, "商品" + cartItem.getProductName() + "已售罄");
                }
                if(!cartItem.isOnline()) {
                    throw new CartException(ErrorCode.SkuNotAvailable, "商品" + cartItem.getProductName() + "已下架");
                }
                if(cartItem.getNumber() > cartItem.getStockQuantity()) {
                    throw new CartException(ErrorCode.SkuNotAvailable,
                            String.format("商品%s购买数量超过了库存数，库存只有%d个",
                                    cartItem.getProductName(), cartItem.getStockQuantity()));
                }
                if(cartItem.getNumber() > cartItem.getTradeMaxNumber()) {
                    throw new CartException(ErrorCode.SkuNotAvailable,
                            String.format("商品%s最多只允许购买%d个",
                                    cartItem.getProductName(), cartItem.getTradeMaxNumber()));
                }
            }
        }
    }


}