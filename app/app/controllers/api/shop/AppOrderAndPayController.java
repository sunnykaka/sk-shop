package controllers.api.shop;

import com.fasterxml.jackson.databind.JsonNode;
import common.exceptions.AppBusinessException;
import common.exceptions.ErrorCode;
import common.utils.JsonUtils;
import controllers.BaseController;
import controllers.api.shop.payment.AppPayRequestHandler;
import controllers.api.shop.payment.tenpay.AppWeiXinPayRequestHandler;
import ordercenter.models.Trade;
import ordercenter.payment.constants.PayBank;
import ordercenter.services.OrderService;
import ordercenter.services.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import play.Logger;
import play.mvc.Result;
import productcenter.services.SkuAndStorageService;
import usercenter.constants.MarketChannel;
import usercenter.services.AddressService;
import utils.secure.SecuredAction;

import java.util.Arrays;
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

        MarketChannel channel = MarketChannel.fromLegacyClient(client);

        if(selItems.contains(",")) {
            selItems = selItems.replaceAll(",", "_");
        }

        List<Integer> orderIds = orderService.submitOrder(currentUser(), selItems, addressId, channel, null);

        Map<String, String> payInfoMap = submitToPay(payOrg, clientIp, orderIds);

        JsonNode jsonNode = JsonUtils.object2Node(payInfoMap);

        Logger.info("submitToPay方法返回结果：" + jsonNode.toString());

        return ok(jsonNode);
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

        List<Integer> orderIdList = Arrays.asList(orderIds.split("_")).stream().
                map(Integer::parseInt).collect(Collectors.toList());

        Map<String, String> payInfoMap = submitToPay(payOrg, clientIp, orderIdList);

        JsonNode jsonNode = JsonUtils.object2Node(payInfoMap);

        Logger.info("myOrderToPay方法返回结果：" + jsonNode.toString());

        return ok(jsonNode);

    }

    private Map<String, String> submitToPay(String payOrg, String clientIp, List<Integer> orderIds) {
        //去支付（产生交易和支付部分没做）
        Trade trade = tradeService.submitTradeOrder(this.currentUser(), orderIds, PayBank.valueOf(payOrg), MarketChannel.WEB, clientIp);

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
        return payInfoMap;
    }

}