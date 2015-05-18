package usercenter.dtos;

import play.data.validation.Constraints;

import javax.validation.constraints.Pattern;

/**
 * 更新用户资料
 *
 * Created by zhb on 15-5-7.
 */
public class UserDataForm {

    @Pattern(regexp = "^[A-Za-z\u4e00-\u9fa5]{2,20}$", message = "请输入2~20个汉字或字母")
    private String name;

    private int sex;

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
