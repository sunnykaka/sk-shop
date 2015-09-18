package controllers.api.shop;

import common.exceptions.AppBusinessException;
import common.exceptions.ErrorCode;
import controllers.BaseController;
import controllers.api.shop.payment.AppPayResponseHandler;
import controllers.api.shop.payment.AppTenpayUtils;
import ordercenter.models.Trade;
import ordercenter.payment.CallBackResult;
import ordercenter.payment.constants.ResponseType;
import ordercenter.payment.constants.TradeStatus;
import ordercenter.services.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import play.Logger;
import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;
import utils.secure.SecuredAction;

import java.util.Map;
import java.util.TreeMap;

/**
 * 支付通用控制器类
* User: lidujun
* Date: 2015-09-09
*/
@org.springframework.stereotype.Controller
public class OrderPayCallBackAppController extends BaseController {

    @Autowired
    TradeService tradeService;

    private Map<String, String> parseRetXml(String way) {
        //Logger.info(way + "支付平台返回的数据 : \n");
        Map<String, String> xmlMap = new TreeMap<String, String>();
        Node xmlNode = Http.Context.current().request().body().asXml().getElementsByTagName("xml").item(0);
        if(xmlNode != null) {
            NodeList nodeList = xmlNode.getChildNodes();
            if(nodeList != null && nodeList.getLength() > 0) {
                for(int i=0; i < nodeList.getLength(); i++) {
                    Node item = nodeList.item(i);
                    if(item != null && !"#text".equalsIgnoreCase(item.getNodeName())) {
                        xmlMap.put(item.getNodeName(), item.getTextContent());
                    }
                }
            }
        }
        Logger.info(way + "支付平台返回的数据 : " + xmlMap);
        return xmlMap;
    }

    /**
     * 支付有通知信息返回
     * @return
     */
    @BodyParser.Of(BodyParser.Xml.class)
    public Result payNotify() {
        Map<String, String> retMap = parseRetXml("payNotify");
        CallBackResult result = null;
        if("SUCCESS".equalsIgnoreCase(retMap.get("return_code"))
                &&  "SUCCESS".equalsIgnoreCase(retMap.get("result_code"))) { //此处是不是真正支付成功？
            //签名验证
            if(AppTenpayUtils.verify(retMap)) {
                Logger.error("交易成功！");
                AppPayResponseHandler appRes = new AppPayResponseHandler(retMap);
                result = appRes.handleCallback(ResponseType.NOTIFY);
            }
        } else {
            Logger.info("错误代码:" + retMap.get("err_code"));
            Logger.info("错误代码描述:" + retMap.get("err_code_des"));
        }

        //告诉微信服务器，我收到信息了，不要在调用回调action
        if(result == null || !result.success()) {
            return ok(AppTenpayUtils.setReturnInfoXml("FAIL", ""));
        } else {
            return ok(AppTenpayUtils.setReturnInfoXml("SUCCESS", ""));
        }
    }

    /**
     * 检查支付情况
     * @return
     */
    @SecuredAction
    public Result checkPayState(String tradeNo) {
        Logger.info("订单交易号: " + tradeNo);
        try {
            if(tradeNo == null || tradeNo.trim().length() == 0) {
                throw new AppBusinessException(ErrorCode.Conflict, "用户订单交易号不存在！");
            }
            Trade traderade = tradeService.getTradeByTradeNo(tradeNo);
            if(traderade == null) {
                throw new AppBusinessException(ErrorCode.Conflict, "未支付成功。若已付款，请联系商城客服人员!");
            } else {
                String state = traderade.getTradeStatus();
                //财富通(感觉有问题但是以后再优化)
                if(TradeStatus.TRADE_SUCCESS.toString().equalsIgnoreCase(state)) {
                    return noContent(); //此种情况应该比对日志看是什么原因造成的
                } else {
                   //查询订单
                    Map<String, String> payResult = AppTenpayUtils.searchPayResult(traderade);
                    if(payResult != null && "SUCCESS".equalsIgnoreCase(payResult.get("trade_state"))) {
                        return noContent();
                    } else {
                        throw new AppBusinessException(ErrorCode.Conflict, "未支付成功，请重新支付!");
                    }
                }
            }
        } catch (Exception e) {
            Logger.error("-----------检查支付情况出现异常，其订单交易号：" + tradeNo, e);
            throw new AppBusinessException(ErrorCode.Conflict, "检查支付情况出现异常，请联系商城客服人员！");
        }
    }

}
