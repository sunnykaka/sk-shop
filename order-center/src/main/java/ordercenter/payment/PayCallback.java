package ordercenter.payment;

import ordercenter.payment.constants.ResponseType;
import ordercenter.payment.models.TradeInfo;

/**
 * 支付请求成功返回后的业务处理接口
 * User: lidujun
 * Date: 2015-04-29
 */
public interface PayCallback {

    /**
     * @param tradeInfo   封装好的交易信息
     * @param type           调用类型：return || notify
     * @return                  CallBackResult，包括支付结果（true || false)，以及需要返回给调用端(Controller,页面)的参数,以及需要跳转的页面等。
     */
    CallBackResult  initResult(TradeInfo tradeInfo, ResponseType type);


    /**
     * 在支付请求成功返回后，处理自己的回调业务
     * @param tradeInfo    封装好的交易信息
     * @param type      调用类型：return || notify
     * @param result      可以增加额外的返回数据，设置返回值
     * @return
     */
   boolean doAfterBack(TradeInfo tradeInfo, ResponseType type, CallBackResult result);
}
