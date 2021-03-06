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
     * 条形码
     */
    protected String barCode;

    /**
     * 商家ID（设计师ID）
     */
    protected int customerId;

    /**
     * 商家名称（设计师名称）
     */
    protected String customerName;

    /**
     * 库位ID(库存位置)
     */
    protected int storageId;

    /**
     * 产品id
     */
    protected int productId;

    /**
     * 产品名称，展示时使用
     */
    protected String productName;

    /**
     *类目ID
     */
    protected int categoryId;

    /**
     * 图标
     */
    protected String mainPicture;

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
     * 库存数量
     */
    protected int stockQuantity;

    /**
     * 交易最大数量
     */
    protected int tradeMaxNumber;

    /**
     * 是否有库存，这个字段不保存，是在显示购物车的时候动态从数据库中查询的，这样可以实时查看是否有货
     */
    protected boolean hasStock;

    /**
     * 该商品是否上架：是否上架(0表示未上架, 1表示上架)
     */
    protected Boolean online;

    //protected TradePriceStrategy tradePriceStrategy;


    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public int getTradeMaxNumber() {
        return tradeMaxNumber;
    }

    public void setTradeMaxNumber(int tradeMaxNumber) {
        this.tradeMaxNumber = tradeMaxNumber;
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

    public int getStorageId() {
        return storageId;
    }

    public void setStorageId(int storageId) {
        this.storageId = storageId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getMainPicture() {
        return mainPicture;
    }

    public void setMainPicture(String mainPicture) {
        this.mainPicture = mainPicture;
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

    /**
     * 计算购物车项金额合计
     * @return
     */
    public Money calTotalPrice() {
        return getCurUnitPrice().multiply(getNumber());
    }

}
