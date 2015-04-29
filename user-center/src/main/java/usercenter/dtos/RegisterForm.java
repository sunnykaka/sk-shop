package usercenter.dtos;

import play.data.validation.Constraints;

import javax.validation.constraints.Pattern;

/**
 * Created by liubin on 15-4-24.
 */
public class RegisterForm {

    @Constraints.Required(message = "用户名不能为空")
    @Pattern(regexp = "^[A-Za-z0-9_]{4,10}$", message = "4-10位字符，支持字母/汉字/数字/下划线组合")
    String username;

    @Constraints.Required(message = "密码不能为空")
    @Constraints.MinLength(value = 4, message = "密码长度需要为4-20位")
    @Constraints.MaxLength(value = 20, message = "密码长度需要为4-20位")
    String password;

    @Constraints.Required(message = "手机验证码不能为空")
    @Constraints.MinLength(value = 6, message = "手机验证码需要为6位")
    @Constraints.MaxLength(value = 6, message = "手机验证码需要为6位")
    String verificationCode;

    @Constraints.Required(message = "手机不能为空")
    @Pattern(regexp = "^[1][\\d]{10}", message = "请输入正确的手机号码")
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
