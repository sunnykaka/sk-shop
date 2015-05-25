package ordercenter.payment;

import common.utils.ParamUtils;
import common.utils.play.BaseGlobal;
import ordercenter.models.Trade;
import ordercenter.payment.alipay.AlipayInfoBuilder;
import ordercenter.payment.constants.ResponseType;
import ordercenter.payment.tenpay.TenpayInfoBuilder;
import ordercenter.services.TradeService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import play.Logger;
import play.mvc.Http.Request;

import java.util.Map;

/**
 * 支付响应类
 * User: lidujun
 * Date: 2015-04-29
 */
public class PayResponseHandler {

    private static final char Log_Gap = ' ';

    //交易信息
    private Trade trade;
    //送过去的交易回调处理类
    private String callBackHandlerClass;

    private BackInfoBuilder builder;
    //所有返回的参数，原样封装
    private Map<String, String> backParams;

    public PayResponseHandler(Request request) {

        String extra_common_param = ParamUtils.getByKey(request, "extra_common_param");//阿里的支付回传参数

        String attach = ParamUtils.getByKey(request, "attach");//腾讯的回传参数

        if (StringUtils.isNotEmpty(extra_common_param)) {
            String[] split = extra_common_param.split("\\|");
            this.builder = new AlipayInfoBuilder();
            this.callBackHandlerClass = split[0].trim();
        }

        if (StringUtils.isNotEmpty(attach)) {
            this.builder = new TenpayInfoBuilder();
            this.callBackHandlerClass = attach.trim();
        }
        Validate.notNull(this.callBackHandlerClass, "没有定义支付回调处理接口类");
        this.trade = this.builder.buildFromRequest(request);
        this.backParams = this.builder.buildParam(request);
    }

    /**
     * 回调业务处理
     *
     * @param type 处理类型
     * @return 处理返回结果
     */
    public CallBackResult handleCallback(ResponseType type) {
        Logger.info("本次通知类型为：" + type.getValue()); //是阿里支付还是财付通

        //初始化相关变量
        CallBackResult result;
        PayCallback callBackHandler;
        try {
            Class<PayCallback> classz = (Class<PayCallback>) Class.forName(callBackHandlerClass);
            callBackHandler = classz.newInstance();
            result = callBackHandler.initResult(this.trade, type);
        } catch (ClassNotFoundException e) {
            Logger.error("未定义的支付返回处理类：" + callBackHandlerClass, e);
            throw new IllegalArgumentException("未定义的支付返回处理类", e);
        } catch (Exception e) {
            Logger.error("实例化回调处理类时失败", e);
            throw new IllegalStateException("实例化回调处理类时失败", e);
        }

        //MsgLogger 记录返回参数信息以及类型
        if (Logger.isInfoEnabled()) {
            Logger.info(type.getValue() + Log_Gap + backParams);
        }

        if (!trade.verify(backParams, type)) {
            Logger.error("回调签名验证出错: " + backParams);
            result.setResult(false);
            return result;
        }

        if (!trade.isSuccess()) {
            Logger.error("交易不成功，状态为failure: " + backParams);
            result.setResult(false);
            return result;
        }

        //下面内容是第三方支付成功，后续的交易处理，对于客户以下内容即便出现异常，对客户而言都已经支付成功！异常记录在日志/logs/trade.log中，需要运营手动确认相关异常内容
        TradeService tradeService = BaseGlobal.ctx.getBean(TradeService.class);
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
        sb.append(Log_Gap);
        sb.append(type);
        sb.append(Log_Gap);
        sb.append("tradeNo=").append(tradeNo);
        sb.append(" outerTradeNo=").append(outerTradeNo);
        sb.append(" outerPlatformType=").append(outerPlatformType);
        return sb.toString();
    }

}
