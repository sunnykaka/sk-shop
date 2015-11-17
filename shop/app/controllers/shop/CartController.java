package controllers.shop;

import com.google.common.collect.Lists;
import common.utils.JsonResult;
import ordercenter.excepiton.CartException;
import ordercenter.models.Cart;
import ordercenter.models.Voucher;
import ordercenter.services.CartService;
import ordercenter.services.VoucherService;
import ordercenter.util.CartUtil;
import org.springframework.beans.factory.annotation.Autowired;
import play.mvc.Controller;
import play.mvc.Result;
import productcenter.models.SkuStorage;
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
import java.util.stream.Collectors;

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
    private VoucherService voucherService;


    /**
     * 获取库存信息，用于前端数据验证(最大购买数限制、和库存)
     * @param skuId
     * @return
     */
    @SecuredAction
    public Result getSkuStorage(int skuId) {
        SkuStorage skuStorage = skuAndStorageService.getSkuStorage(skuId);
        return ok(new JsonResult(true,"sku当前库存信息", skuStorage).toNode());
    }

    /**
     * 获取用户购物车中购买项当前数量
     * @return
     */
    public Result getUserCartItemNum() {

        int totalNum = 0;
        User user = SessionUtils.currentUser();
        if(user != null) {
            Cart cart = cartService.getCartByUserId(user.getId());
            if(cart != null) {
                totalNum = cart.calcTotalNum();
            }
        }

        return ok(new JsonResult(true, "", totalNum).toNode());
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

        return this.addSkuToCart(skuId, number, false);
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
        try {
            int totalNum = cartService.addSkuToCart(SessionUtils.currentUser().getId(), skuId, number, isReplace);
            result.put("itemTotalNum", totalNum);

            return ok(new JsonResult(true, "购物车成功添加" + number + "件该商品", result).toNode());
        } catch (CartException e) {
            if(e.getMaxCanBuyNum() != null) {
                result.put("maxCanBuyNum", e.getMaxCanBuyNum());
            }

            return ok(new JsonResult(false, e.getMessage(), result).toNode());
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
        User user = SessionUtils.currentUser();
        Cart cart = cartService.buildUserCart(user.getId());
        return ok(showCart.render(cart));
    }

    /**
     * 管理购物车的时候删除某个SKU商品
     *
     * @param cartItemId
     */
    @SecuredAction
    public Result deleteCartItem(int cartItemId) {
        cartService.deleteCartItemById(cartItemId);

        int totalNum = 0;
        User user = SessionUtils.currentUser();
        if(user != null) {
            Cart cart = cartService.getCartByUserId(user.getId());
            if(cart != null) {
                totalNum = cart.calcTotalNum();
            }
        }
        Map<String,Integer> map = new HashMap<>();
        map.put("itemTotalNum", totalNum);

        return ok(new JsonResult(true,"删除成功",map).toNode());
    }

    /**
     * 去结算-验证数据
     * @return
     */
    @SecuredAction
    public Result selCartItemProcess(String selCartItems) {
        try {

            User user = SessionUtils.currentUser();

            List<Integer> selCartItemIdList = CartUtil.getCartItemIdList(selCartItems);

            Cart cart = cartService.buildUserCart(user.getId());

            cartService.selectCartItems(cart, selCartItemIdList);

            return ok(new JsonResult(true,"选中购物车项选中状态更新成功").toNode());

        } catch (CartException e) {
            return ok(new JsonResult(false, e.getMessage()).toNode());
        }

    }

    /**
     * 去结算-选择送货地址
     * @return
     */
    @SecuredAction
    public Result chooseAddress(String selCartItems) {
        List<Integer> selCartItemIdList = CartUtil.getCartItemIdList(selCartItems);
        User curUser = SessionUtils.currentUser();
        Cart cart = cartService.buildUserCartBySelItem(curUser.getId(), selCartItemIdList);
        List<Address> addressList = addressService.queryAllAddress(curUser.getId(), true);
        List<Voucher> voucherList = voucherService.findUserAvailableVouchers(curUser.getId());
        return ok(chooseAddress.render(selCartItems, addressList, cart, false, voucherList));
    }

    /**
     * 验证立即支付数据
     * @return
     */
    @SecuredAction
    public Result verifyPromptlyPayData(int skuId, int number) {
        try {
            cartService.verifySkuToBuy(skuId, number, number);

            return ok(new JsonResult(true).toNode());
        } catch (CartException e) {

            return ok(new JsonResult(false, e.getMessage()).toNode());
        }
    }

    /**
     * 立即支付-去结算-选择送货地址
     * @return
     */
    @SecuredAction
    public Result promptlyPayChooseAddress(int skuId, int number) {
        User curUser = SessionUtils.currentUser();

        Cart cart = cartService.fakeCartForPromptlyPay(skuId, number);

        List<Address> addressList = addressService.queryAllAddress(curUser.getId(), true);
        List<Voucher> voucherList = voucherService.findUserAvailableVouchers(curUser.getId());

        return ok(chooseAddress.render(skuId + ":" + number, addressList, cart, true, voucherList));
    }


}