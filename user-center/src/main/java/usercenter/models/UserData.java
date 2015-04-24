package usercenter.models;

import common.models.utils.EntityClass;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户基本资料表
 *
 * @author alec
 * @version 1.0.0
 * @since 2013-1-15 下午09:14:58
 */
@Entity
@Table(name = "userdata")
public class UserData implements EntityClass<Integer>{

    private static final long serialVersionUID = 7993724590252729908L;

    private Integer id;

    private Integer userId;

    private User user;

    private String name;

    private int sex;

    private String phoneNumber;

    private int familyNumber;

    private int hasMarried;

    private String birthday;

    private boolean isDelete;

    private DateTime createDate;

    private DateTime updateDate;

    private static Map<Integer, String> userSex = new HashMap<>();
    private static Map<Integer, String> userHasMarried = new HashMap<>();
    private static Map<Integer, String> userFamilyNumber = new HashMap<>();

    static {
        userSex.put(0, "保密");
        userSex.put(1, "男");
        userSex.put(2, "女");

        userHasMarried.put(0, "保密");
        userHasMarried.put(1, "未婚");
        userHasMarried.put(2, "已婚");

        userFamilyNumber.put(0, "保密");
        userFamilyNumber.put(1, "1-2人");
        userFamilyNumber.put(2, "2-4人");
        userFamilyNumber.put(3, "4人以上");
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
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "sex")
    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    @Column(name = "phoneNumber")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Column(name = "familyNumber")
    public int getFamilyNumber() {
        return familyNumber;
    }

    public void setFamilyNumber(int familyNumber) {
        this.familyNumber = familyNumber;
    }

    @Column(name = "hasMarried")
    public int getHasMarried() {
        return hasMarried;
    }

    public void setHasMarried(int hasMarried) {
        this.hasMarried = hasMarried;
    }

    @Column(name = "birthday")
    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    @Column(name = "isDelete")
    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean isDelete) {
        this.isDelete = isDelete;
    }

    @Column(name = "createDate")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(DateTime createDate) {
        this.createDate = createDate;
    }

    @Column(name = "updateDate")
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(DateTime updateDate) {
        this.updateDate = updateDate;
    }
}
