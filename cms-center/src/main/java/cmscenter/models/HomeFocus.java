package cmscenter.models;

import cmscenter.constants.HomeFocusPageType;
import cmscenter.constants.HomeFocusType;
import common.models.utils.EntityClass;
import common.models.utils.TableTimeData;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;

/**
 * app首页图片位置
 *
 * Created by zhb on 2015/10/28.
 */
@Entity
@Table(name = "HomeFocus")
public class HomeFocus implements EntityClass<Integer>,TableTimeData {

    private Integer id;

    private String name;

    private String picUrl;

    private String content;

    private HomeFocusType type;

    private int priority;

    private HomeFocusPageType pageType;

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

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "picUrl")
    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    @Column(name = "content")
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    public HomeFocusType getType() {
        return type;
    }

    public void setType(HomeFocusType type) {
        this.type = type;
    }

    @Column(name = "priority")
    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Column(name = "pageType")
    @Enumerated(EnumType.STRING)
    public HomeFocusPageType getPageType() {
        return pageType;
    }

    public void setPageType(HomeFocusPageType pageType) {
        this.pageType = pageType;
    }

    @Column(name = "createTime")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Override
    public DateTime getCreateDate() {
        return createDate;
    }

    @Override
    public void setCreateDate(DateTime createDate) {
        this.createDate = createDate;
    }

    @Column(name = "updateTime")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Override
    public DateTime getUpdateDate() {
        return updateDate;
    }

    @Override
    public void setUpdateDate(DateTime updateDate) {
        this.updateDate = updateDate;
    }
}
