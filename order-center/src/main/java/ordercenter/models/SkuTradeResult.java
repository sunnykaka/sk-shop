package ordercenter.models;

import common.models.utils.EntityClass;

import javax.persistence.*;

/**
 * sku交易记录
 *
 * User: lidujun
 * Date: 2015-04-29
 */
@Table(name = "SkuTradeResult")
@Entity
public class SkuTradeResult implements EntityClass<Integer> {

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 产品主键id
     */
    private int productId;

    /**
     * sku主键
     */
    private int skuId;

    /**
     * 成交数量
     */
    private long number;

    /**
     * 付款成功数量
     */
    private long payNumber;

    /**
     * 退货数量
     */
    private long backNumber;

    public SkuTradeResult() {
    }

    public SkuTradeResult(int skuId, int productId) {
        this.skuId = skuId;
        this.productId = productId;
    }

    /**
     * 附加销售量
     */
    public void appendedNumber(int saleNumber) {
        number += saleNumber;
    }

    /**
     * 附加退货量
     */
    public void appendedBackNumber(int backNumber) {
        this.backNumber += backNumber;
    }

    /**
     * 附加付款成功量
     */
    public void appendedPayNumber(int payNumber) {
        this.payNumber += payNumber;
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

    @Column(name = "productId")
    @Basic
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    @Column(name = "skuId")
    @Basic
    public int getSkuId() {
        return skuId;
    }

    public void setSkuId(int skuId) {
        this.skuId = skuId;
    }

    @Column(name = "number")
    @Basic
    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    @Column(name = "payNumber")
    @Basic
    public long getPayNumber() {
        return payNumber;
    }

    public void setPayNumber(long payNumber) {
        this.payNumber = payNumber;
    }

    @Column(name = "backNumber")
    @Basic
    public long getBackNumber() {
        return backNumber;
    }

    public void setBackNumber(long backNumber) {
        this.backNumber = backNumber;
    }
}
