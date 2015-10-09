package controllers.shop;

import common.utils.DateUtils;
import common.utils.JsonResult;
import common.utils.Money;
import ordercenter.constants.BizType;
import ordercenter.constants.Client;
import ordercenter.constants.OrderState;
import ordercenter.constants.TradePayType;
import ordercenter.models.Cart;
import ordercenter.models.CartItem;
import ordercenter.models.Order;
import ordercenter.models.Trade;
import ordercenter.payment.PayRequestHandler;
import ordercenter.payment.constants.PayBank;
import ordercenter.payment.constants.PayMethod;
import ordercenter.services.CartService;
import ordercenter.services.OrderService;
import ordercenter.services.TradeService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
    CartService cartService;

    private static final Logger.ALogger tradeLogger = Logger.of("tradeRequest");

    /**
     * 用于测试支付1分钱
     *
     * @return
     */
    public Result testPay() {
        Trade trade = Trade.TradeBuilder.createNewTrade(Money.valueOfCent(1L), BizType.Order, PayBank.Alipay, null, Client.Browser);
        PayRequestHandler handler = PayBank.valueOf(trade.getDefaultbank()).getPayMethod().getPayRequestHandler();
        String form = handler.forwardToPay(trade);
        return ok(orderToPay.render(form));
    }


    /**
     * 提交订单-生成订单
     *
     * @param addressId 用户选择的寄送地址
     * @return
     */
    @SecuredAction
    public Result submitOrder(String selItems, int addressId, boolean isPromptlyPay) {
        String curUserName = "";
        Cart cart = null;
        try {
            if (selItems == null || selItems.trim().length() == 0) {
                return ok(new JsonResult(false, "订单为空！").toNode());
            }

            User curUser = SessionUtils.currentUser();
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
                    return ok(new JsonResult(false, "立即购买商品有问题，请核对一下").toNode());
                }

                if (skuId <= 0) {
                    return ok(new JsonResult(false, "在系统中找不到商品！").toNode());
                }

                if (number <= 0) {
                    return ok(new JsonResult(false, "至少要购买1件商品！").toNode());
                }

                cart = new Cart();
                CartItem cartItem = new CartItem();
                cartItem.setSkuId(skuId);
                cartItem.setNumber(number);

                Money totalMoney = Money.valueOf(0);
                totalMoney = cartService.setCartItemValues(cartItem);
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
                cart = cartService.buildUserCartBySelItem(curUser.getId(), selItems);
            }

            if (cart == null || cart.getCartItemList().size() == 0) {
                return ok(new JsonResult(false, "订单为空").toNode());
            }

            //库存校验
            for (CartItem cartItem : cart.getCartItemList()) {
                if (!skuAndStorageService.isSkuUsable(cartItem.getSkuId())) {
                    Logger.warn("商品：" + cartItem.getProductName() + "已售罄或已下架或已移除，不能再购买");
                    return ok(new JsonResult(false, "商品：" + cartItem.getProductName() + "已售罄或已下架，不能再购买").toNode());
                }
            }

            //邮寄地址
            Address address = addressService.getAddress(addressId, curUser.getId());
            if (address == null) {
                return ok(new JsonResult(false, "您选择的订单寄送地址已经被修改，在系统中不存在！").toNode());
            }

            //生成订单相关信息
            String orderIds = orderService.submitOrderProcess(selItems, isPromptlyPay, curUser, cart, address, Client.Browser);
            return ok(new JsonResult(true, "生成订单成功", orderIds).toNode());
        } catch (Exception e) {
            Logger.error(curUserName + "提交的订单在生成订单的过程中出现异常，其购物车信息：" + cart, e);
            return ok(new JsonResult(false, "生成订单失败，请联系商城客服人员！").toNode());
        }
    }

    /**
     * 提交订单-选择支付方式(生成订单)
     *
     * @param orderIds 用户的订单
     * @return
     */
    @SecuredAction
    public Result toOrderPlay(String orderIds) {
        User curUser = SessionUtils.currentUser();

        String[] split = null;
        split = orderIds.split("_");
        //拆出来的订单不会很多，一般不会超过10个，所以采用for循环获取订单的方式
        StringBuilder orderIdsSb = new StringBuilder();
        Money totalMoney = Money.valueOf(0);
        for (int i = 0; i < split.length; i++) {
            int orderId = Integer.valueOf(split[i]);
            Order order = orderService.getOrderById(orderId, curUser.getId());
            if (order != null) {
                if (orderIdsSb.length() > 0) {
                    orderIdsSb.append("_");
                }
                orderIdsSb.append(orderId);
                totalMoney = totalMoney.add(order.getTotalMoney());
            }
        }

        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("orderIds", orderIdsSb.toString());
        resultMap.put("totalMoney", totalMoney);
        return ok(orderPlay.render(resultMap));
    }

    /**
     * 提交订单-验证并生成交易记录
     *
     * @param payType
     * @param payMethod
     * @param payOrg
     * @param orderIds
     * @return
     */
    @SecuredAction
    public Result submitTradeOrder(String payType, String payMethod, String payOrg, String orderIds) {

        tradeLogger.info("开始校验订单信息，订单ids:" + orderIds);

        if (payOrg == null || payOrg.trim().length() == 0) {
            return ok(new JsonResult(false, "没有选择支付机构！").toNode());
        }
        if (orderIds == null || orderIds.trim().length() == 0) {
            return ok(new JsonResult(false, "需要支付的订单为空！").toNode());
        }

        String curUserName = "";
        try {
            User curUser = SessionUtils.currentUser();
            curUserName = curUser.getUserName();


            String[] split = orderIds.split("_");
            Integer[] idList = new Integer[split.length];
            for (int i = 0; i < split.length; i++) {
                if (!NumberUtils.isNumber(split[i])) {
                    tradeLogger.error("订单支付出现异常:" + "订单号" + split[i] + "错误！");
                    return ok(new JsonResult(false, "订单号" + split[i] + "错误！").toNode());
                } else {
                    idList[i] = Integer.valueOf(split[i]);
                }
            }

            PayBank payBank = PayBank.valueOf(payOrg);
            PayMethod payMethodEnum = payBank.getPayMethod();

            List<Order> orderList = new ArrayList<Order>(idList.length);


            for (int id : idList) {
                Order order = orderService.getOrderById(id);
                if (order == null) {
                    tradeLogger.error("订单支付出现异常:订单不存在，订单id：" + id);
                    return ok(new JsonResult(false, "要支付的订单不存在！").toNode());
                }
                long orderNo = order.getOrderNo();

                if (!order.getOrderState().waitPay(TradePayType.OnLine)) {
                    if (order.getOrderState().getName().equals(OrderState.Cancel.getName())) {
                        tradeLogger.error("订单支付出现异常:" + "订单(" + orderNo + ")已取消，请重新下单！");
                        return ok(new JsonResult(false, "订单(" + orderNo + ")已取消，请重新下单！").toNode());
                    } else {
                        tradeLogger.error("订单支付出现异常:" + "订单(" + orderNo + ")已支付，请勿重复支付！");
                        return ok(new JsonResult(false, "订单(" + orderNo + ")已支付，请勿重复支付！").toNode());
                    }
                }

                //更新订单
                order.setAccountType(curUser.getAccountType());
                order.setPayType(TradePayType.OnLine);
                order.setPayBank(payBank);
                order.setUpdateTime(DateUtils.current());
                orderList.add(order);
            }

            tradeLogger.info("校验订单信息通过，开始创建交易相关信息");

//            //交易号
//            String tradeNo = TradeSequenceUtil.getTradeNo();

            /**
             * 1.计算出订单总金额
             * 2.创建交易
             * 3.保存tradeOrder信息
             * 4.创建trade,将交易的表单刷到页面
             * 5.跳转去第三方支付平台付款
             */
            long totalFee = this.getPayMoneyForCent(orderList);
            Trade trade = Trade.TradeBuilder.createNewTrade(Money.valueOfCent(totalFee), BizType.Order, payBank, null, Client.Browser);

            tradeService.submitTradeOrderProcess(trade.getTradeNo(), orderList, payMethodEnum);

            PayRequestHandler handler = PayBank.valueOf(trade.getDefaultbank()).getPayMethod().getPayRequestHandler();
            String form = handler.forwardToPay(trade);

            tradeLogger.info("交易信息创建完成，tradeNo = " + trade.getTradeNo());

            PayFormVO formVO = new PayFormVO();
            formVO.setForm(form);
            formVO.setTradeNo(trade.getTradeNo());

            return ok(new JsonResult(true, "生成交易成功", formVO).toNode());
        } catch (Exception e) {
            Logger.error("用户" + curUserName + "订单支付在提交第三方支付前发生异常，其提交的订单编号如下：" + orderIds, e);
            return ok(new JsonResult(false, "订单支付失败，请联系商城客服人员！").toNode());
        }
    }

