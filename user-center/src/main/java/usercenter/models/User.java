package usercenter.models;

import common.models.utils.EntityClass;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import usercenter.constants.AccountType;

import javax.persistence.*;
import java.util.Date;

/**
 * <pre>
 * 网站的用户对象.
 *
 * 用户目前有三种: 普通注册用户, 第三方登录用户, 在其他平台购买后由客服手动生成的用户.
 * 用户信息中最主要的是 用户名、邮箱、手机号 和 密码, 用户名、邮箱、手机号 都有业务上的唯一性(db 中某些字段可为空, 因此并没有唯一约束)
 *
 * 1.用户普通注册时可以使用 <用户名/邮箱/手机号> 中的任意一种, 并且用户名是一定会保存的.
 * 此时, 若输入是邮箱, 则此信息写进邮箱, 手机号同理.
 *
 * 2.第三方登录过来的用户是特殊的. 没有任何信息. 此时的约定是这样: 用户Id 与 用户名 相同.
 * password 在 db 中有非空约束, 其值与第三方平台返回的 outId 保持一致.
 *
 * 3.由客服手动输入的用户数据, 只有手机号. 此时的约定: 用户名 与 手机号一致, 并随机生成一个密码
 *
 * 关于用户信息更改:
 *   用户名 >> 当与邮箱相同、与手机号相同、或者跟用户Id 相同(第三方登录过来的)时, 则可以设置;
 *   密码 >> 当用户Id 和 用户名相同(只发生在第三方登录的情况)时, 则可以设置;
 *   邮箱 >> 为空时, 则可以设置. 这一点也说明了: <span style="color:red">邮箱只能设置一次</span>;
 *   手机号 >> 可以随时设置.
 * </pre>
 */
public class User implements EntityClass<Integer> {

    private static final long serialVersionUID = 7993724590252729908L;

    private Integer id;

    private AccountType accountType;

    private String userName;

    private String email;

    private String phone;

    private transient String password;

    /**
     * 目前仅代表第三方用户是否有完善资料, 不代表邮件或手机号激活. 目前的注册用户没有激活的步骤
     */
    private boolean isActive = false;

    private String registerDate;

    private String registerIP;

    /**
     * 最新的登录时间
     */
    private DateTime loginTime;

    /**
     * 登录总次数
     */
    private int loginCount;

    //是否已删除
    private boolean isDelete;

    //是否为禁用
    private boolean hasForbidden;

    //总积分
    private long pointTotal;

    //累计消费金额
    private long expenseTotal;


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

    @Column(name = "accountType")
    @Enumerated(EnumType.STRING)
    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    @Column(name = "userName")
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Column(name = "email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "phone")
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Column(name = "password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name = "isActive")
    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    @Column(name = "registerDate")
    public String getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(String registerDate) {
        this.registerDate = registerDate;
    }

    @Column(name = "registerIP")
    public String getRegisterIP() {
        return registerIP;
    }

    public void setRegisterIP(String registerIP) {
        this.registerIP = registerIP;
    }

    @Column(name = "loginTime")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(DateTime loginTime) {
        this.loginTime = loginTime;
    }

    @Column(name = "loginCount")
    public int getLoginCount() {
        return loginCount;
    }

    public void setLoginCount(int loginCount) {
        this.loginCount = loginCount;
    }

    @Column(name = "isDelete")
    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean isDelete) {
        this.isDelete = isDelete;
    }

    @Column(name = "hasForbidden")
    public boolean isHasForbidden() {
        return hasForbidden;
    }

    public void setHasForbidden(boolean hasForbidden) {
        this.hasForbidden = hasForbidden;
    }

    @Column(name = "pointTotal")
    public long getPointTotal() {
        return pointTotal;
    }

    public void setPointTotal(long pointTotal) {
        this.pointTotal = pointTotal;
    }

    @Column(name = "expenseTotal")
    public long getExpenseTotal() {
        return expenseTotal;
    }

    public void setExpenseTotal(long expenseTotal) {
        this.expenseTotal = expenseTotal;
    }
}
