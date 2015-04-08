package productcenter.models;

import common.models.utils.EntityClass;
import common.models.utils.OperableData;
import common.utils.Money;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 产品（商品）
 * Created by lidujun on 2015-04-01.
 */
@Table(name = "product")
@Entity
public class Product implements EntityClass<Integer>, OperableData {

    /**
     * 库自增ID
     */
    private Integer id;

    /**
     * 产品（商品）名称
     */
    private String name;

    /**
     * 设计师id
     */
    private Integer desigerId;

    /**
     * 提供的产品编码
     */
    private String supplierSpuCode;

    /**
     * 产品编码
     */
    private String spuCode;

    /**
     * 后台类目id
     */
    private Integer categoryId;

    /**
     * 地址
     */
    private String address;

    /**
     * 描述
     */
    private String description;

    /**
     * 上线标志
     */
    private Boolean online;

    /**
     * 删除标志
     */
    private Boolean isDelete;

    /**
     * 创建时间
     */
    private DateTime createTime;

    /**
     * 更新时间
     */
    private DateTime updateTime;

    /**
     * 最后操作人
     */
    private Integer operatorId;

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", desigerId=" + desigerId +
                ", supplierSpuCode='" + supplierSpuCode + '\'' +
                ", spuCode='" + spuCode + '\'' +
                ", categoryId=" + categoryId +
                ", address='" + address + '\'' +
                ", description='" + description + '\'' +
                ", online=" + online +
                ", isDelete=" + isDelete +
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

    @Column(name = "name")
    @Basic
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "desiger_id")
    @Basic
    public Integer getDesigerId() {
        return desigerId;
    }

    public void setDesigerId(Integer desigerId) {
        this.desigerId = desigerId;
    }

    @Column(name = "supplier_spu_code")
    @Basic
    public String getSupplierSpuCode() {
        return supplierSpuCode;
    }

    public void setSupplierSpuCode(String supplierSpuCode) {
        this.supplierSpuCode = supplierSpuCode;
    }

    @Column(name = "spu_code")
    @Basic
    public String getSpuCode() {
        return spuCode;
    }

    public void setSpuCode(String spuCode) {
        this.spuCode = spuCode;
    }

    @Column(name = "category_id")
    @Basic
    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    @Column(name = "address")
    @Basic
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Column(name = "description")
    @Basic
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "online")
    @Basic
    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }

    @Column(name = "is_delete")
    @Basic
    public Boolean getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Boolean isDelete) {
        this.isDelete = isDelete;
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
