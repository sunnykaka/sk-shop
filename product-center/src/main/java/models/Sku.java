package models;

import common.models.utils.EntityClass;
import common.models.utils.OperableData;
import common.utils.Money;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;

/**
 * 单品SKU，表示可以存储的最小的单元
 *
 * Created by zhb on 15-4-1.
 */
@Table(name = "sku")
@Entity
public class Sku implements EntityClass<Integer>, OperableData {

    private Integer id;

    private Integer productId;

    /** SKU状态 */
    private Boolean online;

    private String barCode;

    private String skuCode;

    private Money price;

    private Money domesticPrice;

    private Money foreignPrice;

    private Integer stock;

    private Integer sellMax;

    private DateTime createTime;

    private DateTime updateTime;

    private Integer operatorId;

    @Override
    public String toString() {
        return "SKU{" +
                "id=" + id +
                ", productId=" + productId +
                ", online=" + online +
                ", barCode='" + barCode + '\'' +
                ", skuCode='" + skuCode + '\'' +
                ", price=" + price +
                ", domesticPrice=" + domesticPrice +
                ", foreignPrice=" + foreignPrice +
                ", stock=" + stock +
                ", sellMax=" + sellMax +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", operatorId=" + operatorId +
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

    @Column(name = "product_id")
    @Basic
    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    @Column(name = "online")
    @Basic
    public Boolean isOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }

    @Column(name = "bar_code")
    @Basic
    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    @Column(name = "sku_code")
    @Basic
    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    @Column(name = "price")
    @Type(type="common.utils.hibernate.MoneyType")
    public Money getPrice() {
        return price;
    }

    public void setPrice(Money price) {
        this.price = price;
    }

    @Column(name = "domestic_price")
    @Type(type="common.utils.hibernate.MoneyType")
    public Money getDomesticPrice() {
        return domesticPrice;
    }

    public void setDomesticPrice(Money domesticPrice) {
        this.domesticPrice = domesticPrice;
    }

    @Column(name = "foreign_price")
    @Type(type="common.utils.hibernate.MoneyType")
    public Money getForeignPrice() {
        return foreignPrice;
    }

    public void setForeignPrice(Money foreignPrice) {
        this.foreignPrice = foreignPrice;
    }

    @Column(name = "stock")
    @Basic
    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    @Column(name = "sell_max")
    @Basic
    public Integer getSellMax() {
        return sellMax;
    }

    public void setSellMax(Integer sellMax) {
        this.sellMax = sellMax;
    }

    @Column(name = "create_time")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Override
    public DateTime getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(DateTime createTime) {
        this.createTime = createTime;
    }

    @Column(name = "update_time")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Override
    public DateTime getUpdateTime() {
        return updateTime;
    }

    @Override
    public void setUpdateTime(DateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Column(name = "operator_id")
    @Basic
    @Override
    public Integer getOperatorId() {
        return operatorId;
    }

    @Override
    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }
}
