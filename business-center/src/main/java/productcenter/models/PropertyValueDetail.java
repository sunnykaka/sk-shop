package productcenter.models;

import common.models.utils.EntityClass;
import common.models.utils.OperableData;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;

/**
 * 类目属性值的详情，比如鞋子类目中鞋跟这个属性，它有3cm,5cm等不同的属性值，
 * 可以通过具体的图片来展示这些类目属性值
 * 注意：它只描述属性和值，不和类目相关，所以如果不同类目都要用到这个值描述，那么
 * 他们必须妥协为公共的描述，比如红色的鞋就具体描述了鞋，如果另外一个类目衣服也要用这个红色
 * 那么他们共同决定用一个红色图片代替，而不是包含类目信息
 * User: lidujun
 * Date: 2015-04-24
 */
@Table(name = "PropertyValueDetail")
@Entity
public class PropertyValueDetail implements EntityClass<Integer>, OperableData {

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 属性主键id
     */
    private int propertyId;

    /**
     * 值主键id
     */
    private int valueId;

    /**
     * 图片url
     */
    private String pictureUrl;

    /**
     * 描述
     */
    private String description;

    /**
     * 是否被删除
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

    @Override
    public String toString() {
        return "PropertyValueDetail{" +
                "id=" + id +
                ", propertyId=" + propertyId +
                ", valueId=" + valueId +
                ", pictureUrl='" + pictureUrl + '\'' +
                ", description='" + description + '\'' +
                ", isDelete=" + isDelete +
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

    @Column(name = "propertyId")
    @Basic
    public int getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(int propertyId) {
        this.propertyId = propertyId;
    }

    @Column(name = "valueId")
    @Basic
    public int getValueId() {
        return valueId;
    }

    public void setValueId(int valueId) {
        this.valueId = valueId;
    }

    @Column(name = "pictureUrl")
    @Basic
    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    @Column(name = "description")
    @Basic
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "isDelete")
    @Basic
    public Boolean getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Boolean isDelete) {
        this.isDelete = isDelete;
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
}
