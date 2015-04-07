package models;

import common.models.utils.EntityClass;

import javax.persistence.*;

/**
 * 前台、后台类目关联
 * Created by zhb on 15-4-1.
 */
@Table(name = "navigate_category")
@Entity
public class NavigateCategory implements EntityClass<Integer> {

    private Integer id;

    private Integer categoryId;

    private Integer navigateId;

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

    @Column(name = "navigate_id")
    @Basic
    public Integer getNavigateId() {
        return navigateId;
    }

    public void setNavigateId(Integer navigateId) {
        this.navigateId = navigateId;
    }
}
