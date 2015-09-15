package productcenter.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    private boolean defaultSku;

    @JsonCreator
    public SkuInfo(
           @JsonProperty("skuId") Integer skuId,
           @JsonProperty("skuCode") String skuCode,
           @JsonProperty("barCode") String barCode,
           @JsonProperty("price") Money price,
           @JsonProperty("marketPrice") Money marketPrice,
           @JsonProperty("skuPropertiesInDb") String skuPropertiesInDb,
           @JsonProperty("stockQuantity") int stockQuantity,
           @JsonProperty("tradeMaxNumber") int tradeMaxNumber,
           @JsonProperty("imageList") List<String> imageList,
           @JsonProperty("defaultSku") boolean defaultSku) {
        this.skuId = skuId;
        this.skuCode = skuCode;
        this.barCode = barCode;
        this.price = price;
        this.marketPrice = marketPrice;
        this.skuPropertiesInDb = skuPropertiesInDb;
        this.stockQuantity = stockQuantity;
        this.tradeMaxNumber = tradeMaxNumber;
        this.imageList = imageList;
        this.defaultSku = defaultSku;
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

    public boolean isDefaultSku() {
        return defaultSku;
    }
}
