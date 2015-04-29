package usercenter.domain;

import org.apache.commons.lang3.RandomStringUtils;
import usercenter.cache.UserCache;

/**
 * Created by liubin on 15-4-27.
 */
public class PhoneVerification {

    public static int PHONE_VERIFICATION_MAX_COUNT_IN_DAY = 5;
    public static int VERIFICATION_CODE_EXPIRE_TIME = 7200;
    public static int VERIFICATION_CODE_LENGTH = 6;


    private String phone;

    public PhoneVerification(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    /**
     * 根据手机生成短信验证码
     * @return
     */
    public String generatePhoneVerificationCode() {
        //判断一天之内是不是发送过5次了
        int count = UserCache.getPhoneRegisterTimeCount(phone);
        if (count >= PHONE_VERIFICATION_MAX_COUNT_IN_DAY) {
            return null;
        }

        //生成验证码
        String verificationCode = RandomStringUtils.randomNumeric(VERIFICATION_CODE_LENGTH);

        String existVerificationCode = UserCache.getPhoneVerificationCode(phone);
        if (existVerificationCode != null) {
            existVerificationCode += ":" + verificationCode;
        } else {
            existVerificationCode = verificationCode;
        }
        UserCache.setPhoneVerificationCode(phone, existVerificationCode, VERIFICATION_CODE_EXPIRE_TIME);

        return verificationCode;

    }

    /**
     * 校验短信验证码是否有效
     * @param verificationCode
     * @return
     */
    public boolean verifyCode(String verificationCode) {
        if(verificationCode.length() != VERIFICATION_CODE_LENGTH) {
            return false;
        }
        String verificationCodeInCache = UserCache.getPhoneVerificationCode(phone);
        if(verificationCodeInCache == null) {
            return false;
        }
        return verificationCodeInCache.contains(verificationCode);
    }


}
