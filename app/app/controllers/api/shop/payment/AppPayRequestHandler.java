package controllers.api.shop.payment;

import ordercenter.models.Trade;
import play.Configuration;
import play.Play;

import java.util.Map;

/**
 * 支付请求处理抽象类
 * User: lidujun
 * Date: 2015-04-29
 */
public abstract class AppPayRequestHandler {
    /**
     * 读取play配置文件
     */
    protected static Configuration cfg = Play.application().configuration();

    /**
     * 根据交易信息构建支付信息的map
     *
     * @param trade
     * @return
     */
    public Map<String, String> buildPayInfo(Trade trade) {
        return null;
    }

    /**
     * 构建付款参数
     *
     * @param trade
     * @return
     */
    protected abstract Map<String, String> buildPayParam(Trade trade);

    /**
     * 付款url地址
     *
     * @return
     */
    protected abstract String getPaymentURL();

    /**
     * 登陆
     *
     * @param params
     */
    protected abstract void doSign(Map<String, String> params);

}
