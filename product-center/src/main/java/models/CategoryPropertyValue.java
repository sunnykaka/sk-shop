package models;

import common.models.utils.EntityClass;

import javax.persistence.*;

/**
 * Created by zhb on 15-4-7.
 */
@Table(name = "category_value")
@Entity
public class CategoryPropertyValue implements EntityClass<Integer>{

    private Integer id;

    private Integer categoryId;

    private Integer propertyId;

    private Integer valueId;

    /** 排序 */
    private Integer priority;

    public CategoryPropertyValue(){}

    public CategoryPropertyValue(int categoryId,int propertyId,int valueId){
        this.categoryId = categoryId;
        this.propertyId = propertyId;
        this.valueId = valueId;
    }

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    public Integer getId() {
        return id;
    }

    @Override
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

    @Column(name = "value_id")
    @Basic
    public Integer getValueId() {
        return valueId;
    }

    public void setValueId(Integer valueId) {
        this.valueId = valueId;
    }

    @Column(name = "priority")
    @Basic
    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}
