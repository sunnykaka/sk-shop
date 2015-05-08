package usercenter.dtos;

import play.data.validation.Constraints;

/**
 * 更新用户资料
 *
 * Created by zhb on 15-5-7.
 */
public class UserDataForm {

    @Constraints.MaxLength(value = 40, message = "输入超过最大40字符限制")
    private String name;

    private int sex;

    @Constraints.MaxLength(value = 50, message = "输入超过最大50字符限制")
    private String province;

    @Constraints.MaxLength(value = 50, message = "输入超过最大50字符限制")
    private String city;

    @Constraints.MaxLength(value = 50, message = "输入超过最大50字符限制")
    private String area;

    @Constraints.MaxLength(value = 200, message = "输入超过最大200字符限制")
    private String location;

    /** 生日拆分  年 */
    @Constraints.MaxLength(value = 4, message = "输入超过最大4字符限制")
    private String birthdayY;

    /** 生日拆分  月 */
    @Constraints.MaxLength(value = 2, message = "输入超过最大2字符限制")
    private String birthdayM;

    /** 生日拆分  日 */
    @Constraints.MaxLength(value = 2, message = "输入超过最大2字符限制")
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
