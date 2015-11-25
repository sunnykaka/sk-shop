package usercenter.models;

import common.models.utils.EntityClass;

import javax.persistence.*;

/**
 * 设计师其实就是商家
 * User: Asion
 * Date: 12-6-20
 * Time: 下午1:36
 */
@Entity
@Table(name = "customer")
public class Designer implements EntityClass<Integer> {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 快递公司
     */
    private DeliveryInfo.DeliveryType defaultLogistics;

    /**
     * 国籍id
     */
    private Integer nationId;

    /**
     * 介绍
     */
    private String description;

    /**
     * 是否删除
     */
    private boolean isDelete;

    /**
     * 是否发布
     */
    private boolean isPublished;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 设计师备注，商品详情展示时需要(优先展示商品备注，商品备注没有再展示设计师备注，若都没有则不展示)
     */
    private String remarks;

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

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "defaultLogistics")
    @Enumerated(EnumType.STRING)
    public DeliveryInfo.DeliveryType getDefaultLogistics() {
        return defaultLogistics;
    }

    public void setDefaultLogistics(DeliveryInfo.DeliveryType defaultLogistics) {
        this.defaultLogistics = defaultLogistics;
    }

    @Column(name = "nationId")
    public Integer getNationId() {
        return nationId;
    }

    public void setNationId(Integer nationId) {
        this.nationId = nationId;
    }

    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "isDelete")
    public boolean getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(boolean isDelete) {
        this.isDelete = isDelete;
    }

    @Column(name = "isPublished")
    public boolean getIsPublished() {
        return isPublished;
    }

    public void setIsPublished(boolean isPublished) {
        this.isPublished = isPublished;
    }

    @Column(name = "priority")
    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    @Column(name = "remarks")
    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
