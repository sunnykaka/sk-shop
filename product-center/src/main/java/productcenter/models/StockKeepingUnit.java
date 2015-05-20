package productcenter.models;

import common.models.utils.EntityClass;
import common.models.utils.OperableData;
import common.utils.Money;
import common.utils.play.BaseGlobal;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import productcenter.constants.SKUState;
import productcenter.services.PropertyAndValueService;
import productcenter.util.PropertyValueUtil;

import javax.persistence.*;
import java.util.*;

/**
 * SKU来自于线下零售行业，表示可以存储的最小的单元
 *
 * User: lidujun
 * Date: 2015-04-24
 */
@Table(name = "StockKeepingUnit")
@Entity
public class StockKeepingUnit implements EntityClass<Integer>, OperableData {

    /**
     * SKU主键  ID
     */
    private Integer id;

    /**
     * 商品ID，一个商品可以有多个sku
     */
    private Integer productId;

    /**
     * sku编码
     */
    private String skuCode;

    /**
     * 条形码
     */
    private String barCode;

    /**
     * 售卖价格
     */
    private Money price = Money.valueOf(0);

    /**
     * 首发价格
     */
    private Money marketPrice = Money.valueOf(0);

    /**
     * SKU属性在数据库中的字符串表示 pid:vid,pid:vid，值和上面的skuProperties对应
     */
    private String skuPropertiesInDb;

    /**
     * SKU 状态，默认是无效的，需要操作人员手的有效，有效的时候会检查约束
     */
    private SKUState skuState = SKUState.REMOVED;

    private boolean skuPropertiesLoaded = false;
    /**
     * SKU 属性列表
     */
    private List<SkuProperty> skuProperties = new ArrayList<>();

    /**
     * 创建时间
     */
    private DateTime createTime;

    /**
     * 更新时间可用于搜索引擎重dump数据
     */
    private DateTime updateTime;

    /**
     * 设置为可以买
     * @return
     */
    public boolean canBuy() {
        return this.skuState == SKUState.NORMAL;
    }

    @Override
    public String toString() {
        return "StockKeepingUnit{" +
                "id=" + id +
                ", productId=" + productId +
                ", skuCode='" + skuCode + '\'' +
                ", barCode='" + barCode + '\'' +
                ", price=" + price +
                ", marketPrice=" + marketPrice +
                ", skuPropertiesInDb='" + skuPropertiesInDb + '\'' +
                ", skuState=" + skuState +
                ", skuProperties=" + skuProperties +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
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

    @Column(name = "productId")
    @Basic
    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    @Column(name = "skuCode")
    @Basic
    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    @Column(name = "barCode")
    @Basic
    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    @Column(name = "price")
    @Type(type="common.utils.hibernate.MoneyType")
    public Money getPrice() {
        return price;
    }

    public void setPrice(Money price) {
        this.price = price;
    }

    @Column(name = "marketPrice")
    @Type(type="common.utils.hibernate.MoneyType")
    public Money getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(Money marketPrice) {
        this.marketPrice = marketPrice;
    }

    @Column(name = "skuPropertiesInDb")
    @Basic
    public String getSkuPropertiesInDb() {
        return skuPropertiesInDb;
    }

    public void setSkuPropertiesInDb(String skuPropertiesInDb) {
        this.skuPropertiesInDb = skuPropertiesInDb;
    }

    @Column(name = "skuState")
    @Enumerated(EnumType.STRING)
    public SKUState getSkuState() {
        return skuState;
    }

    public void setSkuState(SKUState skuState) {
        this.skuState = skuState;
    }

    @Transient
    public List<SkuProperty> getSkuProperties() {
        if(!skuPropertiesLoaded) {
            skuProperties = loadSkuProperty();
            skuPropertiesLoaded = true;
        }

        return skuProperties;
    }

    /**
     * 将sku数据库中的字符串pidvid列表转换为Sku属性的List
     * @return
     */
    private List<SkuProperty> loadSkuProperty() {
        List<SkuProperty> skuProperties = new ArrayList<>();
        String skuPropertiesInDb = getSkuPropertiesInDb(); //把数据库中的字符串pidvid列表转换为Sku属性的List
        Set<Integer> propertyIdSet = new HashSet<>();
        Set<Integer> valueIdSet = new HashSet<>();

        if (StringUtils.isNotEmpty(skuPropertiesInDb)) {
            String[] pidvid = skuPropertiesInDb.split(",");
            for (String s : pidvid) {
                SkuProperty skuProperty = new SkuProperty(getId(), Long.valueOf(s));
                skuProperties.add(skuProperty);

                propertyIdSet.add(skuProperty.getPropertyId());
                valueIdSet.add(skuProperty.getValueId());
            }

            //得到property的name和value
            PropertyAndValueService propertyAndValueService = BaseGlobal.ctx.getBean(PropertyAndValueService.class);
            List<Property> propertyList = propertyAndValueService.getPropertyByIdList(new ArrayList<>(propertyIdSet));
            List<Value> valueList = propertyAndValueService.getValueByIdList(new ArrayList<>(valueIdSet));

            skuProperties.forEach(sku -> {
                Optional<Property> firstProperty = propertyList.stream().filter(p -> p.getId() == sku.getPropertyId()).findFirst();
                if(firstProperty.isPresent()) {
                    sku.setPropertyName(firstProperty.get().getName());
                }

                Optional<Value> firstValue = valueList.stream().filter(v -> v.getId() == sku.getValueId()).findFirst();
                if(firstValue.isPresent()) {
                    sku.setPropertyValue(firstValue.get().getValueName());
                }
            });

        }


        return skuProperties;
    }

    @Column(name = "createTime")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Override
    public DateTime getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(DateTime createTime) {
        this.createTime = createTime;
    }

    @Column(name = "updateTime")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getUpdateTime() {
        return updateTime;
    }

    @Override
    public void setUpdateTime(DateTime updateTime) {
        this.updateTime = updateTime;
    }
}
