package models;

import common.models.utils.EntityClass;
import common.models.utils.OperableData;
import constants.PropertyType;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;

/**
 * 产品（商品）关联属性
 *
 * Created by zhb on 15-4-1.
 */
@Table(name = "product_property")
@Entity
public class ProductProperty implements EntityClass<Integer>, OperableData {

    private Integer id;

    private Integer productId;

    private PropertyType type;

    //商品的pidvid json串，表示商品的属性，按属性在类目上的优先级排序
    // '{"singlePidVidMap":{"1":4294977296,"4":17179879190,"5":21474846488},"pidvid":[4294977296,17179879190,21474846488],"multiPidVidMap":{}}'
    private String json;

    private DateTime createTime;

    private DateTime updateTime;

    private Integer operatorId;

    @Override
    public String toString() {
        return "ProductProperty{" +
                "id=" + id +
                ", productId=" + productId +
                ", type=" + type +
                ", json='" + json + '\'' +
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

    @Column(name = "type")
    @Basic
    public PropertyType getType() {
        return type;
    }

    public void setType(PropertyType type) {
        this.type = type;
    }

    @Column(name = "json")
    @Basic
    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
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
