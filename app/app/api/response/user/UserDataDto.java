package api.response.user;

/**
 * 用户基本资料表
 */
public class UserDataDto {

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

    /** 生日拆分  年 */
    private String birthdayY;

    /** 生日拆分  月 */
    private String birthdayM;

    /** 生日拆分  日 */
    private String birthdayD;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBirthdayY() {
        return birthdayY;
    }

    public void setBirthdayY(String birthdayY) {
        this.birthdayY = birthdayY;
    }

    public String getBirthdayM() {
        return birthdayM;
    }

    public void setBirthdayM(String birthdayM) {
        this.birthdayM = birthdayM;
    }

    public String getBirthdayD() {
        return birthdayD;
    }

    public void setBirthdayD(String birthdayD) {
        this.birthdayD = birthdayD;
    }
}
