package ordercenter.payment;

import common.utils.DateUtils;
import common.utils.ParamUtils;
import common.utils.play.BaseGlobal;
import ordercenter.constants.BizType;
import ordercenter.models.Trade;
import ordercenter.payment.constants.PayChannel;
import ordercenter.payment.constants.ResponseType;
import ordercenter.payment.constants.TradeStatus;
import ordercenter.services.TradeService;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.StaleObjectStateException;
import org.joda.time.DateTime;
import play.Logger;
import play.mvc.Http.Request;

import java.util.HashMap;
import java.util.Map;

/**
 * User: amos.zhou
 * Date: 13-10-24
 * Time: 上午11:52
 */
public class PayResponseHandler {

    private static final char Log_Gap = ' ';
    private static final Logger.ALogger logger = Logger.of(PayResponseHandler.class);
    private static final Logger.ALogger ALIPAY_LOGGER = Logger.of("alipayTrade");
    private static final Logger.ALogger TEMPAY_LOGGER = Logger.of("tenpayTrade");
    private static final Logger.ALogger TRADE_RETURN_LOGGER = Logger.of("TradeReturn");

    //交易信息
    private Trade tradeInfo;
    //送过去的交易回调处理类
    private Class<PayCallback> callBackHandlerClass;

    //所有返回的参数，原样封装
    private Map<String, String> backParams;

    private TradeService tradeService = BaseGlobal.ctx.getBean(TradeService.class);

    /**
     * 交易在第三方支付平台返回来之前的状态
     */
    private TradeStatus statusBeforeBack;

    //TODO 要不要从构造函数中移出？ 还有整个支付过程中异常处理，以及必要的日志记录
    public PayResponseHandler(Request request) {
        TRADE_RETURN_LOGGER.info("第三方平台返回数据：" + request.queryString());

        /**
         * 先从回调参数中取出交易号，并查出对应出交易记录
         */
        String tradeNo = ParamUtils.getByKey(request, "out_trade_no");
        if (StringUtils.isBlank(tradeNo)) {
            throw new IllegalArgumentException("交易不存在");
        }
        tradeInfo = tradeService.getTradeByTradeNo(tradeNo);
        statusBeforeBack = TradeStatus.valueOf(tradeInfo.getTradeStatus());

        if (tradeInfo == null) {
            throw new IllegalArgumentException("交易不存在");
        }

        /**
         * 接下来要从完request中提取信息完善request对象
         */
        completeTrade(request);
        this.callBackHandlerClass = BizType.valueOf(tradeInfo.getBizType()).getHandlerClass();
        this.backParams = buildParam(request);
    }


