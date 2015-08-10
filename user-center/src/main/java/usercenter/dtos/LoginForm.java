package usercenter.dtos;

import play.data.validation.Constraints;
import usercenter.constants.MarketChannel;

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

    String deviceId;

    String channel;

    String deviceInfo;


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

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public LoginInfo retrieveLoginInfo() {
        return new LoginInfo(deviceId, channel, deviceInfo);
    }

    public static class LoginInfo {
        String deviceId;

        String channel;

        String deviceInfo;

        public LoginInfo(String deviceId, String channel, String deviceInfo) {
            this.deviceId = deviceId;
            this.channel = channel;
            this.deviceInfo = deviceInfo;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public String getChannel() {
            return channel;
        }

        public String getDeviceInfo() {
            return deviceInfo;
        }
    }
}
