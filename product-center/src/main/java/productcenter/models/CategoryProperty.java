package productcenter.models;

import common.models.utils.EntityClass;
import productcenter.constants.PropertyType;

import javax.persistence.*;

/**
 * 类目属性关联
 * Created by zhb on 15-4-1.
 */
@Table(name = "category_property")
@Entity
public class CategoryProperty implements EntityClass<Integer> {

    private Integer id;

    private Integer categoryId;

    private Integer propertyId;

    /**
     * 属性类型
     */
    private String type;

    private Property property;

    /**
     * 表示在发布商品的时候，这个属性的值是checkbox，可勾选多个，而不是一个单选下拉框
     */
    private boolean multiValue;

    public CategoryProperty() {
    }

    public CategoryProperty(int categoryId, int propertyId, String type, boolean multiValue) {
        this.categoryId = categoryId;
        this.propertyId = propertyId;
        this.type = type;
        this.multiValue = multiValue;
    }

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "category_id")
    @Basic
    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    @Column(name = "property_id")
    @Basic
    public Integer getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Integer propertyId) {
        this.propertyId = propertyId;
    }

    @Column(name = "type")
    @Basic
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Column(name = "multi_value")
    @Basic
    public boolean isMultiValue() {
        return multiValue;
    }

    public void setMultiValue(boolean multiValue) {
        this.multiValue = multiValue;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", insertable = false, updatable = false)
    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }
}
