package ordercenter.models;


/**
 * 交易项
 * User: lidujun
 * Date: 2015-04-23
 */
public class TradeItem {




    /**
     * 是否有库存，这个字段不保存，是在显示购物车的时候动态从数据库中查询的，这样可以实时查看是否有货
     */
    protected boolean hasStock;

    //protected TradePriceStrategy tradePriceStrategy;



    public boolean isHasStock() {
        return hasStock;
    }

    public void setHasStock(boolean hasStock) {
        this.hasStock = hasStock;
    }
}
