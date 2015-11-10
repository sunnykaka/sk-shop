package common.utils;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * Created by liubin on 15-10-14.
 */
public class TestUtils {

    /**
     * 创建一个虚拟用户信息
     * @return
     */
    public static UserRegisterInfo mockUserRegisterInfo() {
        String phone = "1" + RandomStringUtils.randomNumeric(10);
        String username = RandomStringUtils.randomAlphabetic(10);
        String password = "111111";

        return new UserRegisterInfo(phone, username, password);
    }

    public static class UserRegisterInfo {
        public String phone;
        public String username;
        public String password;

        public UserRegisterInfo(String phone, String username, String password) {
            this.phone = phone;
            this.username = username;
            this.password = password;
        }
    }

}
