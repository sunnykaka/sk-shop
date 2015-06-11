package controllers.shop;

import common.utils.JsonResult;
import common.utils.Money;
import ordercenter.constants.BizType;
import ordercenter.constants.TradePayType;
import ordercenter.models.Cart;
import ordercenter.models.CartItem;
import ordercenter.models.Order;
import ordercenter.models.OrderItem;
import ordercenter.payment.PayInfoWrapper;
import ordercenter.payment.PayRequestHandler;
import ordercenter.payment.PaymentManager;
import ordercenter.payment.constants.PayBank;
import ordercenter.payment.constants.PayMethod;
import ordercenter.services.OrderService;
import ordercenter.services.TradeService;
import ordercenter.util.TradeSequenceUtil;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import productcenter.services.SkuAndStorageService;
import usercenter.models.User;
import usercenter.models.address.Address;
import usercenter.services.AddressService;
import usercenter.utils.SessionUtils;
import utils.secure.SecuredAction;
import views.html.shop.orderPlay;
import views.html.shop.orderToPay;

import java.util.ArrayList;
import java.util.List;


/**
 * 订单及其支付Controller
 * User: lidujun
 * Date: 2015-05-11
 */
@org.springframework.stereotype.Controller
public class OrderAndPayController extends Controller {

    @Autowired
    private OrderService orderService;

    @Autowired
    SkuAndStorageService skuAndStorageService;

    @Autowired
    AddressService addressService;

    @Autowired
    TradeService tradeService;

    @Autowired
    CartProcess cartProcess;

    /**
     * 提交订单-生成订单
     * @param addressId 用户选择的寄送地址
     * @return
     */
    @SecuredAction
    public Result submitOrder(String selItems, int addressId, boolean isPromptlyPay) {
        String curUserName = "";
        Cart cart = null;
        try {
            if(selItems == null || selItems.trim().length() == 0) {
                return ok(new JsonResult(false,"去结算项为空！").toNode());
            }

            User curUser = SessionUtils.currentUser();
            curUserName = curUser.getUserName();

            String[] split = null;
            if(isPromptlyPay) {  //立即购买
                int skuId = 0;
                int number =0;
                try {
                    split = selItems.split(":");
                    skuId = Integer.valueOf(split[0]);
                    number = Integer.valueOf(split[1]);
                } catch (Exception e) {
                    Logger.warn("解析传递到后台的立即购买项id发生异常", e);
                    return ok(new JsonResult(false, "立即购买商品有问题，请核对一下").toNode());
                }

                if(skuId <= 0) {
                    return ok(new JsonResult(false,"在系统中找不到立即购买的商品！").toNode());
                }

                if(number <= 0) {
                    return ok(new JsonResult(false,"至少要购买1件商品！").toNode());
                }

                cart = new Cart();
                CartItem cartItem = new CartItem();
                cartItem.setSkuId(skuId);
                cartItem.setNumber(number);

                Money totalMoney = Money.valueOf(0);
                totalMoney = cartProcess.setCartItemValues(cartItem);

                cart.setTotalMoney(totalMoney);
                List<CartItem> cartItemList = new ArrayList<CartItem>();
                cartItemList.add(cartItem);
                cart.setCartItemList(cartItemList);
            } else {
                List<Integer> selCartItemIdList = new ArrayList<Integer>();
                try {
                    split = selItems.split(",");
                    for (int i = 0; i < split.length; i++) {
                        selCartItemIdList.add(Integer.valueOf(split[i]));
                    }
                } catch (Exception e) {
                    Logger.warn("解析传递到后台的选中购物车项id发生异常", e);
                    return ok(new JsonResult(false, "选中的购物车商品有问题，请核对一下").toNode());
                }
                cart = cartProcess.buildUserCartBySelItem(curUser.getId(), selItems);
            }

            if (cart == null || cart.getCartItemList().size() == 0) {
                return ok(new JsonResult(false,"出现错误：购物车为空").toNode());
            }

            //库存校验
            for(CartItem cartItem : cart.getCartItemList()) {
                if (!skuAndStorageService.isSkuUsable(cartItem.getSkuId())) {
                    Logger.warn("商品：" + cartItem.getProductName() + "已售完或已下架或已移除，不能再购买");
                    return ok(new JsonResult(false,"商品：" + cartItem.getProductName() + "已售完或已下架或已移除，不能再购买").toNode());
                }
            }

            //邮寄地址
            Address address = addressService.getAddress(addressId, curUser.getId());
            if(address == null) {
                return ok(new JsonResult(false,"您选择的订单寄送地址已经被修改，在系统中不存在！").toNode());
            }

            //生成订单相关信息
            int orderId = orderService.submitOrderProcess(selItems, isPromptlyPay, curUser, cart, address);
            return ok(new JsonResult(true,"生成订单成功",orderId).toNode());
        } catch (Exception e) {
            Logger.error(curUserName + "提交的订单在生成订单的过程中出现异常，其购物车信息：" + cart, e);
            return ok(new JsonResult(false,"生成订单失败，请联系商城客服人员！").toNode());
        }
    }