//    /**
//     * 提交订单：订单支付
//     * @deprecate
//     * @param payMethod
//     * @param payOrg
//     * @param orderIds
//     * @param tradeNo
//     * @return
//     */
//    @SecuredAction
//    public Result toPayOrder(String payMethod, String payOrg, String orderIds, String tradeNo) {
//        String[] split = orderIds.split("_");
//        List<Integer> idList = new ArrayList<Integer>();
//        for (int i = 0; i < split.length; i++) {
//            idList.add(Integer.valueOf(split[i]));
//        }
//        PayInfoWrapper payInfoWrapper = new PayInfoWrapper();
//        payInfoWrapper.setTradeNo(tradeNo);
//        //设置购买方式为order，订单
//        payInfoWrapper.setBizType(BizType.Order.getName());
//
//        payInfoWrapper.setPayMethod(PayMethod.valueOf(payMethod));
//        payInfoWrapper.setDefaultbank(payOrg);
//
//        //默认为支付宝,主要用来更新支付方式
//        PayBank bank = PayBank.Alipay;
//        //目前设置默为阿里支付
//        if (payInfoWrapper.isBank()) {
//            bank = PayBank.valueOf(payInfoWrapper.getDefaultbank());
//        } else {
//            if (payMethod.equals(PayMethod.Tenpay.getName()) || payMethod.equals(PayMethod.WXSM.getName())) {
//                bank = PayBank.Tenpay;
//                if (payMethod.equals(PayMethod.WXSM.getName())) {
//                    bank = PayBank.WXSM;
//                }
//                payInfoWrapper.setBuyerIP(request().remoteAddress());
//            }
//        }
//        Logger.info("--------------------真正的支付机构：" + bank.getValue() + ":" + bank.getName() + ":" + bank.toString());
//        payInfoWrapper.setDefaultbank(bank.toString());//TODO checkout
//
//        List<Order> orderList = new ArrayList<Order>(idList.size());
//        for (int id : idList) {
//            Order order = orderService.getOrderById(id);
//            orderList.add(order);
//        }
//        //订单总金额，显示在支付宝收银台里的“应付总额”里
//        long totalFee = this.getPayMoneyForCent(orderList);
//
//        payInfoWrapper.setTotalFee(totalFee);
//
//        PayRequestHandler payService = PaymentManager.getPayRequestHandler(PayMethod.valueOf(payMethod));
//        String form = ""; //todo   need revert payService.forwardToPay(payInfoWrapper);
//        return ok(orderToPay.render(form));
//    }

    /**
     * 按照支付订单号列表重新计算支付总金额，单位分
     *
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

}