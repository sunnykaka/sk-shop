package usercenter.models;

import common.models.utils.EntityClass;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import usercenter.constants.AccountType;

import javax.persistence.*;
import java.util.Date;

/**
 * 第三方帐号信息
 * User: Alec
 * Date: 12-12-11
 * Time: 下午3:12
 * To change this template use File | Settings | File Templates.
 */
public class UserOuter implements EntityClass<Integer> {

    private Integer id;
    /**
     * 外部账号认证系统的跟踪ID
     */
    private String outerId;

    //账户类型，可区别出来自什么网站，比如QQ，sina，KRQ代表我们自己，不持久，是动态构造的
    private AccountType accountType;

    private int userId;

    private User user;

    private DateTime createDate;

    private boolean isLocked;

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

    @Column(name = "outerId")
    public String getOuterId() {
        return outerId;
    }

    public void setOuterId(String outerId) {
        this.outerId = outerId;
    }

    @Column(name = "accountType")
    @Enumerated(EnumType.STRING)
    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
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

    @Column(name = "createDate")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(DateTime createDate) {
        this.createDate = createDate;
    }

    @Column(name = "isLocked")
    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }
}
