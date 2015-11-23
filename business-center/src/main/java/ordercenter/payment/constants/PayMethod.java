package ordercenter.payment.constants;

import common.models.utils.ViewEnum;
import ordercenter.payment.PayRequestHandler;
import ordercenter.payment.alipay.AliPayRequestHandler;
import ordercenter.payment.tenpay.TenPayRequestHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付方式
 * User: lidujun
 * Date: 2015-04-29
 */
public enum PayMethod implements ViewEnum {

    /**
     * 信用卡，银行卡都是走财付通的渠道（channel），故此他们的支付渠道均为一样
     */
    directPay("支付宝", PayChannel.Alipay),
    creditPay("信用卡", PayChannel.Alipay),
    bankPay("银行卡", PayChannel.Alipay),
    Tenpay("财付通", PayChannel.TenPay),
    WXSM("微信支付", PayChannel.WXSM);


    private static Map<PayMethod, Class<? extends PayRequestHandler>> payServiceMap = new HashMap<PayMethod, Class<? extends PayRequestHandler>>();

    /**
     *将默认的方式初始化管理，并提供  addPayService（）以方便运时添加
     */
    static {
        //支付宝，实现类
        addPayService(directPay, AliPayRequestHandler.class);
        //银行，目前也是通过支付宝来实现
        addPayService(bankPay, AliPayRequestHandler.class);
        //财富通，实现类
        addPayService(Tenpay, TenPayRequestHandler.class);
        //微信，实现类
        addPayService(WXSM, TenPayRequestHandler.class);
    }

    /**
     * 得到支付实例
     *
     * @return
     */
    public PayRequestHandler getPayRequestHandler() {
        try {
            return payServiceMap.get(this).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 提供的对外接口,以方便运行时添加
     *
     * @param payMethod
     * @param payService
     */
    public static void addPayService(PayMethod payMethod, Class<? extends PayRequestHandler> payService) {
        payServiceMap.put(payMethod, payService);
    }

    /**
     * 支付方式中文名
     */
    private String nameZhCn;
    /**
     * 支付通道
     */
    private PayChannel channel;

    PayMethod(String name, PayChannel channel) {
        this.nameZhCn = name;
        this.channel = channel;
    }

    @Override
    public String getName() {
        return this.toString();
    }

    @Override
    public String getValue() {
        return nameZhCn;
    }

    public String getChannel() {
        return this.channel.toString();
    }
}
