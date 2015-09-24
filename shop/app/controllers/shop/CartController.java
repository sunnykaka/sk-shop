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
import productcenter.models.Product;
import productcenter.models.SkuStorage;
import productcenter.models.StockKeepingUnit;
import productcenter.services.ProductService;
import productcenter.services.SkuAndStorageService;
import usercenter.models.User;
import usercenter.models.address.Address;
import usercenter.services.AddressService;
import usercenter.utils.SessionUtils;
import utils.secure.SecuredAction;
import views.html.shop.chooseAddress;
import views.html.shop.showCart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private ProductService productService;

    @Autowired
    private CartProcess cartProcess;

    /**
     * 获取库存信息，用于前端数据验证(最大购买数限制、和库存)
     * @param skuId
     * @return
     */
    @SecuredAction
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
     * 获取用户购物车中购买项当前数量
     * @return
     */
    public Result getUserCartItemNum() {
        try {
            User curUser = SessionUtils.currentUser();
            if(curUser == null || curUser.getId() <= 0) {
                return ok(new JsonResult(true,"用户没有登陆", 0).toNode());
            } else {
                Cart cart = cartService.getCartByUserId(curUser.getId());
                if(cart == null) {
                    return ok(new JsonResult(true,"用户还没在系统购买过东西", 0).toNode());
                }
                List<CartItem> cartItems = cartService.queryCarItemsByCartId(cart.getId());
                if(cartItems == null || cartItems.size() == 0) {
                    return ok(new JsonResult(true,"用户当前购物车为空", 0).toNode());
                }
                int totalNum = 0;
                for(CartItem cartItem : cartItems) {
                    totalNum += cartItem.getNumber();
                }
                return ok(new JsonResult(true, "用户购买了东西", totalNum).toNode());
            }
        } catch (final Exception e) {
            Logger.error("获取用户购物车信息出现异常:", e);
            return ok(new JsonResult(false,"获取用户购物车信息出现异常").toNode());
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
    @SecuredAction
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
    @SecuredAction
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
    private Result addSkuToCart(int skuId, int number, boolean isReplace){
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
                return ok(new JsonResult(false, "商品已下架或已售罄！","addForbid").toNode());
            }

            SkuStorage skuStorage = skuAndStorageService.getSkuStorage(skuId);
            int maxStockNum = skuStorage.getStockQuantity();
            if (maxStockNum <= 0) {
                return ok(new JsonResult(false,"已售罄","addForbid").toNode());
            }

            User curUser = SessionUtils.currentUser();
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

            int maxCanBuyNum = skuStorage.getTradeMaxNumber();

            Map<String,Integer> retMap = new HashMap<String,Integer>();

            if (addNumber > maxCanBuyNum) {
                retMap.put("maxCanBuyNum", maxCanBuyNum);
                return ok(new JsonResult(false,"超过最大购买商品数量,最多能够购买" + maxCanBuyNum +"件,购物车中已有" + (addNumber - number) + "件该商品", retMap).toNode());
            }

            if (addNumber > maxStockNum) {
                if(maxStockNum < maxCanBuyNum) {
                    maxCanBuyNum = maxStockNum;
                }
                retMap.put("maxCanBuyNum", maxCanBuyNum);
                return ok(new JsonResult(false,"超过最大购买商品数量,最多能够购买" + maxCanBuyNum +"件,购物车中已有" + (addNumber - number) + "件该商品", retMap).toNode());
            }

            createOrUpdateUserCart(curUser.getId(), skuId, number, isReplace);

            if(!isReplace) {
                List<CartItem> cartItems = null;
                cart = this.buildUserSimpleCart(curUser.getId());
                if(cart != null) {
                    cartItems = cart.getCartItemList();
                }
                if(cartItems == null || cartItems.size() == 0) {
                    return ok(new JsonResult(true,"用户当前购物车为空", 1).toNode());
                }
                int totalNum = 0;
                for(CartItem cartItem : cartItems) {
                    totalNum += cartItem.getNumber();
                }
                retMap.put("itemTotalNum", totalNum);
            }
            return ok(new JsonResult(true, "购物车成功添加" + number + "件该商品", retMap).toNode());
        } catch (Exception e) {
            Logger.error("sku[" + skuId + "]数量为[" + number + "]添加购物车时出现异常:", e);
            return ok(new JsonResult(false,"加入购物车时服务器发生异常，请联系商城客服人员").toNode());
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
    @SecuredAction
    public Result showCart(){
        try {
            User curUser = SessionUtils.currentUser();
            Cart cart = cartProcess.buildUserCart(curUser.getId());
            return ok(showCart.render(cart));
        } catch (Exception e) {
            Logger.error("进入我的购物车发生异常:", e);
            return ok(new JsonResult(false,"进入我的购物车发生异常，请联系商城客服人员").toNode());
        }
    }

    /**
     * 管理购物车的时候删除某个SKU商品
     *
     * @param cartItemId
     */
    @SecuredAction
    public Result deleteCartItem(int cartItemId) {
        try {
            cartService.deleteCartItemById(cartItemId);
            return ok(new JsonResult(true,"删除成功").toNode());
        } catch (Exception e) {
            Logger.error("删除购物车失败:", e);
            return ok(new JsonResult(false,"删除失败").toNode());
        }
    }

    /**
     * 去结算-验证数据
     * @return
     */
    @SecuredAction
    public Result selCartItemProcess(String selCartItems) {
        if(selCartItems == null || selCartItems.trim().length() == 0) {
            return ok(new JsonResult(false,"去结算项为空！").toNode());
        }
        List<Integer>cartItemIdlist = new ArrayList<Integer>();
        try {
            String[] split = selCartItems.split("_");
            for (int i = 0; i < split.length; i++) {
                cartItemIdlist.add(Integer.valueOf(split[i]));
            }
        } catch (Exception e) {
            Logger.warn("解析传递到后台的选中购物车项id发生异常", e);
            return ok(new JsonResult(false, "选中的购物车商品有问题，请核对一下").toNode());
        }

        try {
            User curUser = SessionUtils.currentUser();
            Cart cart = cartProcess.buildUserCart(curUser.getId());
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

            List<CartItem> selCartItemList = new ArrayList<CartItem>();
            for (CartItem cartItem : cartItemList) {
                cartItem.setSelected(false);
                if(!cartItemIdlist.contains(cartItem.getId())) {
                    continue;
                }
                if(!cartItem.isHasStock()) {
                    return ok(new JsonResult(false,"商品" + cartItem.getProductName() + "已售罄").toNode());
                }
                if(!cartItem.isOnline()) {
                    return ok(new JsonResult(false,"商品" + cartItem.getProductName() + "已下架").toNode());
                }

                if(cartItem.getStockQuantity() < cartItem.getNumber()) {
                    return ok(new JsonResult(false,"商品" + cartItem.getProductName() + "购买数量超过了库存数，库存只有" + cartItem.getStockQuantity() + "个").toNode());
                }
                cartItem.setSelected(true);
                selCartItemList.add(cartItem);
            }
            //更新购物车是否选中
            cartService.updateCartItemList(selCartItemList);
            return ok(new JsonResult(true,"选中购物车项选中状态更新成功").toNode());
        } catch (Exception e) {
            Logger.warn("去结算-选择邮递地址发生异常:", e);
            return ok(new JsonResult(false,"去结算发生异常，请联系商城客服人员").toNode());
        }
    }

    /**
     * 去结算-选择送货地址
     * @return
     */
    @SecuredAction
    public Result chooseAddress(String selCartItems) {
        if(selCartItems.contains("_")) {
            selCartItems = selCartItems.replaceAll("_", ",");
        }
        User curUser = SessionUtils.currentUser();
        Cart cart = cartProcess.buildUserCartBySelItem(curUser.getId(), selCartItems);
        List<Address> addressList = addressService.queryAllAddress(curUser.getId(), true);
        return ok(chooseAddress.render(selCartItems, addressList, cart,false));
    }

    /**
     * 验证立即支付数据
     * @return
     */
    @SecuredAction
    public Result verifyPromptlyPayData(int skuId, int number) {
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
                return ok(new JsonResult(false, "商品已下架或已售罄！","addForbid").toNode());
            }

            SkuStorage skuStorage = skuAndStorageService.getSkuStorage(skuId);
            int maxStockNum = skuStorage.getStockQuantity();
            if (maxStockNum <= 0) {
                return ok(new JsonResult(false,"已售罄","addForbid").toNode());
            }
            return ok(new JsonResult(true).toNode());
        } catch (Exception e) {
            Logger.error("sku[" + skuId + "]数量为[" + number + "]立即支付出现异常:", e);
            return ok(new JsonResult(false,"立即支付失败，请联系商城客服人员！").toNode());
        }
    }

    /**
     * 立即支付-去结算-选择送货地址
     * @return
     */
    @SecuredAction
    public Result promptlyPayChooseAddress(int skuId, int number) {
        User curUser = SessionUtils.currentUser();

        Cart cart = new Cart();
        CartItem cartItem = new CartItem();
        cartItem.setSkuId(skuId);
        cartItem.setNumber(number);

        Money totalMoney = Money.valueOf(0);
        totalMoney = cartProcess.setCartItemValues(cartItem);

        cart.setTotalMoney(totalMoney);
        List<CartItem> cartItemList = new ArrayList<CartItem>();
        cartItemList.add(cartItem);
        cart.setCartItemList(cartItemList);

        List<Address> addressList = addressService.queryAllAddress(curUser.getId(), true);
        return ok(chooseAddress.render(skuId + ":" + number, addressList, cart,true));
    }

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