package usercenter.dtos;

import play.data.validation.Constraints;

/**
 * Created by zhb on 15-4-30.
 */
public class RecoverPswForm {

    private String phone;

    @Constraints.Required(message = "新密码不能为空")
    @Constraints.MinLength(value = 4, message = "新密码长度需要为4-20位")
    @Constraints.MaxLength(value = 20, message = "新密码长度需要为4-20位")
    private String password;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
