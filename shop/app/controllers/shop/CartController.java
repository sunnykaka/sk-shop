package controllers.shop;

import common.utils.JsonResult;
import common.utils.Money;
import ordercenter.models.Cart;
import ordercenter.models.CartItem;
import ordercenter.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import productcenter.models.*;
import productcenter.services.ProductPictureService;
import productcenter.services.ProductService;
import productcenter.services.PropertyAndValueService;
import productcenter.services.SkuAndStorageService;
import services.CmsService;
import usercenter.models.User;
import usercenter.models.address.Address;
import usercenter.services.AddressService;
import views.html.shop.chooseAddress;
import views.html.shop.showCart;

import java.util.List;

/**
 * 购物车Controller
 * User: lidujun
 * Date: 2015-05-06
 */
@org.springframework.stereotype.Controller
public class CartController extends Controller {

    @Autowired
    private CartService cartService;

    @Autowired
    private SkuAndStorageService skuAndStorageService;

    @Autowired
    private AddressService addressService;

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
     * 获取库存信息，用于前端数据验证(最大购买数限制、和库存)
     * @param skuId
     * @return
     */
    //@SecuredAction
    public Result getSkuStorage(int skuId) {
        try {
            SkuStorage skuStorage = skuAndStorageService.getSkuStorage(skuId);
            return ok(new JsonResult(true,"sku当前库存信息", skuStorage).toNode());
        } catch (final Exception e) {
            Logger.error("获取sku当前库存信息出现异常:", e);
            return ok(new JsonResult(false,"获取sku当前库存信息出现异常").toNode());
        }
    }

    /**
     * 添加购物车，用户必须已经登陆，没登陆让用户先跳转到用户登陆界面
     * 响应客户端把一个sku加入购物车
     * 一个商品加入购物车之前会检查库存，如果没库存了不允许增加
     * 同一商品数量累加，不替换
     * @param skuId
     * @param number
     * @return
     */
    //@SecuredAction
    public Result addSkuToCartAddNum(int skuId, int number) {
        return this.addSkuToCart(skuId, number,false);
    }

    /**
     * 添加购物车，用户必须已经登陆，没登陆让用户先跳转到用户登陆界面
     * 响应客户端把一个sku加入购物车
     * 一个商品加入购物车之前会检查库存，如果没库存了不允许增加
     * 同一商品数量不累加，直接替换
     * @param skuId
     * @param number
     * @return
     */
    //@SecuredAction
    public Result addSkuToCartReplaceNum(int skuId, int number) {
        return this.addSkuToCart(skuId, number,true);
    }

    /**
     * 添加购物车，用户必须已经登陆，没登陆让用户先跳转到用户登陆界面
     * 响应客户端把一个sku加入购物车
     * 一个商品加入购物车之前会检查库存，如果没库存了不允许增加
     * @param skuId
     * @param number
     * @param isReplace 是否替换同一个商品数量
     * @return
     */
    //@SecuredAction
    public Result addSkuToCart(int skuId, int number, boolean isReplace){
        try {
            if (number < 1) {
                return ok(new JsonResult(false, "购买数量不能小于1！").toNode());
            }

            //sku所属商品是否下架或被移除
            boolean flag = false;
            StockKeepingUnit sku = skuAndStorageService.getStockKeepingUnitById(skuId);
            if (sku != null && sku.canBuy()) {
                Product product = productService.getProductById(sku.getProductId());
                if ((!product.getIsDelete()) && product.isOnline()) {
                    flag = true;
                }
            }
            if (!flag) {
                return ok(new JsonResult(false, "商品已下架或被移除！","addForbid").toNode());
            }

            SkuStorage skuStorage = skuAndStorageService.getSkuStorage(skuId);
            int maxStockNum = skuStorage.getStockQuantity();
            if (maxStockNum <= 0) {
                return ok(new JsonResult(false,"已经售完","addForbid").toNode());
            }



            /////////////////测试 /////////////////
            User curUser = new User();
            curUser.setId(14311);
            //测试

            /////////////////测试 /////////////////

            //User curUser = SessionUtils.currentUser();
            Cart cart = this.buildUserSimpleCart(curUser.getId());
            int addNumber = number;
            if(!isReplace) {
                if (cart != null) {
                    List<CartItem> cartItemList = cart.getCartItemList();
                    cart.setCartItemList(cartItemList);
                    for (CartItem cartItem : cartItemList) {
                        if (cartItem.getSkuId() == skuId) {
                            addNumber = number + cartItem.getNumber();
                            break;
                        }
                    }
                }
            }else {
                addNumber = number;
            }

            if (addNumber > skuStorage.getTradeMaxNumber()) {
                return ok(new JsonResult(false,"超过最大购买数量,购物车中已有" + (addNumber - number) + "件该商品").toNode());
            }

            if (addNumber > maxStockNum) {
                return ok(new JsonResult(false,"超过最大能购买商品数量,最多还能够购买" + maxStockNum + "件该商品","maxStockNum:" + maxStockNum).toNode());
            }

            createOrUpdateUserCart(curUser.getId(), skuId, number, isReplace);
            return ok(new JsonResult(true, "购物车成功添加" + number + "件该商品", "itemTotalNum:" + addNumber).toNode());
        } catch (Exception e) {
            Logger.error("sku[" + skuId + "]数量为[" + number + "]添加购物车时出现异常:", e);
            return ok(new JsonResult(false,"加入购物车时服务器发生异常").toNode());
        }
    }

