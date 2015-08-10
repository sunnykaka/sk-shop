package usercenter.models;

import common.models.utils.EntityClass;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import usercenter.constants.MarketChannel;

import javax.persistence.*;

/**
 * 登录记录
 */
@Entity
@Table(name = "login_record")
public class LoginRecord implements EntityClass<Integer> {

    private Integer id;

    private String deviceId;

    private String deviceInfo;

    //渠道
    private MarketChannel channel;

    private Integer userId;

    private User user;

    private DateTime createTime;


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

    @Column(name = "deviceId")
    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @Column(name = "deviceInfo")
    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    @Column(name = "channel")
    @Enumerated(EnumType.STRING)
    public MarketChannel getChannel() {
        return channel;
    }

    public void setChannel(MarketChannel channel) {
        this.channel = channel;
    }

    @Column(name = "createTime")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(DateTime createTime) {
        this.createTime = createTime;
    }

    @Column(name = "userId")
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