    /**
     * 从Request中提取参数信息，用于返回数据的参数校验
     *
     * @param request
     * @return
     */
    private Map<String, String> buildParam(Request request) {

        Map<String, String> params = new HashMap<String, String>();
        Map requestParams = request.queryString();

        //获取支付宝GET过来反馈信息
        for (Object oName : requestParams.keySet()) {
            String name = (String) oName;
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            /**
             * 只处理了支付宝和非支付宝（目前是财付通）的情况，
             * 如果继续增加其它支付平台要重构，else分支不一定能满足需要
             */
            if (PayChannel.Alipay.toString().equals(tradeInfo.getOuterPlatformType())) {
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i]
                            : valueStr + values[i] + ",";
                }
                params.put(name, valueStr);
            } else {
                if (values != null && values.length > 0)
                    params.put(name, values[0]);
            }
        }
        return params;

    }

    /**
     * 获取支付渠道，根据不平的支付平台，来完善trade信息
     */
    private void completeTrade(Request request) {

        /**
         * 支付宝的回调示例
         * http://www.fashiongeeker.com/payOrder/normalReturn?
         body=%E5%B0%9A%E5%AE%A2-11439965467302
         &buyer_email=amoszhou%40foxmail.com
         &buyer_id=2088512330246343
         &exterface=create_direct_pay_by_user
         &is_success=T
         &notify_id=RqPnCoPT3K9%252Fvwbh3InSP0RQZJPavnX%252BY7YVYoPsEW%252FI%252FyJu5RuYirbu1w4GKfoLTEpK
         &notify_time=2015-08-19+14%3A25%3A36
         &notify_type=trade_status_sync
         &out_trade_no=11439965467302
         &payment_type=1
         &seller_email=zhifubao%40yezaoshu.com
         &seller_id=2088021413357886
         &subject=%E5%B0%9A%E5%AE%A2-11439965467302
         &total_fee=0.01
         &trade_no=2015081900001000340062717009
         &trade_status=TRADE_SUCCESS
         &sign=bba41d7eee8dffc0656eaf29003c3021&sign_type=MD5
         */
        if (PayChannel.Alipay.toString().equals(tradeInfo.getOuterPlatformType())) {
            tradeInfo.setNotifyId(ParamUtils.getByKey(request, "notify_id"));
            tradeInfo.setNotifyType(ParamUtils.getByKey(request, "notify_type"));
            tradeInfo.setTradeGmtCreateTime(DateUtils.parseDateTime(ParamUtils.getByKey(request, "notify_time")));
            tradeInfo.setOuterBuyerAccount(ParamUtils.getByKey(request, "buyer_email"));
            tradeInfo.setOuterBuyerId(ParamUtils.getByKey(request, "buyer_id"));
            tradeInfo.setOuterTradeNo(ParamUtils.getByKey(request, "trade_no"));
            tradeInfo.setGmtModifyTime(new DateTime());
            tradeInfo.setPayRetTotalFee(ParamUtils.getByKey(request, "total_fee"));
            /**
             * 支付状态都是以支付宝为准的，所以就不需要再转换成枚举了，直接存进去就好
             */
            tradeInfo.setTradeStatus(ParamUtils.getByKey(request, "trade_status"));
        }

        if (PayChannel.TenPay.toString().equals(tradeInfo.getOuterPlatformType()) ||
                PayChannel.WXSM.toString().equals(tradeInfo.getOuterPlatformType())) {
            tradeInfo.setNotifyId(ParamUtils.getByKey(request, "notify_id"));
            tradeInfo.setNotifyType("redirect");
            tradeInfo.setTradeGmtCreateTime(DateUtils.parseDateTime(ParamUtils.getByKey(request, "time_end")));
//            tradeInfo.setOuterBuyerAccount(""); 财付通没有这个字段，无法返回
            tradeInfo.setOuterBuyerId(ParamUtils.getByKey(request, "buyer_alias"));  //TODO 64位，数据库长度要修改
            tradeInfo.setOuterTradeNo(ParamUtils.getByKey(request, "transaction_id"));
            tradeInfo.setGmtModifyTime(new DateTime());
            tradeInfo.setPayRetTotalFee(ParamUtils.getByKey(request, "total_fee"));
            /**
             * 支付状态都是以支付宝为准的，所以就不需要再转换成枚举了，直接存进去就好
             */
            tradeInfo.setTradeStatus(ParamUtils.getByKey(request, "trade_state"));
        }


    }

    /**
     * 区分业务类型，将不同的业务写入不同的日志中，方便查看与比对。
     *
     * @return
     */
    private Logger.ALogger getLogger() {
        if (PayChannel.Alipay.toString().equals(tradeInfo.getOuterPlatformType())) {  //阿里支付的
            return ALIPAY_LOGGER;
        } else {//财付通支付的
            return TEMPAY_LOGGER;
        }
    }

    /**
     * 回调业务处理
     *
     * @param type 处理类型
     * @return 处理返回结果
     */
    public CallBackResult handleCallback(ResponseType type) {
        Logger.ALogger msgLog = getLogger(); //是阿里支付还是财付通,分开写日志
        msgLog.info("本次通知类型为：" + type.getValue());
        msgLog.info("本次交易对象：" + tradeInfo);

        //初始化相关变量
        CallBackResult result;
        PayCallback callBackHandler;

        //实例化回调处理类，并准备好相关参数
        try {
            callBackHandler = callBackHandlerClass.newInstance();
            result = callBackHandler.initResult(this.tradeInfo, type);
        } catch (Exception e) {
            msgLog.error("初始化回调处理类失败");
            throw new IllegalArgumentException("初始化回调处理类失败", e);
        }


        /**
         * 校验回调参数失败
         */
        if (!tradeInfo.verify(backParams, type)) {
            msgLog.error("回调签名验证出错: " + backParams);
            result.setResult(false);
            return result;
        }

        if (!tradeInfo.isSuccess()) {
            logger.error("交易不成功，状态为failure: " + backParams);
            result.setResult(false);
            return result;
        }

        msgLog.info("交易状态更新前为：" + statusBeforeBack);


        /**
         * 只有第一次返回来才会执行业务逻辑和更新状态。
         * 在此处为了防止return 和 notify同时回来，并发更新状态，重复执行业务逻辑，引入乐观锁机制
         */
        if (statusBeforeBack.equals(TradeStatus.INIT)) {
            /**
             * 更新trade信息
             */
            try {
                tradeService.createOrUpdateTrade(tradeInfo);
                msgLog.info("更新交易状态完成，交易状态从【" + statusBeforeBack + "】更新为【" + tradeInfo.getTradeStatus() + "】");
                boolean bizHandResult = callBackHandler.doAfterBack(this.tradeInfo, type, result);
                msgLog.info("本次业务处理结果为：" + bizHandResult);
            } catch (StaleObjectStateException e) {//并发异常不需要处理
                msgLog.info("发生并发情况，return和notify通知同时请求过来");
            }
        }
        result.setResult(true);
        return result;

//        //查询支付宝发送过来的消息是否已经消费,如果已经消费则直接返回成功,主要针对情况是：return 和 Notify有很明显时差时，一个先回来，一另后回来。则直接处理成功。
//        if (tradeService.existTrade(tradeInfo.getTradeNo(), tradeInfo.getOuterTradeNo())) {
//            msgLog.info("此笔交易已经处理,TradeNo:" + tradeInfo.getTradeNo() + " 本次通知类型为：" + type);
//            result.setResult(true);
//            return result;
//        }
//
//        //插入交易日志，当return 和 Notify有一个先回来的时候，插入已经成功，另外一个回来的时候则会抛出DuplicateKeyException,也视为成功
//        //此处主要针对的是return notify并发执行的问题（同时回来接收处理）
//        try {
//            tradeService.createOrUpdateTrade(tradeInfo);
//            result.setResult(callBackHandler.doAfterBack(this.tradeInfo, type, result));
//        } catch (DuplicateKeyException e) {
//            //视为成功，不需要做任务处理。
//            msgLog.info("此笔交易遇到并发情况，return notify 同时到达, TradeNo:" + tradeInfo.getTradeNo() + ", 本次通知类型为：" + type);
//            result.setResult(true);
//        }
//
//        if (result.success()) {
//            TRADE_RETURN_LOGGER.info(buildLogString("success-" + type.getValue(), "normal", tradeInfo.getTradeNo(), tradeInfo.getOuterTradeNo(), tradeInfo.getOuterPlatformType()));
//        } else {
//            TRADE_RETURN_LOGGER.error(buildLogString("fail-" + type.getValue(), "exception", tradeInfo.getTradeNo(), tradeInfo.getOuterTradeNo(), tradeInfo.getOuterPlatformType()));
//        }
//        return result;
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
