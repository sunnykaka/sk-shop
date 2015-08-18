package common.models;


import common.constants.MessageJobSource;
import common.constants.MessageJobType;
import common.models.utils.EntityClass;
import common.models.utils.OperableData;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;


/**
 * 邮件和短信发送任务
 */
@Entity
@Table(name = "message_job")
public class MessageJob implements EntityClass<Integer>, OperableData {

    private Integer id;

    private String contact;

    private String content;

    private MessageJobType type;

    private boolean processed;

    private DateTime createTime;

    private DateTime updateTime;

    private String title;

    private DateTime targetTime;

    private String processInfo;

    private MessageJobSource source;

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

    @Column(name = "contact")
    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
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
    public MessageJobType getType() {
        return type;
    }

    public void setType(MessageJobType type) {
        this.type = type;
    }

    @Column(name = "processed")
    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
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

    @Column(name = "title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(name = "targetTime")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getTargetTime() {
        return targetTime;
    }

    public void setTargetTime(DateTime targetTime) {
        this.targetTime = targetTime;
    }

    @Column(name = "processInfo")
    public String getProcessInfo() {
        return processInfo;
    }

    public void setProcessInfo(String processInfo) {
        this.processInfo = processInfo;
    }

    @Column(name = "source")
    @Enumerated(EnumType.STRING)
    public MessageJobSource getSource() {
        return source;
    }

    public void setSource(MessageJobSource source) {
        this.source = source;
    }
}
