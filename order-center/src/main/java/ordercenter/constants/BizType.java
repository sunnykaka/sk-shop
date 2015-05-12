package ordercenter.constants;

import common.models.utils.ViewEnum;

/**
 * 业务类型
 * User: lidujun
 * Date: 2015-05-08
 */
public enum BizType implements ViewEnum {
    /**
     * 订单
     */
    Order("订单"),
    /**
     * 优惠券
     */
    Coupon("优惠券");

    public String value;

    BizType(String value) {
        this.value = value;
    }

    @Override
    public String getName() {
        return this.toString();
    }

    @Override
    public String getValue() {
        return value;
    }
}
