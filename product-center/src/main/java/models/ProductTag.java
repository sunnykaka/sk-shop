package models;

import common.models.utils.EntityClass;
import common.models.utils.OperableData;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;

/**
 * 产品（商品）标签
 * Created by lidujun on 2015-04-01.
 */
@Table(name = "product_tag")
@Entity
public class ProductTag implements EntityClass<Integer> , OperableData {

    /**
     * 库自增ID
     */
    private Integer id;

    private Integer productId;

    private Integer tagId;

    private DateTime createTime;

    private DateTime updateTime;

    private Integer operatorId;

    @Override
    public String toString() {
        return "ProductTag{" +
                "id=" + id +
                ", productId=" + productId +
                ", tagId=" + tagId +
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

    @Column(name = "tag_id")
    @Basic
    public Integer getTagId() {
        return tagId;
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId;
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
