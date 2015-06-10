package ordercenter.payment;


import ordercenter.payment.alipay.AliPayRequestHandler;
import ordercenter.payment.constants.PayMethod;
import ordercenter.payment.tenpay.TenPayRequestHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付管理类
 * User: lidujun
 * Date: 2015-04-29
 */
public class PaymentManager {

    private static Map<PayMethod,Class<? extends PayRequestHandler>> payServiceMap = new HashMap<PayMethod,Class<? extends PayRequestHandler>>();

    /**
     *将默认的方式初始化管理，并提供  addPayService（）以方便运时添加
     */
    static{
        //支付宝，实现类
        addPayService(PayMethod.directPay, AliPayRequestHandler.class);
        //银行，目前也是通过支付宝来实现
        addPayService(PayMethod.bankPay, AliPayRequestHandler.class);
         //财富通，实现类
        addPayService(PayMethod.Tenpay, TenPayRequestHandler.class);
    }

    /**
     * 得到支付实例
     * @param payMethod
     * @return
     */
    public static PayRequestHandler getPayRequestHandler(PayMethod payMethod){
        try {
            return payServiceMap.get(payMethod).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 提供的对外接口,以方便运行时添加
     * @param payMethod
     * @param payService
     */
    public static void addPayService(PayMethod payMethod,Class<? extends PayRequestHandler> payService){
        payServiceMap.put(payMethod,payService);
    }
}
