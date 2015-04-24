package usercenter.dtos;

import play.data.validation.Constraints;

import javax.validation.constraints.Pattern;

/**
 * Created by liubin on 15-4-24.
 */
public class LoginForm {

    @Constraints.Required(message = "用户名不能为空")
    @Pattern(regexp = "^[A-Za-z0-9_]{4,20}$", message = "4-20位字符，支持字母/汉字/数字/下划线组合")
    String username;

    @Constraints.Required(message = "密码不能为空")
    @Constraints.MinLength(value = 4, message = "密码长度需要为4-20位")
    @Constraints.MaxLength(value = 20, message = "密码长度需要为4-20位")
    String password;

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
}
