package controllers.api.shop;

import api.response.shop.CartDto;
import common.utils.JsonUtils;
import controllers.BaseController;
import ordercenter.models.Cart;
import ordercenter.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import play.mvc.Result;
import usercenter.models.User;
import usercenter.models.address.Address;
import usercenter.services.AddressService;
import utils.secure.SecuredAction;

import java.util.HashMap;
import java.util.Map;

/**
 * 购物车Controller
 * User: lidujun
 * Date: 2015-08-25
 */
@org.springframework.stereotype.Controller
public class AppCartController extends BaseController {

    @Autowired
    private CartService cartService;

    @Autowired
    private AddressService addressService;


    /**
     * 获取用户购物车中购买项当前数量
     * @return
     */
    public Result getUserCartItemNum() {

        int totalNum = 0;
        User user = this.currentUser();
        if(user != null) {
            Cart cart = cartService.getCartByUserId(user.getId());
            if(cart != null) {
                totalNum = cart.calcTotalNum();
            }
        }

        Map<String,Integer> totalNumMap = new HashMap<>();
        totalNumMap.put("totalNum", totalNum);
        return ok(JsonUtils.object2Node(totalNumMap));
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
        return this.addSkuToCart(skuId, number, true);
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

        Map<String, Object> result = new HashMap<>();

        int totalNum = cartService.addSkuToCart(currentUser().getId(), skuId, number, isReplace);
        result.put("itemTotalNum", totalNum);

        return ok(JsonUtils.object2Node(result));
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
        User curUser = this.currentUser();
        Cart cart = cartService.buildUserCart(curUser.getId());
        return ok(JsonUtils.object2Node(CartDto.buildUserCart(cart)));
    }

    /**
     * 管理购物车的时候删除某个SKU商品
     *
     * @param cartItemId
     */
    @SecuredAction
    public Result deleteCartItem(int cartItemId) {
        cartService.deleteCartItemById(cartItemId);
        return noContent(); //删除成功
    }

    /**
     * 去结算
     * @return
     */
    @SecuredAction
    public Result toBilling(String selCartItems) {

        User user = currentUser();

        Cart cart = cartService.buildUserCart(user.getId());

        cartService.selectCartItems(cart, selCartItems);

        Address defaultAddress = addressService.queryDefaultAddress(user.getId());

        Map<String, Object> retMap = new HashMap<>();
        retMap.put("cart",CartDto.buildUserCart(cart));
        retMap.put("defaultAddress", defaultAddress);

        return ok(JsonUtils.object2Node(retMap));
    }

    /**
     * 立即支付
     * @return
     */
    @SecuredAction
    public Result toBillingByPromptlyPay(int skuId, int number) {

        cartService.verifySkuToBuy(skuId, number, number);

        Cart cart = cartService.fakeCartForPromptlyPay(skuId, number);

        Address defaultAddress = addressService.queryDefaultAddress(currentUser().getId());

        Map<String, Object> retMap = new HashMap<>();
        retMap.put("cart", CartDto.buildUserCart(cart));
        retMap.put("defaultAddress", defaultAddress);

        return ok(JsonUtils.object2Node(retMap));
    }

}