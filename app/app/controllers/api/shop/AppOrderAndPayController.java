package controllers.api.shop;

import com.google.common.collect.Lists;
import common.exceptions.AppBusinessException;
import common.exceptions.ErrorCode;
import common.utils.DateUtils;
import common.utils.JsonResult;
import common.utils.JsonUtils;
import common.utils.Money;
import controllers.BaseController;
import controllers.api.shop.payment.AppPayRequestHandler;
import controllers.api.shop.payment.tenpay.AppWeiXinPayRequestHandler;
import ordercenter.constants.BizType;
import ordercenter.constants.Client;
import ordercenter.constants.OrderState;
import ordercenter.constants.TradePayType;
import ordercenter.models.Cart;
import ordercenter.models.CartItem;
import ordercenter.models.Order;
import ordercenter.models.Trade;
import ordercenter.payment.constants.PayBank;
import ordercenter.payment.constants.PayMethod;
import ordercenter.services.CartService;
import ordercenter.services.OrderService;
import ordercenter.services.TradeService;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import play.Logger;
import play.mvc.Result;
import productcenter.services.SkuAndStorageService;
import usercenter.models.User;
import usercenter.models.address.Address;
import usercenter.services.AddressService;
import utils.secure.SecuredAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 订单及其支付Controller
 * User: lidujun
 * Date: 2015-08-31
 */
