package usercenter.dtos;

import common.utils.RegExpUtils;
import play.data.validation.Constraints;

/**
 * Created by zhb on 15-4-30.
 */
public class PhoneCodeForm {

    @Constraints.Required(message = "手机验证码不能为空")
    @Constraints.MinLength(value = 6, message = "手机验证码需要为6位")
    @Constraints.MaxLength(value = 6, message = "手机验证码需要为6位")
    String verificationCode;

    @Constraints.Required(message = "手机不能为空")
    @Constraints.Pattern(value = RegExpUtils.PHONE_REG_EXP, message = "请输入正确的手机号码")
    String phone;

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
