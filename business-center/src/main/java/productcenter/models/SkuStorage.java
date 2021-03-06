package productcenter.models;

import common.models.utils.EntityClass;

import javax.persistence.*;

/**
 * sku 对应的库位
 *
 * User: lidujun
 * Date: 2015-04-27
 */
@Table(name = "SkuStorage")
@Entity
public class SkuStorage implements EntityClass<Integer> {
    /**
     * 主键 id
     */
    private Integer id;

    /**
     * sku
     */
    private int skuId;

    private StockKeepingUnit sku;

    /**
     * 库位编号
     */
    private int productStorageId;
    /**
     * 库存数量
     */
    private int stockQuantity;

    /**
     * 交易最大数量
     */
    private int tradeMaxNumber;

    @Override
    public String toString() {
        return "SkuStorage{" +
                "id=" + id +
                ", skuId=" + skuId +
                ", productStorageId=" + productStorageId +
                ", stockQuantity=" + stockQuantity +
                ", tradeMaxNumber=" + tradeMaxNumber +
                '}';
    }

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "skuId")
    @Basic
    public int getSkuId() {
        return skuId;
    }

    public void setSkuId(int skuId) {
        this.skuId = skuId;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skuId", insertable = false, updatable = false, unique = true)
    public StockKeepingUnit getSku() {
        return sku;
    }

    public void setSku(StockKeepingUnit sku) {
        this.sku = sku;
    }

    @Column(name = "productStorageId")
    @Basic
    public int getProductStorageId() {
        return productStorageId;
    }

    public void setProductStorageId(int productStorageId) {
        this.productStorageId = productStorageId;
    }

    @Column(name = "stockQuantity")
    @Basic
    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    @Column(name = "tradeMaxNumber")
    @Basic
    public int getTradeMaxNumber() {
        return tradeMaxNumber;
    }

    public void setTradeMaxNumber(int tradeMaxNumber) {
        this.tradeMaxNumber = tradeMaxNumber;
    }
}
