package controllers.shop;

import common.utils.DateUtils;
import common.utils.JsonResult;
import common.utils.Money;
import ordercenter.constants.BizType;
import ordercenter.constants.OrderState;
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
import ordercenter.services.CartService;
import ordercenter.services.OrderService;
import ordercenter.services.TradeService;
import ordercenter.util.OrderNumberUtil;
import org.apache.commons.lang3.math.NumberUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import productcenter.services.SkuAndStorageService;
import usercenter.models.User;
import usercenter.models.address.Address;
import usercenter.services.AddressService;
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
    private CartService cartService;

    @Autowired
    SkuAndStorageService skuAndStorageService;

    @Autowired
    AddressService addressService;

    @Autowired
    TradeService tradeService;

    /**
     * 提交订单:生成订单
     * @param addressId 用户选择的寄送地址
     * @return
     */
    public Result submitOrder(int addressId) {
        String curUserName = "";
        Cart cart = null;
        try {
            //测试
            User curUser = new User();
            curUser.setId(14311);
            addressId = 7397;
            //测试

            //curUser = SessionUtils.currentUser();
            curUserName = curUser.getUserName();

            cart = cartService.getCartByUserId(curUser.getId());
            if (cart == null || cart.getCartItemList().size() == 0) {
                return ok(new JsonResult(false,"出现错误：购物车为空").toNode());
            }

            //邮寄地址
            Address address = addressService.getAddress(addressId, curUser.getId());
            if(address == null) {
                return ok(new JsonResult(false,"您选择的订单寄送地址已经被修改，在系统中不存在！").toNode());
            }

            List<CartItem> cartItems = cart.getCartItemList();

            Order order = new Order();
            order.setOrderNo(OrderNumberUtil.getOrderNo()); //生成订单号
            order.setUserId(curUser.getId());
            order.setUserName(curUser.getUserName());
            order.setTotalMoney(cart.getTotalMoney());
            order.setOrderState(OrderState.Create);

            DateTime curTime = DateUtils.current();
            order.setCreateTime(curTime);
            order.setMilliDate(curTime.getMillis());
            order.setIsDelete(false);
            order.setBrush(false);

            //orderService.submitOrderProcess(order, cart, cartItems, address);

            //order = orderService.getOrderById(order.getId());
            return ok(orderPlay.render(order));
        } catch (final Exception e) {
            Logger.error(curUserName + "提交的订单在生成订单的过程中出现异常，其购物车信息：" + cart, e);
            return ok(new JsonResult(false,"生成订单失败，请联系商城客服人员！").toNode());
        }
    }

    /**
     * 立即支付：订单支付
     * @param payType
     * @param payMethod
     * @param payOrg
     * @param orderNoList
     * @return
     */
    public Result toPayOrder(String payType, String payMethod, String payOrg, String orderNoList) {
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
        if(orderNoList == null || orderNoList.trim().length() == 0) {
            return ok(new JsonResult(false,"需要支付的订单为空！").toNode());
        }

        String curUserName = "";
        Cart cart = null;
        try {
            //测试
            User curUser = new User();
            curUser.setId(14311);
            //测试

            //User curUser = SessionUtils.currentUser();
            curUserName = curUser.getUserName();

            String[] split = orderNoList.split(",");
            Long[] list = new Long[split.length];
            for (int i = 0; i < split.length; i++) {
                if (!NumberUtils.isNumber(split[i])) {
                    Logger.warn("订单支付出现异常:" + "订单号" + split[i] + "错误！");
                    return ok(new JsonResult(false,"订单号" + split[i] + "错误！").toNode());
                } else {
                    list[i] = Long.valueOf(split[i]);
                }
            }

            PayInfoWrapper payInfoWrapper = new PayInfoWrapper();
            //设置购买方式为order，订单
            payInfoWrapper.setBizType(BizType.Order.getName());
            payInfoWrapper.setCallBackClass(OrderPayCallback.class);
            payInfoWrapper.setPayMethod(PayMethod.valueOf(payMethod));
            payInfoWrapper.setDefaultbank(payOrg);

            //默认为支付宝,主要用来更新支付方式
            PayBank bank = PayBank.Alipay;
            //目前设置默为阿里支付
            if (payInfoWrapper.isBank()) {
                bank = PayBank.valueOf(payInfoWrapper.getDefaultbank());
            } else {
                if(payMethod.equals(PayMethod.Tenpay)) {
                    bank = PayBank.Tenpay;
                }
            }
            Logger.info("///////////////////////////真正的支付机构：" + bank.getValue());
            payInfoWrapper.setDefaultbank(bank.getName());

            List<Order> orderList = new ArrayList<Order>(list.length);
            for (Long orderNo : list) {
                Order order = orderService.getOrderByOrderNo(orderNo);
                if (order == null) {
                    Logger.warn("订单支付出现异常:订单不存在:");
                    return ok(new JsonResult(false,"订单(" + orderNo + ")不存在！").toNode());
                }
                if (!order.getOrderState().waitPay(order.getPayType())) {
                    Logger.warn("订单支付出现异常:" + "订单(" + orderNo + ")不支持付款操作！");
                    return ok(new JsonResult(false,"订单(" + orderNo + ")不支持付款操作！").toNode());
                }
                if (verifyOrderItem(order)) {
                    Logger.warn("订单：" + order.getOrderNo() + "中商品已下架或移除");
                    return ok(new JsonResult(false,"订单：" + order.getOrderNo() + "中商品已下架或移除").toNode());
                }
                order.setAccountType(curUser.getAccountType());
                order.setPayType(TradePayType.valueOf(payType));
                order.setPayBank(bank);

                orderList.add(order);
            }
            //处理交易信息
            tradeService.submitTradeOrderProcess(payInfoWrapper, orderList);

            //订单总金额，显示在支付宝收银台里的“应付总额”里
            long totalFee = this.getPayMoneyForCent(list);
            payInfoWrapper.setTotalFee(totalFee);

            PayRequestHandler payService = PaymentManager.getPayRequestHandler(PayMethod.valueOf(payMethod));
            String form = payService.forwardToPay(payInfoWrapper);
            return ok(orderToPay.render(form));
        } catch (Exception e) {
            Logger.error("用户" + curUserName + "订单支付在提交第三方支付前发生异常，其提交的订单编号如下：" + orderNoList, e);
            return ok(new JsonResult(false,"订单支付失败，请联系商城客服人员！").toNode());
        }
    }

    /**
     * 按照支付订单号列表重新计算支付总金额
     * @param orderNoList
     * @return
     */
    private long getPayMoneyForCent(Long[] orderNoList) {
        Money money = Money.valueOf(0);
        for (long orderNo : orderNoList) {
            Order order = orderService.getOrderByOrderNo(orderNo);
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
            //如果是 付款减库存 则检测SKU
            if (orderItem.getStoreStrategy().isPayStrategy()) {
                if (!skuAndStorageService.isSkuUsable(orderItem.getSkuId())) {
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }
}