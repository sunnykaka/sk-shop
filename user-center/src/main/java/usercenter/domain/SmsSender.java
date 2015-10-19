package usercenter.domain;

import common.exceptions.AppException;
import common.exceptions.ErrorCode;
import common.utils.RegExpUtils;
import common.utils.SmsUtils;
import common.utils.play.BaseGlobal;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import play.Logger;
import usercenter.cache.UserCache;

/**
 * Created by liubin on 15-4-27.
 */
public class SmsSender {

    public static int SEND_MESSAGE_MAX_TIMES_IN_DAY = 10;
    public static int ALL_SEND_MESSAGE_MAX_TIMES_IN_DAY = 1000;
    public static int VERIFICATION_CODE_EXPIRE_TIME = 7200;
    public static int VERIFICATION_CODE_LENGTH = 6;

    public static String SECURITY_CODE = "pzmlJGvQHdry7ZLv";

    public static final String PHONE_VERIFICATION_CODE_MESSAGE_FORMAT = "您的短信验证码是%s，两小时内有效。";


    private String phone;
    private String ip;
    private Usage usage;

    public SmsSender(String phone, String ip, Usage usage) {
        this.phone = phone;
        this.usage = usage;
        this.ip = ip;
    }

    public String getPhone() {
        return phone;
    }

    public String getIp() {
        return ip;
    }

    /**
     * 根据手机生成短信验证码
     * @return
     */
    private String generatePhoneVerificationCode() {
        //判断一天之内是不是发送过10次了
        int count = UserCache.getMessageSendTimesInDay(phone, usage);
        if (count >= SEND_MESSAGE_MAX_TIMES_IN_DAY) {
            return null;
        }

//        //判断IP发送数量达到上限
//        if(BaseGlobal.isProd()) {
//            count = UserCache.getMessageSendIpCountInDay(ip, usage);
//            if (count >= SEND_MESSAGE_MAX_TIMES_IN_DAY) {
//                return null;
//            }
//        }

        //判断IP发送数量达到上限
        if(BaseGlobal.isProd()) {
            count = UserCache.getAllMessageSendTimesInDay(usage);
            if (count >= ALL_SEND_MESSAGE_MAX_TIMES_IN_DAY) {
                return null;
            }
        }

        //生成验证码
        String verificationCode = generateCode();

        String existVerificationCode = UserCache.getPhoneVerificationCode(phone, usage);
        if (existVerificationCode != null) {
            existVerificationCode += ":" + verificationCode;
        } else {
            existVerificationCode = verificationCode;
        }
        UserCache.setPhoneVerificationCode(phone, usage, existVerificationCode, VERIFICATION_CODE_EXPIRE_TIME);

        return verificationCode;

    }

    private boolean sendMessage(String message) {

        boolean success = SmsUtils.sendSms(phone, message);
        if(success) {
            UserCache.incrMessageSendTimesInDay(phone, usage);
//            UserCache.incrMessageSendIpCountInDay(ip, usage);
            UserCache.incrAllMessageSendTimesInDay(usage);
        }

        return success;
    }

    public void sendPhoneVerificationMessage() throws AppException {

        Logger.debug(String.format("发送短信请求IP[%s], phone[%s]", ip, phone));

        if(!RegExpUtils.isPhone(phone)) {
            throw new AppException(ErrorCode.InvalidArgument, "请输入有效的手机号码");
        }
        String code = generatePhoneVerificationCode();
        if(StringUtils.isBlank(code)) {
            throw new AppException(ErrorCode.Forbidden, "发送失败,发送次数超过上限");
        } else {
            if(sendMessage(String.format(PHONE_VERIFICATION_CODE_MESSAGE_FORMAT, code))) {
                return;
            }
        }

        throw new AppException("验证码发送失败");

    }

    private String generateCode() {
        return RandomStringUtils.randomNumeric(VERIFICATION_CODE_LENGTH);
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
        String verificationCodeInCache = UserCache.getPhoneVerificationCode(phone, usage);
        if(verificationCodeInCache == null) {
            return false;
        }
        if(verificationCodeInCache.contains(verificationCode)) {
            String newStr = verificationCodeInCache.replaceAll(verificationCode, "");
            UserCache.setPhoneVerificationCode(phone, usage, newStr, VERIFICATION_CODE_EXPIRE_TIME);
            return true;
        } else {
            return false;
        }
    }


    public static enum Usage {
        REGISTER, BIND
    }

}
