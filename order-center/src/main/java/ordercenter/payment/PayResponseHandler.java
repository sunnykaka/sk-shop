package ordercenter.payment;

import common.utils.ParamUtils;
import common.utils.play.BaseGlobal;
import ordercenter.models.Trade;
import ordercenter.models.TradeOrder;
import ordercenter.payment.alipay.AlipayInfoBuilder;
import ordercenter.payment.constants.PayMethod;
import ordercenter.payment.constants.PayType;
import ordercenter.payment.constants.ResponseType;
import ordercenter.payment.tenpay.TenpayInfoBuilder;
import ordercenter.services.OrderPayCallbackProcess;
import ordercenter.services.TradeService;
import play.Logger;
import play.mvc.Http.Request;
import usercenter.models.User;
import usercenter.utils.SessionUtils;

import java.util.List;
import java.util.Map;

/**
 * 支付响应类
 * User: lidujun
 * Date: 2015-04-29
 */
public class PayResponseHandler {
    private Request request;

    /**
     * 订单交易记录
     */
    private TradeOrder tradeOrder;

    /**
     * 交易信息
     */
    private Trade trade;

    private BackInfoBuilder builder;

    /**
     * 所有返回的参数，原样封装
     */
    private Map<String, String> backParams;

    public PayResponseHandler(Request request) {
        this.request = request;
    }

