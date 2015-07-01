package usercenter.dtos;

import common.utils.RegExpUtils;
import play.data.validation.Constraints;

/**
 * Created by liubin on 15-4-24.
 */
public class RegisterForm {

    @Constraints.Required(message = "用户名不能为空")
    @Constraints.Pattern(value = RegExpUtils.USERNAME_REG_EXP, message = "用户名请输入4-20位，可由中文、英文或数字组成")
    String username;

    @Constraints.Required(message = "密码不能为空")
    @Constraints.MinLength(value = 4, message = "密码长度需要为4-20位")
    @Constraints.MaxLength(value = 20, message = "密码长度需要为4-20位")
    String password;

    @Constraints.Required(message = "手机验证码不能为空")
    @Constraints.MinLength(value = 6, message = "手机验证码长度不符合要求")
    @Constraints.MaxLength(value = 6, message = "手机验证码长度不符合要求")
    String verificationCode;

    @Constraints.Required(message = "手机不能为空")
    @Constraints.Pattern(value = RegExpUtils.PHONE_REG_EXP, message = "请输入正确的手机号码")
    String phone;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}
