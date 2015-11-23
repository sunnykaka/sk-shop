package productcenter.dtos;

import common.utils.Money;

/**
 * Created by amoszhou on 15/11/10.
 */
public class SkuDiscount {

    private Integer skuId;

    private Money skuMoney;

    private String skuPrice;


    public Integer getSkuId() {
        return skuId;
    }

    public void setSkuId(Integer skuId) {
        this.skuId = skuId;
    }

    public Money getSkuMoney() {
        return skuMoney;
    }

    public void setSkuMoney(Money skuMoney) {
        this.skuMoney = skuMoney;
    }

    public String getSkuPrice() {
        return skuPrice;
    }

    public void setSkuPrice(String skuPrice) {
        this.skuPrice = skuPrice;
    }
}
