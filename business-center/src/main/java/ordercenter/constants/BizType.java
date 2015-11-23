package ordercenter.constants;

import common.models.utils.ViewEnum;
import ordercenter.payment.PayCallback;
import ordercenter.services.OrderPayCallbackProcess;

/**
 * 业务类型
 * User: lidujun
 * Date: 2015-05-08
 */
public enum BizType implements ViewEnum {
    /**
     * 订单
     */
    Order("订单", OrderPayCallbackProcess.class);
//    /**
//     * 优惠券
//     */
//    Coupon("优惠券", OrderPayCallbackProcess.class);

    private String value;

    private Class<? extends PayCallback> handlerClass;

    BizType(String value, Class<? extends PayCallback> handlerClass) {
        this.value = value;
        this.handlerClass = handlerClass;
    }

    @Override
    public String getName() {
        return this.toString();
    }

    @Override
    public String getValue() {
        return value;
    }

    public Class getHandlerClass(){
        return this.handlerClass;
    }
}
