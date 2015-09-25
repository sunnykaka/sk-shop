package api.response.user;

import org.apache.commons.lang3.StringUtils;
import usercenter.models.UserData;

/**
 * Created by Zhb on 2015/9/24.
 */
public class UserDateHomeDto {

    private String name;

    private String mobile;

    public static UserDateHomeDto build(UserData userData){

        UserDateHomeDto userDateHomeDto = new UserDateHomeDto();

        userDateHomeDto.setMobile(userData.getUser().getPhone());

        if(StringUtils.isEmpty(userData.getName())){
            userDateHomeDto.setName(userData.getUser().getUserName());
        }else{
            userDateHomeDto.setName(userData.getName());
        }


        return userDateHomeDto;

    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