    /**
     * 提交订单-选择支付方式(生成订单)
     * @param orderId 用户选择的寄送地址
     * @return
     */
    @SecuredAction
    public Result toOrderPlay(int orderId) {
        User curUser = SessionUtils.currentUser();
        Order order = orderService.getOrderById(orderId, curUser.getId());
        return ok(orderPlay.render(order));
    }

    /**
     * 提交订单-验证并生成交易记录
     * @param payType
     * @param payMethod
     * @param payOrg
     * @param orderIds
     * @return
     */
    @SecuredAction
    public Result submitTradeOrder(String payType, String payMethod, String payOrg, String orderIds) {
        //参数组织没想好
        if(payType == null || payType.trim().length() == 0) {
            return ok(new JsonResult(false,"没有选择支付类型！").toNode());
        }
        if(payMethod == null || payMethod.trim().length() == 0) {
            return ok(new JsonResult(false,"没有选择支付方式！").toNode());
        }

        if(payOrg == null || payOrg.trim().length() == 0) {
            return ok(new JsonResult(false,"没有选择支付机构！").toNode());
        }
        if(orderIds == null || orderIds.trim().length() == 0) {
            return ok(new JsonResult(false,"需要支付的订单为空！").toNode());
        }

        String curUserName = "";
        try {
            User curUser = SessionUtils.currentUser();
            curUserName = curUser.getUserName();

            //仅用于验证用
            PayMethod payMethodEnum = PayMethod.valueOf(payMethod);

            String[] split = orderIds.split(",");
            Integer[] idList = new Integer[split.length];
            for (int i = 0; i < split.length; i++) {
                if (!NumberUtils.isNumber(split[i])) {
                    Logger.warn("订单支付出现异常:" + "订单号" + split[i] + "错误！");
                    return ok(new JsonResult(false,"订单号" + split[i] + "错误！").toNode());
                } else {
                    idList[i] = Integer.valueOf(split[i]);
                }
            }

            PayBank payBank = PayBank.valueOf(payOrg);

            List<Order> orderList = new ArrayList<Order>(idList.length);
            for (int id : idList) {
                Order order = orderService.getOrderById(id);
                if (order == null) {
                    Logger.warn("订单支付出现异常:订单不存在，订单id：" + id);
                    return ok(new JsonResult(false,"要支付的订单不存在！").toNode());
                }
                long orderNo = order.getOrderNo();
                if (!order.getOrderState().waitPay(TradePayType.valueOf(payType))) {
                    Logger.warn("订单支付出现异常:" + "订单(" + orderNo + ")不支持付款操作！");
                    return ok(new JsonResult(false,"订单(" + orderNo + ")不支持付款操作！").toNode());
                }
                if (verifyOrderItem(order)) {
                    Logger.warn("订单：" + order.getOrderNo() + "已售完或已下架或已移除，不能再购买");
                    return ok(new JsonResult(false,"订单：" + order.getOrderNo() + "中商品已售完或已下架或已移除，不能再购买").toNode());
                }
                order.setAccountType(curUser.getAccountType());
                order.setPayType(TradePayType.valueOf(payType));
                order.setPayBank(payBank);

                orderList.add(order);
            }
            //交易号
            String tradeNo = TradeSequenceUtil.getTradeNo();
            //处理交易信息
            tradeService.submitTradeOrderProcess(tradeNo, orderList, payMethodEnum);
            return ok(new JsonResult(true,"生成交易成功",tradeNo).toNode());
        } catch (Exception e) {
            Logger.error("用户" + curUserName + "订单支付在提交第三方支付前发生异常，其提交的订单编号如下：" + orderIds, e);
            return ok(new JsonResult(false,"订单支付失败，请联系商城客服人员！").toNode());
        }
    }

