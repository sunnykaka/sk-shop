package productcenter.constants;

import common.models.utils.ViewEnum;

/**
 * 库存策略
 * User: lidujun
 * Date: 2015-04-24
 */
public enum StoreStrategy implements ViewEnum {

    /**
     * 普通策略(创建则扣减库存,取消则回加库存.)
     */
    NormalStrategy("普通策略"),
    /**
     * 支付成功则扣减库存策略
     */
    PayStrategy("支付成功则扣减库存策略"),
    /**
     * 热销
     */
    HOT("热销");

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

}
