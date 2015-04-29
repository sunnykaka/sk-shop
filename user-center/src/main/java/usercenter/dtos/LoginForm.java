package usercenter.dtos;

import play.data.validation.Constraints;

import javax.validation.constraints.Pattern;

/**
 * Created by liubin on 15-4-24.
 */
public class LoginForm {

    @Constraints.Required(message = "用户名或手机不能为空")
    String passport;

    @Constraints.Required(message = "密码不能为空")
    @Constraints.MinLength(value = 4, message = "密码长度需要为4-20位")
    @Constraints.MaxLength(value = 20, message = "密码长度需要为4-20位")
    String password;

    String rememberMe;

    public String getPassport() {
        return passport;
    }

    public void setPassport(String passport) {
        this.passport = passport;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(String rememberMe) {
        this.rememberMe = rememberMe;
    }
}
