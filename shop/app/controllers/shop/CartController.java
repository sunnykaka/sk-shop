package controllers.shop;

import common.utils.JsonResult;
import ordercenter.models.Cart;
import ordercenter.models.CartItem;
import ordercenter.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import play.mvc.Controller;
import play.mvc.Result;
import productcenter.models.SkuStorage;
import productcenter.services.SkuAndStorageService;
import usercenter.models.User;
import views.html.shop.showCart;

import java.util.List;

import static play.Logger.error;


/**
 * 购物车Controller
 */
@org.springframework.stereotype.Controller
public class CartController extends Controller {

    @Autowired
    private CartService cartService;

    @Autowired
    SkuAndStorageService skuAndStorageService;

    /**
     * 获取库存信息，用于前端数据验证(最大购买数限制)
     * @param skuId
     * @return
     */
    public Result getSkuStorage(int skuId) {
        try {
            SkuStorage skuStorage = skuAndStorageService.getSkuStorage(skuId);
            return ok(new JsonResult(true,"sku当前库存信息", skuStorage).toNode());
        } catch (final Exception e) {
            error("获取sku当前库存信息出现异常:", e);
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
    public Result addSkuToCartNotReplace(int skuId, int number) {
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
    public Result addSkuToCartReplace(int skuId, int number) {
        return this.addSkuToCart(skuId, number,true);
    }

    /**
     * 用户购物车操作方法
     * @param userId
     * @param skuId
     * @param number
     * @param isReplace
     * @return
     */
    private Cart useUserId(int userId, int skuId, int number, boolean isReplace) {
        Cart cart = cartService.getCartByUserId(userId);
        if(cart == null) {
            cartService.initCartByUserId(userId, skuId, number);
        } else {
            cartService.addSkuToCart(cart, skuId, number,isReplace);
        }
        //重新加载购物车
        cart = cartService.getCartByUserId(userId);
        return cart;
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
            //测试
            User curUser = new User();
            curUser.setId(14311);
            //测试

            //User curUser = SessionUtils.currentUser();
            if(curUser != null) {
                if (!skuAndStorageService.isSkuUsable(skuId)) { //ldj
                    return ok(new JsonResult(false, "商品已下架或被移除！").toNode());
                }
                if (number < 1) {
                    return ok(new JsonResult(false, "购买数量不能小于1！").toNode());
                }

                Cart cart = cartService.getCartByUserId(curUser.getId());
                int addNumber = 0;
                if(!isReplace) {
                    if (cart != null) {
                        List<CartItem> cartItemList = cart.getCartItemList();
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

                SkuStorage skuStorage = skuAndStorageService.getSkuStorage(skuId);
                if (skuStorage.getStockQuantity() <= 0) {
                    return ok(new JsonResult(false,"已经售完","SoldOut").toNode());
                }
                if (addNumber > skuStorage.getTradeMaxNumber()) {
                    return ok(new JsonResult(false,"超过最大购买数量</br>购物车中已有" + (addNumber - number) + "件该商品").toNode());
                }
                useUserId(curUser.getId(), skuId, number, isReplace);
                return ok(new JsonResult(true,"添加购物车成功" + (addNumber - number) + "件该商品").toNode());
            } else {
                return ok(new JsonResult(false,"用户还没有登陆，请先登陆！").toNode());
            }
        } catch (final Exception e) {
            error("sku[" + skuId + "]数量为[" + number + "]添加购物车时出现异常:", e);
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
        try {
            //测试
            User curUser = new User();
            curUser.setId(14311);
            //测试

            //User curUser = SessionUtils.currentUser();
            Cart cart = cartService.getCartByUserId(curUser.getId());
            return ok(showCart.render(true, cart, null));
        } catch (final Exception e) {
            error("展示购物车时出现异常:", e);
            return ok(showCart.render(false, null, "展示购物车时服务器发生异常！"));
        }
    }

    /**
     * 管理购物车的时候删除某个SKU商品
     *
     * @param cartId
     * @param skuId
     */
    public Result deleteCartItem(int cartId, int skuId) {
        try {
            cartService.deleteCartItemBySkuIdAndCartId(skuId, cartId);
            Cart cart = cartService.getCart(cartId);
            return ok(new JsonResult(true,"删除购物车成功").toNode());
        } catch (Exception e) {
            error("删除购物车失败:", e);
            return ok(new JsonResult(false,"删除购物车失败").toNode());
        }
    }

}