package productcenter.constants;

import common.models.utils.ViewEnum;

/**
 * 库存扣减策略
 * User: lidujun
 * Date: 2015-04-28
 */
public enum StoreStrategy implements ViewEnum{
    /**
     * 普通策略(创建则扣减库存,取消则回加库存.)，包括货到付款
     */
    NormalStrategy("普通策略"),
    /**
     * 支付成功则扣减库存策略
     */
    PayStrategy("支付策略");

    public String value;

    StoreStrategy(String value) {
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

    /**
     * 判断是否是支付策略（支付成功则扣减库存策略）
     * @return
     */
    public boolean isPayStrategy() {
        return this == PayStrategy;
    }

}







