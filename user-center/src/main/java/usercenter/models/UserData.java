package usercenter.models;

import common.models.utils.EntityClass;
import common.models.utils.TableTimeData;
import org.apache.commons.lang3.StringUtils;
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
public class UserData implements EntityClass<Integer>,TableTimeData {

    private static final long serialVersionUID = 7993724590252729908L;

    private Integer id;

    private Integer userId;

    private User user;

    private String name;

    private int sex;

    private String birthday;

    /**
     * 省份，比如浙江
     */
    private String province;

    private String city;

    private String area;

    /**
     * 具体位置，到门牌号
     */
    private String location;

    private DateTime createDate;

    private DateTime updateDate;

    /** 生日拆分  年 */
    private String birthdayY;

    /** 生日拆分  月 */
    private String birthdayM;

    /** 生日拆分  日 */
    private String birthdayD;

    private static Map<Integer, String> userSex = new HashMap<>();

    static {
        userSex.put(0, "保密");
        userSex.put(1, "男");
        userSex.put(2, "女");
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
    @JoinColumn(name = "userId",referencedColumnName = "id",insertable = false, updatable = false)
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

    @Column(name = "birthday")
    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    @Column(name = "province")
    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    @Column(name = "city")
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Column(name = "area")
    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    @Column(name = "location")
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    @Transient
    public String getBirthdayY() {
        return birthdayY;
    }

    public void setBirthdayY(String birthdayY) {
        this.birthdayY = birthdayY;
    }

    @Transient
    public String getBirthdayM() {
        return birthdayM;
    }

    public void setBirthdayM(String birthdayM) {
        this.birthdayM = birthdayM;
    }

    @Transient
    public String getBirthdayD() {
        return birthdayD;
    }

    public void setBirthdayD(String birthdayD) {
        this.birthdayD = birthdayD;
    }

    public void mergerBirthday(){

        this.birthday = this.birthdayY + "-" + this.birthdayM + "-" + this.birthdayD;

    }

    public void splitBirthday(){
        if(StringUtils.isEmpty(this.birthday)){
            return;
        }

        String[] birthday = this.birthday.split("-");
        if (birthday.length > 0) {
            this.birthdayY = birthday[0];
        }
        if (birthday.length > 1) {
            this.birthdayM = birthday[1];
        }
        if (birthday.length > 2) {
            this.birthdayD = birthday[2];
        }
    }
}
