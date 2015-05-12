package ordercenter.constants;

import common.models.utils.ViewEnum;

/**
 * 交易类型
 * User: lidujun
 * Date: 2015-05-11
 */
public enum TradeType implements ViewEnum {

    BuyProduct("购买商品"),

    Recharge("充值");

    public String value;

    TradeType(String value) {
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
