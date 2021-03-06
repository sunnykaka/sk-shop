package cmscenter.models;

import common.models.utils.EntityClass;
import common.models.utils.TableTimeData;
import common.utils.DateUtils;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;

/**
 * Created by Administrator on 2015/9/6.
 */
@Table(name = "theme_collect")
@Entity
public class ThemeCollect implements EntityClass<Integer>,TableTimeData {

    private Integer id;

    private int userId;

    private String deviceId;

    private int themeNo;

    /** 软删 */
    private boolean deleted;

    private DateTime collectTime;

    private DateTime createDate;

    private DateTime updateDate;

    private String themePic;

    private String themeName;

    public ThemeCollect(){
        this.collectTime = DateUtils.current();
    }

    public ThemeCollect(int userId, int themeNo){
        this.userId = userId;
        this.themeNo = themeNo;
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

    @Column(name = "themeNo")
    public int getThemeNo() {
        return themeNo;
    }

    public void setThemeNo(int themeNo) {
        this.themeNo = themeNo;
    }

    @Column(name = "deviceId")
    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @Transient
    public String getThemePic() {
        return themePic;
    }

    public void setThemePic(String themePic) {
        this.themePic = themePic;
    }

    @Transient
    public String getThemeName() {
        return themeName;
    }

    public void setThemeName(String themeName) {
        this.themeName = themeName;
    }
}
