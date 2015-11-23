package usercenter.dtos;

import common.utils.RegExpUtils;
import play.data.validation.Constraints;

/**
 * 通过手机，找回密码
 *
 * Created by zhb on 15-4-30.
 */
public class RecoverCodeForm {

    @Constraints.Required(message = "手机验证码不能为空")
    @Constraints.MinLength(value = 4, message = "手机验证码需要为4位")
    @Constraints.MaxLength(value = 4, message = "手机验证码需要为4位")
    String imageCode;

    @Constraints.Required(message = "手机不能为空")
    @Constraints.Pattern(value = RegExpUtils.PHONE_REG_EXP, message = "请输入正确的手机号码")
    String phone;

    public String getImageCode() {
        return imageCode;
    }

    public void setImageCode(String imageCode) {
        this.imageCode = imageCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
