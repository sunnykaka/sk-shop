package common.models;

import common.models.utils.EntityClass;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;

/**
 *
 */
@Entity
@Table(name = "const")
public class Const implements EntityClass<Integer> {

    /**
     * 专场开始提前通知时间
     */
    public static final String AHEAD_REMIND_USER_FOR_EXHIBITION = "AHEAD_REMIND_USER_FOR_EXHIBITION";

    private Integer id;

    /** 全局常量键 */
    private String constKey;

    /** 全局常量键值 */
    private String constValue;

    /** 常量说明 */
    private String constComment;

    private DateTime createDate;
    private DateTime updateDate;

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

    @Column(name = "constKey")
    public String getConstKey() {
        return constKey;
    }

    public void setConstKey(String constKey) {
        this.constKey = constKey;
    }

    @Column(name = "constValue")
    public String getConstValue() {
        return constValue;
    }

    public void setConstValue(String constValue) {
        this.constValue = constValue;
    }

    @Column(name = "constComment")
    public String getConstComment() {
        return constComment;
    }

    public void setConstComment(String constComment) {
        this.constComment = constComment;
    }

    @Column(name = "createDate")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(DateTime createDate) {
        this.createDate = createDate;
    }

    @Column(name = "updateDate")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(DateTime updateDate) {
        this.updateDate = updateDate;
    }
}
