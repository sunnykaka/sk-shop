package usercenter.dtos;

import play.data.validation.Constraints;

/**
 * Created by zhb on 15-4-30.
 */
public class CodeForm {

    @Constraints.Required(message = "手机验证码不能为空")
    @Constraints.MinLength(value = 6, message = "手机验证码需要为6位")
    @Constraints.MaxLength(value = 6, message = "手机验证码需要为6位")
    private String verificationCode;

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }
}