    /**
     * 提交订单：订单支付
     * @param payMethod
     * @param payOrg
     * @param orderIds
     * @param tradeNo
     * @return
     */
    @SecuredAction
    public Result toPayOrder(String payMethod, String payOrg, String orderIds, String tradeNo) {
        String[] split = orderIds.split(",");
        Integer[] idList = new Integer[split.length];
        for (int i = 0; i < split.length; i++) {
            if (!NumberUtils.isNumber(split[i])) {
                Logger.warn("订单支付出现异常:" + "订单号" + split[i] + "错误！");
                return ok(new JsonResult(false,"订单号" + split[i] + "错误！").toNode());
            } else {
                idList[i] = Integer.valueOf(split[i]);
            }
        }
        PayInfoWrapper payInfoWrapper = new PayInfoWrapper();
        payInfoWrapper.setTradeNo(tradeNo);
        //设置购买方式为order，订单
        payInfoWrapper.setBizType(BizType.Order.getName());

        //payInfoWrapper.setCallBackClass(OrderPayCallbackProcess.class); //ldj

        payInfoWrapper.setPayMethod(PayMethod.valueOf(payMethod));
        payInfoWrapper.setDefaultbank(payOrg);

        //默认为支付宝,主要用来更新支付方式
        PayBank bank = PayBank.Alipay;
        //目前设置默为阿里支付
        if (payInfoWrapper.isBank()) {
            bank = PayBank.valueOf(payInfoWrapper.getDefaultbank());
        } else {
            if(payMethod.equals(PayMethod.Tenpay.getName())) {
                bank = PayBank.Tenpay;
            }
        }
        Logger.info("--------------------真正的支付机构：" + bank.getValue() + ":" + bank.getName() + ":" + bank.getForexBankName());
        payInfoWrapper.setDefaultbank(bank.getForexBankName());

        List<Order> orderList = new ArrayList<Order>(idList.length);
        for (int id : idList) {
            Order order = orderService.getOrderById(id);
            orderList.add(order);
        }
        //订单总金额，显示在支付宝收银台里的“应付总额”里
        long totalFee = this.getPayMoneyForCent(orderList);

        payInfoWrapper.setTotalFee(totalFee);

        PayRequestHandler payService = PaymentManager.getPayRequestHandler(PayMethod.valueOf(payMethod));
        String form = payService.forwardToPay(payInfoWrapper);
        return ok(orderToPay.render(form));
    }

    /**
     * 按照支付订单号列表重新计算支付总金额，单位分
     * @param orderList
     * @return
     */
    private long getPayMoneyForCent(List<Order> orderList) {
        Money money = Money.valueOf(0);
        for (Order order : orderList) {
            money = money.add(order.getTotalMoney());
        }
        return money.getCent();
    }

    /**
     * 验证订单项
     * @param order
     * @return
     */
    private boolean verifyOrderItem(Order order) {
        List<OrderItem> orderItemList = orderService.queryOrderItemsByOrderId(order.getId());
        boolean flag = false;
        for (OrderItem orderItem : orderItemList) {
            if (!skuAndStorageService.isSkuUsable(orderItem.getSkuId())) {
                flag = true;
                break;
            }
        }
        return flag;
    }
}