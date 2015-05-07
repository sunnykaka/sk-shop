package ordercenter.models;

import common.utils.Money;
import productcenter.models.StockKeepingUnit;

/**
 * 交易项
 * User: lidujun
 * Date: 2015-05-07
 */
public class TradeItem {

    /**
     * 商品skuId
     */
    protected int skuId;

    /**
     * 产品名称，展示时使用
     */
    protected String productName;

    /**
     * 单品sku
     */
    protected StockKeepingUnit sku;

    /**
     * 购买价格，展示时使用
     */
    protected Money curUnitPrice;

    /**
     * 购买数量
     */
    protected int number;

    /**
     * 合计多少钱
     */
    protected Money totalPrice = Money.valueOf(0);

    /**
     * 是否有库存，这个字段不保存，是在显示购物车的时候动态从数据库中查询的，这样可以实时查看是否有货
     */
    protected boolean hasStock;

    /**
     * 该商品是否上架：是否上架(0表示未上架, 1表示上架)
     */
    protected Boolean online;

    //protected TradePriceStrategy tradePriceStrategy;

    /**
     * 小计：获取购物车项总价（这个方法可能没用了）
     * @return
     */
    public Money getBuyTotalMoney1() {
        return curUnitPrice.multiply(number);
    }

    public boolean isHasStock() {
        return hasStock;
    }

    public void setHasStock(boolean hasStock) {
        this.hasStock = hasStock;
    }

    public Boolean isOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }

    public int getSkuId() {
        return skuId;
    }

    public void setSkuId(int skuId) {
        this.skuId = skuId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public StockKeepingUnit getSku() {
        return sku;
    }

    public void setSku(StockKeepingUnit sku) {
        this.sku = sku;
    }

    public Money getCurUnitPrice() {
        return curUnitPrice;
    }

    public void setCurUnitPrice(Money curUnitPrice) {
        this.curUnitPrice = curUnitPrice;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Money getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Money totalPrice) {
        this.totalPrice = totalPrice;
    }
}
