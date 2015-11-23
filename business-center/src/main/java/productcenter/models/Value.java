package productcenter.models;

import common.models.utils.EntityClass;
import common.models.utils.OperableData;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;

/**
 * 类目属性的值，它是可以在不同的类目中共享，比如红色是颜色这个类目属性的一个特定值，而颜色
 * 可以在多个类目共享， 在数据库中成为数据字典，要使用这个值的时候就取出对应的ID
 * User: lidujun
 * Date: 2015-04-24
 */
@Table(name = "Value")
@Entity
public class Value implements EntityClass<Integer>, OperableData {

    /**
     * 值主键id
     */
    private Integer id;

    /**
     * 值名称
     */
    private String valueName;

    /**
     * 是否被删除
     */
    private Boolean isDelete = false;

    /**
     * 创建时间
     */
    private DateTime createTime;

    /**
     * 更新时间
     */
    private DateTime updateTime;

    public Value() {
    }

    public Value(String valueName) {
        this.valueName = valueName;
    }

    @Override
    public String toString() {
        return "Value{" +
                "id=" + id +
                ", valueName='" + valueName + '\'' +
                ", isDelete=" + isDelete +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Value value = (Value) o;

        if (!id.equals(value.id)) return false;
        if (valueName != null ? !valueName.equals(value.valueName) : value.valueName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (valueName != null ? valueName.hashCode() : 0);
        return result;
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

    @Column(name = "valueName")
    @Basic
    public String getValueName() {
        return valueName;
    }

    public void setValueName(String valueName) {
        this.valueName = valueName;
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