    /**
     * 回调业务处理
     *
     * 下面内容是第三方支付成功，后续的交易处理，对于客户以下内容即便出现异常，对客户而言都已经支付成功！异常记录在日志/logs/trade.log中，需要运营手动确认相关异常内容
     * @param type 处理类型
     * @return 处理返回结果
     */
    public CallBackResult handleCallback(ResponseType type) {
        //初始化相关变量
        CallBackResult result = new CallBackResult();

        //尚客系统交易号
        String tradeNoStr = ParamUtils.getByKey(request, "out_trade_no");
        if(tradeNoStr == null || tradeNoStr.trim().length() == 0) {
            Logger.error("「订单回调」支付系统没有返回尚客系统交易号：" + tradeNoStr);
            result.setResult(false);
            return result;
        }

        TradeService tradeService = BaseGlobal.ctx.getBean(TradeService.class);
        List<TradeOrder> tradeOrderList = tradeService.getTradeOrdeByTradeNo(tradeNoStr);
        PayMethod payMethod = null;
        if(tradeOrderList == null || tradeOrderList.size() == 0) {
            Logger.error("「订单回调」系统中找不到交易信息！，此交易号号为：" + tradeNoStr);
            result.setResult(false);
            return result;
        } else {
            //现在只对一个订单进行支付
            tradeOrder = tradeOrderList.get(0);
            payMethod = tradeOrder.getPayMethod();

            //设置支付结束后处理类对象，不同的支付方式此处的值不同
            if(PayMethod.directPay.getName().equals(payMethod.getName())
                    || PayMethod.bankPay.getName().equals(payMethod.getName())) { //支付宝和银行卡(银行，目前也是通过支付宝来实现)
                this.builder = new AlipayInfoBuilder();
            } else  if(PayMethod.Tenpay.getName().equals(payMethod.getName())
                    || PayMethod.WXSM.getName().equals(payMethod.getName()) ) {
                this.builder = new TenpayInfoBuilder();
            } else {
                Logger.error("「订单回调」订单支付成功，但是系统中找不到此支付方式！，此交易号号为：" + tradeNoStr);
                result.setResult(false);
                return result;
            }
        }

        this.trade = this.builder.buildFromRequest(request);
        this.backParams = this.builder.buildParam(request);

        boolean signVerifyFlag = trade.verify(this.backParams, type);
        Logger.info("交易号号为：" + tradeNoStr + "----签名认证结果------------:" + signVerifyFlag);

        if (!signVerifyFlag) {
            Logger.error("回调签名验证出错: " + backParams);
            result.setResult(false);
            return result;
        }

        if (!trade.isSuccess()) {
            Logger.error("交易不成功，状态为failure: " + backParams);
            result.setResult(false);
            return result;
        }

        User user = SessionUtils.currentUser();
        if(user != null) {
            trade.setOuterBuyerId(user.getId() + "");
            trade.setOuterBuyerAccount(user.getUserName());
        }

        trade.setTradeOrder(tradeOrderList);

        if(this.tradeOrder != null && payMethod != null) {
            this.trade.setDefaultbank(tradeOrder.getDefaultPayOrg());
            this.trade.setPayTotalFee(tradeOrder.getPayTotalFee());
            this.trade.setBizType(tradeOrder.getBizType().getName());
            this.trade.setPayMethod(tradeOrder.getPayMethod().getName());

            if(PayMethod.WXSM.getName().equals(payMethod.getName())) {
                this.trade.setOuterPlatformType(PayType.WXSM.getValue());
            }
        }

        Logger.info("支付返回参数以及类型信息: " + type.getValue() + " : " + backParams);

        PayCallback callBackHandler;
        try {
            callBackHandler = new OrderPayCallbackProcess();
            result = callBackHandler.initResult(this.trade, type);
        } catch (Exception e) {
            Logger.error("支付成功，但是后续操作出现问题：实例化回调处理类时失败,TradeNo:" + trade.getOuterTradeNo(), e);
            result.setResult(true);
            return result;
        }

        //查询支付宝等发送过来的消息是否已经消费,如果已经消费则直接返回成功,主要针对情况是：return 和 Notify有很明显时差时，一个先回来，一另后回来。则直接处理成功。
        if (tradeService.existTrade(trade.getTradeNo(), trade.getOuterTradeNo())) {
            Logger.info("此笔交易已经处理,TradeNo:" + trade.getTradeNo() + " 本次通知类型为：" + type);
            result.setResult(true);
            return result;
        } else {
            //插入交易日志，当return 和 Notify有一个先回来的时候，插入已经成功，另外一个回来的时候则会抛出EntityExistsException,也视为成功
            //此处主要针对的是return notify并发执行的问题（同时回来接收处理）
            try {
                tradeService.createTrade(trade); //特意没有与doAfterBack中的内容放在一个事物中，即便后续操作发生问题，我们也要记录交易信息
            } catch (Exception e) {
                if(e.getCause() instanceof org.hibernate.exception.ConstraintViolationException ) {
                    Logger.info("此笔交易遇到并发情况，return notify 同时到达, TradeNo:" + trade.getTradeNo() + ", 本次通知类型为：" + type, e);
                } else {
                    Logger.error("需要运营人员手动确认：第三方交易成功，但是后续操作异常,交易号为[" + trade.getTradeNo() + "]-生成交易记录时发生异常", e);
                }
                result.setResult(true);
            }

            try {
                result.setResult(callBackHandler.doAfterBack(this.trade, type, result));
            } catch (Exception e) {
                Logger.error("需要运营人员手动确认：第三方交易成功，但是后续操作异常,交易号为[" + trade.getTradeNo() + "]-更新订单状态、扣减库存发生异常", e);
                result.setResult(true);
            }
        }

        if (result.success()) {
            Logger.info(buildLogString("success-" + type.getValue(), "normal", trade.getTradeNo(), trade.getOuterTradeNo(), trade.getOuterPlatformType()));
        } else {
            Logger.error(buildLogString("fail-" + type.getValue(), "exception", trade.getTradeNo(), trade.getOuterTradeNo(), trade.getOuterPlatformType()));
        }
        return result;
    }

    private String buildLogString(String successFlag, String type, String tradeNo, String outerTradeNo, String outerPlatformType) {
        StringBuilder sb = new StringBuilder();
        sb.append(successFlag);
        sb.append(":");
        sb.append(type);
        sb.append(":");
        sb.append("tradeNo=").append(tradeNo);
        sb.append(" outerTradeNo=").append(outerTradeNo);
        sb.append(" outerPlatformType=").append(outerPlatformType);
        return sb.toString();
    }

}