    /**
     * 展示购物车
     * 用户登陆了就用用户的ID去找购物车，没登陆就跳转到用户登陆界面
     * select * from boss.Cart where id=29627; //userid = 14311
     * select * from boss.cartitem where cartid=29627;
     *
     * @return
     */
    //@SecuredAction
    public Result showCart(){
        //测试
        User curUser = new User();
        curUser.setId(14311);
        //测试

        //User curUser = SessionUtils.currentUser();
        Cart cart = this.buildUserCart(curUser.getId());
        return ok(showCart.render(cart));
    }

    /**
     * 管理购物车的时候删除某个SKU商品
     *
     * @param cartId
     * @param skuId
     */
    //@SecuredAction
    public Result deleteCartItem(int cartId, int skuId) {
        try {
            cartService.deleteCartItemBySkuIdAndCartId(skuId, cartId);
            Cart cart = cartService.getCart(cartId);
            return ok(new JsonResult(true,"删除购物车成功").toNode());
        } catch (Exception e) {
            Logger.error("删除购物车失败:", e);
            return ok(new JsonResult(false,"删除购物车失败").toNode());
        }
    }

    /**
     * 去结算-选择送货地址
     * @return
     */
    //@SecuredAction
    public Result chooseAddress() {
        //测试
        User curUser = new User();
        curUser.setId(14311);
        //测试

        //User curUser = SessionUtils.currentUser();
        Cart cart = this.buildUserCart(curUser.getId());
        String errMsg = "对不起您没有购买任何商品，不能前去支付！";
        if (cart == null) {
            Logger.warn("购物车对象为null的时候进入了填写订单页面:"  + curUser);
            return ok(new JsonResult(false,errMsg).toNode());
        }
        List<CartItem> cartItemList = cart.getCartItemList();
        if (cartItemList.size() == 0) {
            Logger.warn("购物车对象为null的时候进入了填写订单页面:" + curUser);
            return ok(new JsonResult(false,errMsg).toNode());
        }

        for (CartItem cartItem : cartItemList) {
            if(!cartItem.isHasStock()) {
                return ok(new JsonResult(false,"商品" + cartItem.getProductName() + "已经没有库存，无法购买").toNode());
            }
            if(!cartItem.isOnline()) {
                return ok(new JsonResult(false,"商品" + cartItem.getProductName() + "下架或已经被删除").toNode());
            }

            if(cartItem.getStockQuantity() < cartItem.getNumber()) {
                return ok(new JsonResult(false,"商品" + cartItem.getProductName() + "购买数量超过了库存数，库存只有" + cartItem.getStockQuantity() + "个").toNode());
            }
        }
        List<Address> addressList = addressService.queryAllAddress(curUser.getId());
        cart = cartService.getCartByUserId(curUser.getId());
        return ok(chooseAddress.render(addressList, cart));
    }

//    /**
//     * 去结算-选择送货地址
//     * @return
//     */
//    //@SecuredAction
//    public Result directChooseAddress() {
//        //测试
//        User curUser = new User();
//        curUser.setId(14311);
//        //测试
//
//        //User curUser = SessionUtils.currentUser();
//        Cart cart = this.buildUserCart(curUser.getId());
//        String errMsg = "对不起您没有购买任何商品，不能前去支付！";
//        if (cart == null) {
//            Logger.warn("购物车对象为null的时候进入了填写订单页面:"  + curUser);
//            return ok(new JsonResult(false,errMsg).toNode());
//        }
//        List<CartItem> cartItemList = cart.getCartItemList();
//        if (cartItemList.size() == 0) {
//            Logger.warn("购物车对象为null的时候进入了填写订单页面:" + curUser);
//            return ok(new JsonResult(false,errMsg).toNode());
//        }
//
//        Cart cart = new Cart();
//        CartItem cartItem = new CartItem();
//
//        for (CartItem cartItem : cartItemList) {
//            if(!cartItem.isHasStock()) {
//                return ok(new JsonResult(false,"商品" + cartItem.getProductName() + "已经没有库存，无法购买").toNode());
//            }
//            if(!cartItem.isOnline()) {
//                return ok(new JsonResult(false,"商品" + cartItem.getProductName() + "下架或已经被删除").toNode());
//            }
//
//            if(cartItem.getStockQuantity() < cartItem.getNumber()) {
//                return ok(new JsonResult(false,"商品" + cartItem.getProductName() + "购买数量超过了库存数，库存只有" + cartItem.getStockQuantity() + "个").toNode());
//            }
//        }
//        List<Address> addressList = addressService.queryAllAddress(curUser.getId());
//        cart = cartService.getCartByUserId(curUser.getId());
//        return ok(chooseAddress.render(addressList, cart));
//    }

