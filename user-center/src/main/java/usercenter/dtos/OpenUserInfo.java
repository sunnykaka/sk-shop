package usercenter.dtos;

import usercenter.constants.AccountType;
import usercenter.constants.Gender;

import java.util.Map;

/**
 * Created by liubin on 15-5-25.
 */
public class OpenUserInfo {

    private String unionId;

    private String openId;

    private String nickName;

    private Gender gender;

    private String country;

    private String province;

    private String city;

    private String avatar;

    private AccountType accountType;

    private OpenUserInfo() {}

    public static OpenUserInfo fromWeixin(Map<String, Object> map) {
        OpenUserInfo info = new OpenUserInfo();
        info.accountType = AccountType.WeiXin;

        info.unionId = map.get("unionid").toString();
        info.openId = map.get("openid").toString();
        info.nickName = map.get("nickname").toString();
        Integer sex = (Integer)map.getOrDefault("sex", 0);
        Gender gender = Gender.UNKNOWN;
        if(sex == 1) {
            gender = Gender.MALE;
        } else if(sex == 2) {
            gender = Gender.FEMALE;
        }
        info.gender = gender;
//        info.country = (String)map.getOrDefault("country", "");
//        info.province = (String)map.getOrDefault("province", "");
//        info.city = (String)map.getOrDefault("city", "");
        info.avatar = (String)map.getOrDefault("headimgurl", "");

        return info;
    }

    public static OpenUserInfo fromQQ(Map<String, Object> map, String openId) {
        OpenUserInfo info = new OpenUserInfo();
        info.accountType = AccountType.QQ;

        info.unionId = openId;
        info.openId = openId;
        info.nickName = map.get("nickname").toString();
        String genderStr = (String)map.getOrDefault("gender", "男");
        Gender gender = Gender.UNKNOWN;
        if(genderStr.trim().equals("男")) {
            gender = Gender.MALE;
        } else if(genderStr.trim().equals("女")) {
            gender = Gender.FEMALE;
        }
        info.gender = gender;
        info.avatar = (String)map.getOrDefault("figureurl_qq_1", "");

        return info;
    }

    public static OpenUserInfo fromWeibo(Map<String, Object> map) {
        OpenUserInfo info = new OpenUserInfo();
        info.accountType = AccountType.Sina;

        info.unionId = map.get("id").toString();
        info.openId = map.get("id").toString();
        info.nickName = map.get("screen_name").toString();
        String genderStr = (String)map.getOrDefault("gender", "n");
        Gender gender = Gender.UNKNOWN;
        if(genderStr.trim().equals("m")) {
            gender = Gender.MALE;
        } else if(genderStr.trim().equals("f")) {
            gender = Gender.FEMALE;
        }
        info.gender = gender;
        info.avatar = (String)map.getOrDefault("profile_image_url", "");

        return info;
    }



    public String getUnionId() {
        return unionId;
    }

    public String getOpenId() {
        return openId;
    }

    public String getNickName() {
        return nickName;
    }

    public Gender getGender() {
        return gender;
    }

    public String getCountry() {
        return country;
    }

    public String getProvince() {
        return province;
    }

    public String getCity() {
        return city;
    }

    public String getAvatar() {
        return avatar;
    }

    public AccountType getAccountType() {
        return accountType;
    }
}
