package usercenter.models.address;

import common.models.utils.EntityClass;
import common.models.utils.TableTimeData;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;

/**
 * 用户的地址对象
 * User: Asion
 * Date: 11-10-11
 * Time: 下午12:41
 */
@Entity
@Table(name = "address")
public class Address implements EntityClass<Integer>,TableTimeData {

    /**
     * 默认地址
     */
    public static final boolean DEFAULT_ADDRESS_TRUE = true;

    /**
     * 非默认地址
     */
    public static final boolean DEFAULT_ADDRESS_FALSE = false;

    /**
     * 地址Id
     */
    private Integer id;


    /**
     * 是谁的地址
     */
    private int userId;

    /**
     * 收获人姓名
     */
    private String name;

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

    /**
     * 移动电话
     */
    private String mobile;

    /**
     * 固定电话
     */
    private String telephone;

    /**
     * 电子邮件
     */
    private String email;

    /**
     * 邮编
     */
    private String zipCode = "";

    /**
     * 是否是缺省地址
     */
    private boolean defaultAddress;

    /**
     * 使用频率
     */
    private int frequency;

    private boolean deleted;

    private DateTime createDate;

    private DateTime updateDate;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(province).append(location);
        return sb.toString();
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

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "province")
    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    @Column(name = "location")
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Column(name = "mobile")
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Column(name = "telephone")
    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    @Column(name = "email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "zipCode")
    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    @Column(name = "userId")
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Column(name = "defaultAddress")
    public boolean isDefaultAddress() {
        return defaultAddress;
    }

    public void setDefaultAddress(boolean defaultAddress) {
        this.defaultAddress = defaultAddress;
    }

    @Column(name = "frequency")
    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    @Column(name = "isDelete")
    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
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
}
