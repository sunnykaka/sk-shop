package controllers.api.shop;

import common.exceptions.AppBusinessException;
import common.exceptions.ErrorCode;
import common.utils.JsonResult;
import common.utils.JsonUtils;
import common.utils.Money;
import controllers.BaseController;
import ordercenter.models.Cart;
import ordercenter.models.CartItem;
import ordercenter.services.CartService;
import ordercenter.services.OrderService;
import ordercenter.services.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import play.Logger;
import play.mvc.Result;
import productcenter.services.SkuAndStorageService;
import usercenter.models.User;
import usercenter.models.address.Address;
import usercenter.services.AddressService;
import utils.secure.SecuredAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单及其支付Controller
 * User: lidujun
 * Date: 2015-08-31
 */
@org.springframework.stereotype.Controller
public class OrderAndPayApiController extends BaseController {

    @Autowired
    private OrderService orderService;

    @Autowired
    SkuAndStorageService skuAndStorageService;

    @Autowired
    AddressService addressService;

    @Autowired
    TradeService tradeService;

    @Autowired
    private CartService cartService;

    /**
     * 支付：生成订单、生成配送信息、去支付
     * @param isPromptlyPay 是否是立即购买
     * @param selItems 选中的支付购物车项
     * @param addressId 用户选择的寄送地址
     * @param payOrg 支付机构
     * @return
     */
    @SecuredAction
    public Result submitToPay(boolean isPromptlyPay, String selItems, int addressId, String payOrg) {
        Logger.info("进入submitToPay方法，参数：\n"
                + "isPromptlyPay=" + isPromptlyPay + "selItems=" + selItems + " addressId=" + addressId + " payOrg=" + payOrg);
        String curUserName = "";
        Cart cart = null;
        try {
            if (selItems == null || selItems.trim().length() == 0) {
                throw new AppBusinessException(ErrorCode.Conflict, "订单为空！");
            }

            if (payOrg == null || payOrg.trim().length() == 0) {
                return ok(new JsonResult(false, "没有选择支付机构！").toNode());
            }

            User curUser = this.currentUser();
            curUserName = curUser.getUserName();

            String[] split = null;
            if (isPromptlyPay) {  //立即购买
                int skuId = 0;
                int number = 0;
                try {
                    split = selItems.split(":");
                    skuId = Integer.valueOf(split[0]);
                    number = Integer.valueOf(split[1]);
                } catch (Exception e) {
                    Logger.warn("解析传递到后台的立即购买项id发生异常", e);
                    throw new AppBusinessException(ErrorCode.Conflict, "立即购买商品有问题，请核对一下！");
                }

                if (skuId <= 0) {
                    throw new AppBusinessException(ErrorCode.Conflict, "在系统中找不到商品！");
                }

                if (number <= 0) {
                    throw new AppBusinessException(ErrorCode.Conflict, "至少要购买1件商品！");
                }

                cart = new Cart();
                CartItem cartItem = new CartItem();
                cartItem.setSkuId(skuId);
                cartItem.setNumber(number);

                Money totalMoney = cartService.setCartItemValues(cartItem);
                cart.setTotalMoney(totalMoney);

                List<CartItem> cartItemList = new ArrayList();
                cartItemList.add(cartItem);
                cart.setCartItemList(cartItemList);
            } else {
                List<Integer> selCartItemIdList = new ArrayList();
                try {
                    split = selItems.split(",");
                    for (int i = 0; i < split.length; i++) {
                        selCartItemIdList.add(Integer.valueOf(split[i]));
                    }
                } catch (Exception e) {
                    Logger.warn("解析传递到后台的选中购物车项id发生异常", e);
                    throw new AppBusinessException(ErrorCode.Conflict, "选中的购物车商品有问题，请核对一下！");
                }
                cart = cartService.buildUserCartBySelItem(curUser.getId(), selItems);
            }

            if (cart == null || cart.getCartItemList().size() == 0) {
                throw new AppBusinessException(ErrorCode.Conflict, "订单为空！");
            }

            //库存校验
            for (CartItem cartItem : cart.getCartItemList()) {
                if (!skuAndStorageService.isSkuUsable(cartItem.getSkuId())) {
                    Logger.warn("商品：" + cartItem.getProductName() + "已售罄或已下架或已移除，不能再购买");
                    throw new AppBusinessException(ErrorCode.Conflict, "商品：" + cartItem.getProductName() + "已售罄或已下架，不能再购买");
                }
            }

            //邮寄地址
            Address address = addressService.getAddress(addressId, curUser.getId());
            if (address == null) {
                throw new AppBusinessException(ErrorCode.Conflict, "您选择的订单寄送地址已经被修改，在系统中不存在！");
            }

            //生成订单、生成配送信息
            String orderIds = orderService.submitOrderProcess(selItems, isPromptlyPay, curUser, cart, address);
            Map<String,String> retMap = new HashMap();
            retMap.put("orderIds", orderIds);
            return ok(JsonUtils.object2Node(retMap));

            //去支付（产生交易和支付部分没做）
        } catch (Exception e) {
            Logger.error(curUserName + "提交的订单在生成订单的过程中出现异常，其购物车信息：" + cart, e);
            throw new AppBusinessException(ErrorCode.Conflict, "生成订单失败，请联系商城客服人员！");
        }
    }

}