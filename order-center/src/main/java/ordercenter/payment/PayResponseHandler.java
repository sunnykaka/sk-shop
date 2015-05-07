package ordercenter.payment;

import ordercenter.payment.alipay.AlipayInfoBuilder;
import ordercenter.payment.constants.ResponseType;
import ordercenter.payment.models.TradeInfo;
import ordercenter.payment.tenpay.TenpayInfoBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import play.Logger;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 支付响应类
 * User: lidujun
 * Date: 2015-04-29
 */
public class PayResponseHandler {

    private static final char Log_Gap = ' ';

    //交易信息
    private TradeInfo tradeInfo;
    //送过去的交易回调处理类
    private String callBackHandlerClass;

    private BackInfoBuilder builder;
    //所有返回的参数，原样封装
    private Map<String, String> backParams;


    public PayResponseHandler(HttpServletRequest request) {
        String extra_common_param = request.getParameter("extra_common_param");//阿里的支付回传参数
        String attach = request.getParameter("attach");//腾讯的回传参数

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
        this.tradeInfo = this.builder.buildFromRequest(request);
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

        //实例化回调处理类，并准备好相关参数
        try {
            Class<PayCallback> classz = (Class<PayCallback>) Class.forName(callBackHandlerClass);
            callBackHandler = classz.newInstance();
            result = callBackHandler.initResult(this.tradeInfo, type);
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

        if (!tradeInfo.verify(backParams, type)) {
            Logger.error("回调签名验证出错: " + backParams);
            result.setResult(false);
            return result;
        }

        if (!tradeInfo.isSuccess()) {
            Logger.error("交易不成功，状态为failure: " + backParams);
            result.setResult(false);
            return result;
        }

        //后续要放开
//        //查询支付宝发送过来的消息是否已经消费,如果已经消费则直接返回成功,主要针对情况是：return 和 Notify有很明显时差时，一个先回来，一另后回来。则直接处理成功。
//        if (ApplicationContextUtils.getBean(TradeCenterUserClient.class).existTradeInfo(tradeInfo.getTradeNo(), tradeInfo.getOuterTradeNo())) {
//            Logger.info("此笔交易已经处理,TradeNo:" + tradeInfo.getTradeNo() + " 本次通知类型为：" + type);
//            result.setResult(true);
//            return result;
//        }
//
//        //插入交易日志，当return 和 Notify有一个先回来的时候，插入已经成功，另外一个回来的时候则会抛出DuplicateKeyException,也视为成功
//        //此处主要针对的是return notify并发执行的问题（同时回来接收处理）
//        try {
//            ApplicationContextUtils.getBean(TradeRepository.class).createTradeInfo(tradeInfo);
//            result.setResult(callBackHandler.doAfterBack(this.tradeInfo, type, result));
//        } catch (DuplicateKeyException e) {
//            //视为成功，不需要做任务处理。
//            Logger.info("此笔交易遇到并发情况，return notify 同时到达, TradeNo:" + tradeInfo.getTradeNo() + ", 本次通知类型为：" + type);
//            result.setResult(true);
//        }

        if (result.success()) {
            Logger.info(buildLogString("success-" + type.getValue(), "normal", tradeInfo.getTradeNo(), tradeInfo.getOuterTradeNo(), tradeInfo.getOuterPlatformType()));
        } else {
            Logger.error(buildLogString("fail-" + type.getValue(), "exception", tradeInfo.getTradeNo(), tradeInfo.getOuterTradeNo(), tradeInfo.getOuterPlatformType()));
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
