package usercenter.models;

import common.models.utils.EntityClass;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;

/**
 * 反馈意见
 * User: lidujun
 * Date: 2015-07-13
 */
@Entity
@Table(name = "bulletin")
public class Bulletin implements EntityClass<Integer> {

    /**
     * 主键
     */
    private Integer id;

    /**
     * 内容
     */
    private String content;

    /**
     * 是否有效，默认有效
     */
    private Boolean effective = false;

    /**
     * 创建时间
     */
    private DateTime createTime;

    /**
     * 创建人id
     */
    private Integer createBy;

    /**
     * 更新时间
     */
    private DateTime updateTime;

    /**
     * 更新者id
     */
    private Integer updateBy;

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

    @Column(name = "content")
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Column(name = "effective")
    public Boolean getEffective() {
        return effective;
    }

    public void setEffective(Boolean effective) {
        this.effective = effective;
    }

    @Column(name = "createTime")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(DateTime createTime) {
        this.createTime = createTime;
    }

    @Column(name = "createBy")
    public Integer getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Integer createBy) {
        this.createBy = createBy;
    }

    @Column(name = "updateTime")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(DateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Column(name = "updateBy")
    public Integer getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(Integer updateBy) {
        this.updateBy = updateBy;
    }
}
