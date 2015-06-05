package models;

import common.models.utils.EntityClass;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;

/**
 * Created by amos on 15-5-7.
 */
@Table(name = "exhibition_fans")
@Entity
public class CmsExhibitionFans implements EntityClass<Integer> {

    /**
     * 主键
     */
    private Integer id;

    private Integer exhibitionId;

    private Integer userId;

    private DateTime createTime;

    private String phone;

    private boolean processed = false;

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

    @Column(name = "exhibitionId")
    public Integer getExhibitionId() {
        return exhibitionId;
    }

    public void setExhibitionId(Integer exhibitionId) {
        this.exhibitionId = exhibitionId;
    }

    @Column(name = "userId")
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Column(name = "createTime")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(DateTime createTime) {
        this.createTime = createTime;
    }

    @Column(name = "phone")
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Column(name = "processed")
    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }
}