    /**
     * 用户购物车操作方法
     * @param userId
     * @param skuId
     * @param number
     * @param isReplace
     * @return
     */
    private void createOrUpdateUserCart(int userId, int skuId, int number, boolean isReplace) {
        Cart cart = this.buildUserSimpleCart(userId);
        if(cart == null) {
            cartService.initCartByUserId(userId, skuId, number);
        } else {
            cartService.addSkuToCart(cart, skuId, number, isReplace);
        }
    }

    /**
     * 将Cart对象构建完整，包含整个购物车界面展示需要的东东。
     * 需要界面展示时使用
     * @param userId
     * @return
     */
    private Cart buildUserCart(int userId) {
        Cart cart = cartService.getCartByUserId(userId);
        if (cart != null) {
            List<CartItem> cartItems = cartService.queryCarItemsByCartId(cart.getId());
            cart.setCartItemList(cartItems);
            //合计价格
            Money totalMoney = Money.valueOf(0);
            if(cartItems != null && cartItems.size() > 0) {
                for (CartItem cartItem : cartItems) {

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
                        totalMoney = totalMoney.add(cartItem.getCurUnitPrice().multiply(cartItem.getNumber()));

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
                            Logger.warn("构建购物车时发现sku被删除，删除成不成功无所谓" + + cartItem.getSkuId() + " : " + cartItem.getCartId());
                            cartService.deleteCartItemBySkuIdAndCartId(cartItem.getSkuId(), cartItem.getCartId());
                        } catch (Exception e) {
                            Logger.warn("构建购物车删除sku失败，此处删除成不成功无所谓" + cartItem.getSkuId() + " : " + cartItem.getCartId());
                        }
                    }
                }
            }
            cart.setTotalMoney(totalMoney);
        }
        return cart;
    }

    /**
     * 将Cart对象构建完整，仅只包含购物车和购物车项，其它关联对象都没有包含进来。
     * 非界面展示时候使用
     * @param userId
     * @return
     */
    private Cart buildUserSimpleCart(int userId) {
        Cart cart = cartService.getCartByUserId(userId);
        if (cart != null) {
            List<CartItem> cartItems = cartService.queryCarItemsByCartId(cart.getId());
            cart.setCartItemList(cartItems);
        }
        return cart;
    }

}