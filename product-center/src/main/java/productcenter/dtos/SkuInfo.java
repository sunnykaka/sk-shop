package productcenter.dtos;

import common.utils.Money;

import java.util.List;

/**
 * Created by liubin on 15-5-18.
 */
public class SkuInfo {

    private Integer skuId;

    /**
     * sku编码
     */
    private String skuCode;

    /**
     * 条形码
     */
    private String barCode;

    /**
     * 首发价格
     */
    private Money price = Money.valueOf(0);

    /**
     * 售卖价格
     */

    private Money marketPrice = Money.valueOf(0);

    /**
     * SKU属性在数据库中的字符串表示 pid:vid,pid:vid，值和上面的skuProperties对应
     */
    private String skuPropertiesInDb;

    private int stockQuantity;

    private int tradeMaxNumber;

    private List<String> imageList;

    public SkuInfo(Integer skuId, String skuCode, String barCode, Money price, Money marketPrice,
                   String skuPropertiesInDb, int stockQuantity, int tradeMaxNumber, List<String> imageList) {
        this.skuId = skuId;
        this.skuCode = skuCode;
        this.barCode = barCode;
        this.price = price;
        this.marketPrice = marketPrice;
        this.skuPropertiesInDb = skuPropertiesInDb;
        this.stockQuantity = stockQuantity;
        this.tradeMaxNumber = tradeMaxNumber;
        this.imageList = imageList;
    }

    public Integer getSkuId() {
        return skuId;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public String getBarCode() {
        return barCode;
    }

    public Money getPrice() {
        return price;
    }

    public Money getMarketPrice() {
        return marketPrice;
    }

    public String getSkuPropertiesInDb() {
        return skuPropertiesInDb;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public List<String> getImageList() {
        return imageList;
    }

    public int getTradeMaxNumber() {
        return tradeMaxNumber;
    }
}
