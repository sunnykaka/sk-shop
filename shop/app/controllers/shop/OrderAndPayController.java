package controllers.shop;

import common.utils.JsonResult;
import common.utils.Money;
import controllers.user.*;
import ordercenter.constants.BizType;
import ordercenter.models.Order;
import ordercenter.models.Trade;
import ordercenter.payment.PayRequestHandler;
import ordercenter.payment.constants.PayBank;
import ordercenter.services.CartService;
import ordercenter.services.OrderService;
import ordercenter.services.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import productcenter.services.SkuAndStorageService;
import usercenter.constants.MarketChannel;
import usercenter.models.User;
import usercenter.services.AddressService;
import usercenter.utils.SessionUtils;
import utils.secure.SecuredAction;
import views.html.shop.orderPlay;
import views.html.shop.orderToPay;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


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
        Trade trade = Trade.TradeBuilder.createNewTrade(Money.valueOfCent(1L), BizType.Order, PayBank.Alipay, null, MarketChannel.WEB);
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
    public Result submitOrder(String selItems, int addressId, boolean isPromptlyPay, List<String> vouchers) {

        //生成订单相关信息
        List<Integer> orderIds = orderService.submitOrder(SessionUtils.currentUser(), selItems, addressId, MarketChannel.WEB, vouchers);

        if(orderService.needToPay(orderIds)) {

            String orderIdsStr = String.join("_", orderIds.stream().map(String::valueOf).collect(Collectors.toList()));
            return ok(new JsonResult(true, "生成订单成功", orderIdsStr).toNode());

        } else {

            return ok(new JsonResult(true, "生成订单成功", controllers.user.routes.MyOrderController.index(0, 1, 10).url()).toNode());
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

        List<String> orderIdList = Arrays.asList(orderIds.split("_"));
        List<Order> orders = orderIdList.stream().
                map(orderId -> orderService.getOrderById(Integer.parseInt(orderId), curUser.getId())).
                filter(order -> order != null).collect(Collectors.toList());

        orderIdList = orders.stream().map(order -> String.valueOf(order.getId())).collect(Collectors.toList());
        String orderIdsStr = String.join("_", orderIdList);
        Money totalMoney = orders.stream().map(Order::getTotalMoney).reduce(Money.valueOf(0d), Money::add);

        return ok(orderPlay.render(orderIdsStr, totalMoney));
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
        String clientIp = request().remoteAddress();

        tradeLogger.info("开始校验订单信息，订单ids:" + orderIds);

        try {
            User user = SessionUtils.currentUser();

            List<Integer> orderIdList = Arrays.asList(orderIds.split("_")).stream().
                    map(Integer::parseInt).collect(Collectors.toList());

            Trade trade = tradeService.submitTradeOrder(user, orderIdList, PayBank.valueOf(payOrg), MarketChannel.WEB, clientIp);

            PayRequestHandler handler = PayBank.valueOf(trade.getDefaultbank()).getPayMethod().getPayRequestHandler();
            String form = handler.forwardToPay(trade);

            tradeLogger.info("交易信息创建完成，tradeNo = " + trade.getTradeNo());

            PayFormVO formVO = new PayFormVO();
            formVO.setForm(form);
            formVO.setTradeNo(trade.getTradeNo());

            return ok(new JsonResult(true, "生成交易成功", formVO).toNode());
        } catch (Exception e) {
            Logger.error("订单支付在提交第三方支付前发生异常，其提交的订单编号如下：" + orderIds, e);
            return ok(new JsonResult(false, "订单支付失败，请联系商城客服人员！").toNode());
        }
    }

}