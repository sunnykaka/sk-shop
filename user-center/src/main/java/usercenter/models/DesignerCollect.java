package usercenter.models;

import common.models.utils.EntityClass;
import common.models.utils.TableTimeData;
import common.utils.DateUtils;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;

/**
 * Created by zhb on 15-4-27.
 */
@Table(name = "designer_collect")
@Entity
public class DesignerCollect implements EntityClass<Integer>,TableTimeData {

    private Integer id;

    private int userId;

    private int designerId;

    /** 软删 */
    private boolean deleted;

    private DateTime collectTime;

    private DateTime createDate;

    private DateTime updateDate;

    private String designerPic;

    private String designerName;

    public DesignerCollect(){
        this.collectTime = DateUtils.current();
    }

    public DesignerCollect(int userId, int designerId){
        this.userId = userId;
        this.designerId = designerId;
        this.collectTime = DateUtils.current();
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

    @Column(name = "userId")
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Column(name = "designerId")
    public int getDesignerId() {
        return designerId;
    }

    public void setDesignerId(int designerId) {
        this.designerId = designerId;
    }

    @Column(name = "isDelete")
    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Column(name = "collectTime")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(DateTime collectTime) {
        this.collectTime = collectTime;
    }

    @Column(name = "createDate")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Override
    public DateTime getCreateDate() {
        return createDate;
    }

    @Override
    public void setCreateDate(DateTime createDate) {
        this.createDate = createDate;
    }

    @Column(name = "updateDate")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Override
    public DateTime getUpdateDate() {
        return updateDate;
    }

    @Override
    public void setUpdateDate(DateTime updateDate) {
        this.updateDate = updateDate;
    }

    @Transient
    public String getDesignerPic() {
        return designerPic;
    }

    public void setDesignerPic(String designerPic) {
        this.designerPic = designerPic;
    }

    @Transient
    public String getDesignerName() {
        return designerName;
    }

    public void setDesignerName(String designerName) {
        this.designerName = designerName;
    }
}
