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
    private  String description;

    /**
     * 是否删除
     */
    private boolean isDelete;

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
}
