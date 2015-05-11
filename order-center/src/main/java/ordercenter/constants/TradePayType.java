package ordercenter.constants;

import common.models.utils.ViewEnum;

/**
 * 订单支付类型
 * <p/>
 * 支付方式的不同决定订单的生命周期
 * 如果一个订单是在线付款，我们会设置一个付款期限，如果超过这个订单就要自动取消
 * User: lidujun
 * Date: 2015-04-30
 */
public enum TradePayType implements ViewEnum {

    /**
     * 货到付款（现金付款）
     */
    OnDelivery_Cash("货到付款"),
    /**
     * POS机刷卡
     */
    OnDelivery_POS("POS机刷卡"),
    /**
     * 在线付款
     */
    OnLine("在线付款");

    public String value;

    TradePayType(String value) {
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
