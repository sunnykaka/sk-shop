package usercenter.dtos;

import play.data.validation.Constraints;

/**
 * Created by zhb on 15-4-30.
 */
public class ChangePswForm {

    @Constraints.Required(message = "密码不能为空")
    @Constraints.MinLength(value = 4, message = "密码长度需要为4-20位")
    @Constraints.MaxLength(value = 20, message = "密码长度需要为4-20位")
    private String password;

    @Constraints.Required(message = "新密码不能为空")
    @Constraints.MinLength(value = 4, message = "新密码长度需要为4-20位")
    @Constraints.MaxLength(value = 20, message = "新密码长度需要为4-20位")
    private String newPassword;

    @Constraints.Required(message = "验证密码不能为空")
    @Constraints.MinLength(value = 4, message = "验证密码长度需要为4-20位")
    @Constraints.MaxLength(value = 20, message = "验证密码长度需要为4-20位")
    private String rePassword;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getRePassword() {
        return rePassword;
    }

    public void setRePassword(String rePassword) {
        this.rePassword = rePassword;
    }
}
