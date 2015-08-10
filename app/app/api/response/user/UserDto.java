package api.response.user;

import usercenter.constants.AccountType;

/**
 * Created by liubin on 15-8-3.
 */
public class UserDto {

    private AccountType accountType;

    private String userName;

    private String email;

    private String phone;

    private String registerDate;

    private String registerIP;

    //是否已删除
    private boolean deleted;

    //是否为禁用
    private boolean hasForbidden;

    private UserDataDto userData;


    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(String registerDate) {
        this.registerDate = registerDate;
    }

    public String getRegisterIP() {
        return registerIP;
    }

    public void setRegisterIP(String registerIP) {
        this.registerIP = registerIP;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isHasForbidden() {
        return hasForbidden;
    }

    public void setHasForbidden(boolean hasForbidden) {
        this.hasForbidden = hasForbidden;
    }

    public UserDataDto getUserData() {
        return userData;
    }

    public void setUserData(UserDataDto userData) {
        this.userData = userData;
    }
}
