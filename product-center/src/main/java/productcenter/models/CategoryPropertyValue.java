package productcenter.models;

import common.models.utils.EntityClass;
import common.models.utils.OperableData;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import productcenter.constants.PropertyType;

import javax.persistence.*;

/**
 * 类目属性值 领域对象，它代表某个类目属性的一个特定的值，比如手机类目
 * 品牌类目属性的三星，这里三星就是类目属性值
 *
 * @Author: Tiger
 * @Since: 11-6-25 下午1:12
 * @Version 1.0.0
 * @Copyright (c) 2011 by Kariqu.com
 */
@Table(name = "CategoryPropertyValue")
@Entity
public class CategoryPropertyValue implements EntityClass<Integer>, OperableData {

    private Integer id; //没有业务意义，只是数据库主键

    private Integer categoryId;

    private ProductCategory category;

    private Integer propertyId;

    private Property property;

    private Integer valueId;

    private Value value;

    private PropertyType propertyType;

    /**
     * 表示在发布商品的时候，这个属性的值是checkbox，可勾选多个，而不是一个单选下拉框
     */
    private boolean multiValue;

    private boolean compareable;

    /**
     * 类目属性的排序优先级，数字越小越靠前，这个优先级目前没有作用，展示给用户界面的是前台类目属性优先级
     */
    private int priority;

    private DateTime createTime;

    private DateTime updateTime;

    private boolean deleted;

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

    @Column(name = "priority")
    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Column(name = "multiValue")
    public boolean isMultiValue() {
        return multiValue;
    }

    public void setMultiValue(boolean multiValue) {
        this.multiValue = multiValue;
    }

    @Column(name = "compareable")
    public boolean isCompareable() {
        return compareable;
    }

    public void setCompareable(boolean compareable) {
        this.compareable = compareable;
    }

    @Column(name = "categoryId")
    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    @Column(name = "propertyId")
    public Integer getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Integer propertyId) {
        this.propertyId = propertyId;
    }

    @Column(name = "propertyType")
    public PropertyType getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(PropertyType propertyType) {
        this.propertyType = propertyType;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryId", insertable = false, updatable = false)
    public ProductCategory getCategory() {
        return category;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propertyId", insertable = false, updatable = false)
    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    @Column(name = "valueId")
    public Integer getValueId() {
        return valueId;
    }

    public void setValueId(Integer valueId) {
        this.valueId = valueId;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "valueId", insertable = false, updatable = false)
    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
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
    @Override
    public DateTime getUpdateTime() {
        return updateTime;
    }

    @Override
    public void setUpdateTime(DateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Column(name = "isDelete")
    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