@org.springframework.stereotype.Controller
public class AppOrderAndPayController extends BaseController {

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
    public Result submitToPay(boolean isPromptlyPay, String selItems, int addressId, String payOrg,String client) { //device_info
        String clientIp = request().remoteAddress();
        Logger.info("进入submitToPay方法，参数：\n"
                + "isPromptlyPay=" + isPromptlyPay + "selItems=" + selItems + " addressId=" + addressId + " payOrg=" + payOrg + " clientIp=" + clientIp + " client=" + client);
        String curUserName = "";
        Cart cart = null;
        try {
            if (selItems == null || selItems.trim().length() == 0) {
                throw new AppBusinessException(ErrorCode.Conflict, "订单为空！");
            }

            if (payOrg == null || payOrg.trim().length() == 0) {
                return ok(new JsonResult(false, "没有选择支付机构！").toNode());
            }

            if (clientIp == null || clientIp.trim().length() == 0) {
                return ok(new JsonResult(false, "客户端IP地址不能为空！").toNode());
            }

            User curUser = this.currentUser();
            List<Integer> selCartItemIdList = null;

            if (isPromptlyPay) {  //立即购买
                int skuId = 0;
                int number = 0;
                try {
                    String[] split = selItems.split(":");
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

                cart = cartService.fakeCartForPromptlyPay(skuId, number);

            } else {
                selCartItemIdList = Lists.newArrayList(selItems.split(",")).stream().
                        map(Integer::parseInt).collect(Collectors.toList());
                cart = cartService.buildUserCartBySelItem(curUser.getId(), selCartItemIdList);
            }

            if (cart == null || cart.getNotDeleteCartItemList().size() == 0) {
                throw new AppBusinessException(ErrorCode.Conflict, "订单为空！");
            }

            //库存校验
            for (CartItem cartItem : cart.getNotDeleteCartItemList()) {
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


            Client clientParam = Client.valueOf(client);

            //生成订单、生成配送信息
            String orderIds = orderService.submitOrderProcess(selCartItemIdList, curUser, cart, address, clientParam);

            //去支付（产生交易和支付部分没做）
            PayBank payBank = PayBank.valueOf(payOrg);
            PayMethod payMethodEnum = payBank.getPayMethod();

            split = orderIds.split("_");
            List<Order> orderList = new ArrayList(split.length);
            for (int i = 0; i < split.length; i++) {
                int id = Integer.valueOf(split[i]);
                Order order = orderService.getOrderById(id);
                if (order == null) {
                    Logger.error("订单支付出现异常:订单不存在，订单id：" + id);
                    throw new AppBusinessException(ErrorCode.Conflict, "要支付的订单不存在！");
                }
                long orderNo = order.getOrderNo();

                if (!order.getOrderState().waitPay(TradePayType.OnLine)) {
                    if (order.getOrderState().getName().equals(OrderState.Cancel.getName())) {
                        Logger.error("订单支付出现异常:" + "订单(" + orderNo + ")已取消，请重新下单！");
                        throw new AppBusinessException(ErrorCode.Conflict, "订单(" + orderNo + ")已取消，请重新下单！");
                    } else {
                        Logger.error("订单支付出现异常:" + "订单(" + orderNo + ")已支付，请勿重复支付！");
                        throw new AppBusinessException(ErrorCode.Conflict, "订单(" + orderNo + ")已支付，请勿重复支付！");
                    }
                }
                //更新订单
                order.setAccountType(curUser.getAccountType());
                order.setPayType(TradePayType.OnLine);
                order.setPayBank(payBank);
                order.setUpdateTime(DateUtils.current());
                orderList.add(order);
            }

            /*
             * 1.计算出订单总金额
             * 2.创建交易
             * 3.保存tradeOrder信息
             * 4.创建trade,将交易的表单刷到页面
             * 5.生成与支付信息，并拼接返回app使用的签名数据
             */
            long totalFee = this.getPayMoneyForCent(orderList);
            Trade trade = Trade.TradeBuilder.createNewTrade(Money.valueOfCent(totalFee), BizType.Order, payBank, clientIp, clientParam);
            tradeService.submitTradeOrderProcess(trade.getTradeNo(), orderList, payMethodEnum);

            //现在只接入微信就这么写好了
            AppPayRequestHandler payReq = null;

            if("WXSM".equalsIgnoreCase(payOrg)) {
                payReq = new AppWeiXinPayRequestHandler();
            }

            if(payReq == null) {
                throw new AppBusinessException(ErrorCode.Conflict, "不支持此种支付类型！");
            }

            Map<String, String> payInfoMap = payReq.buildPayInfo(trade);
            if(payInfoMap == null) {
                throw new AppBusinessException(ErrorCode.Conflict, "微信预支付订单失败，请重试！");
            }
            return ok(JsonUtils.object2Node(payInfoMap));
        } catch (AppBusinessException e) {
            Logger.error("提交的订单在生成订单的过程中出现异常，其购物车信息：" + cart, e);
            throw e;
        } catch (Exception e) {
            Logger.error("提交的订单在生成订单的过程中出现异常，其购物车信息：" + cart, e);
            throw new AppBusinessException(ErrorCode.Conflict, "生成订单失败，请联系商城客服人员！");
        }
    }

    /**
     * 订单支付
     * @param orderIds 订单id
     * @param addressId 用户选择的寄送地址
     * @param payOrg 支付机构
     * @return
     */
    @SecuredAction
    public Result myOrderToPay(String orderIds, int addressId, String payOrg,String client) { //device_info
        String clientIp = request().remoteAddress();
        Logger.info("进入myOrderToPay方法，参数：\n"
                + "orderIds=" + orderIds + " addressId=" + addressId + " payOrg=" + payOrg + " clientIp=" + clientIp + " client=" + client);
        String curUserName = "";
        try {
            if (orderIds == null || orderIds.trim().length() == 0) {
                throw new AppBusinessException(ErrorCode.Conflict, "需要支付的订单为空！");
            }

            if (payOrg == null || payOrg.trim().length() == 0) {
                throw new AppBusinessException(ErrorCode.Conflict, "没有选择支付机构！");
            }

            if (clientIp == null || clientIp.trim().length() == 0) {
                throw new AppBusinessException(ErrorCode.Conflict, "客户端IP地址不能为空！");
            }

            User curUser = this.currentUser();
            curUserName = curUser.getUserName();

            String[] split = orderIds.split("_");
            Integer[] idList = new Integer[split.length];
            for (int i = 0; i < split.length; i++) {
                if (!NumberUtils.isNumber(split[i])) {
                    Logger.error("订单支付出现异常:" + "订单号" + split[i] + "错误！");
                    throw new AppBusinessException(ErrorCode.Conflict, "订单号" + split[i] + "错误！");
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
                    Logger.error("订单支付出现异常:订单不存在，订单id：" + id);
                    throw new AppBusinessException(ErrorCode.Conflict, "要支付的订单不存在！");
                }
                long orderNo = order.getOrderNo();

                if (!order.getOrderState().waitPay(TradePayType.OnLine)) {
                    if (order.getOrderState().getName().equals(OrderState.Cancel.getName())) {
                        Logger.error("订单支付出现异常:" + "订单(" + orderNo + ")已取消，请重新下单！");
                        throw new AppBusinessException(ErrorCode.Conflict, "订单(" + orderNo + ")已取消，请重新下单！");
                    } else {
                        Logger.error("订单支付出现异常:" + "订单(" + orderNo + ")已支付，请勿重复支付！");
                        throw new AppBusinessException(ErrorCode.Conflict, "订单(" + orderNo + ")已支付，请勿重复支付！");
                    }
                }

                //更新订单
                order.setAccountType(curUser.getAccountType());
                order.setPayType(TradePayType.OnLine);
                order.setPayBank(payBank);
                order.setUpdateTime(DateUtils.current());
                orderList.add(order);
            }

            Logger.info("校验订单信息通过，开始创建交易相关信息");

            //邮寄地址
            Address address = addressService.getAddress(addressId, curUser.getId());
            if (address == null) {
                throw new AppBusinessException(ErrorCode.Conflict, "您选择的订单寄送地址已经被修改，在系统中不存在！");
            }
            Client clientParam = Client.valueOf(client);

            /*
             * 1.计算出订单总金额
             * 2.创建交易
             * 3.保存tradeOrder信息
             * 4.创建trade,将交易的表单刷到页面
             * 5.生成与支付信息，并拼接返回app使用的签名数据
             */
            long totalFee = this.getPayMoneyForCent(orderList);
            Trade trade = Trade.TradeBuilder.createNewTrade(Money.valueOfCent(totalFee), BizType.Order, payBank, clientIp, clientParam);
            tradeService.submitTradeOrderProcess(trade.getTradeNo(), orderList, payMethodEnum);

            //现在只接入微信就这么写好了
            AppPayRequestHandler payReq = null;

            if("WXSM".equalsIgnoreCase(payOrg)) {
                payReq = new AppWeiXinPayRequestHandler();
            }

            if(payReq == null) {
                throw new AppBusinessException(ErrorCode.Conflict, "不支持此种支付类型！");
            }

            Map<String, String> payInfoMap = payReq.buildPayInfo(trade);
            if(payInfoMap == null) {
                throw new AppBusinessException(ErrorCode.Conflict, "微信预支付订单失败，请重试！");
            }
            return ok(JsonUtils.object2Node(payInfoMap));
        } catch (AppBusinessException e) {
            Logger.error(curUserName + "提交订单支付出现异常，信息：" , e);
            throw e;
        } catch (Exception e) {
            Logger.error(curUserName + "提交订单支付出现异常，信息：", e);
            throw new AppBusinessException(ErrorCode.Conflict, "提交订单支付失败，请联系商城客服人员！");
        }
    }

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