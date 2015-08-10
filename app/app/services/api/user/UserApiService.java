package services.api.user;

import api.response.user.LoginResult;
import api.response.user.UserDataDto;
import api.response.user.UserDto;
import common.exceptions.AppBusinessException;
import common.exceptions.ErrorCode;
import dtos.UserToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usercenter.constants.MarketChannel;
import usercenter.dtos.LoginForm;
import usercenter.dtos.RegisterForm;
import usercenter.models.User;
import usercenter.models.UserData;
import usercenter.services.UserDataService;
import usercenter.services.UserService;

/**
 * Created by liubin on 15-8-3.
 */
@Service
public class UserApiService {

    @Autowired
    UserService userService;

    @Autowired
    UserDataService userDataService;

    @Autowired
    UserTokenProvider userTokenProvider;

    @Transactional
    public LoginResult login(LoginForm loginForm) {

        User user = userService.authenticate(loginForm.getPassport(), loginForm.getPassword());
        if(user == null) {
            throw new AppBusinessException(ErrorCode.InvalidArgument, "用户名或密码错误");
        }

        userService.doLogin(user, loginForm.retrieveLoginInfo());

        return createLoginResult(user);
    }

    @Transactional
    public LoginResult register(RegisterForm registerForm, String ip) {

        User user = userService.register(registerForm, ip);

        userService.doLogin(user, new LoginForm.LoginInfo(registerForm.getDeviceId(), registerForm.getChannel(), registerForm.getDeviceInfo()));

        return createLoginResult(user);
    }


    /**
     * 返回登录结果
     * @param user
     * @return
     */
    private LoginResult createLoginResult(User user) {

        UserDto userDto = buildUserDto(user);

        UserToken userToken = userTokenProvider.createToken(user);

        return new LoginResult(userToken.getAccessToken(), userToken.getRefreshToken(),
                userToken.getAccessTokenExpiresInSeconds(), userDto);
    }


    private UserDto buildUserDto(User user) {

        UserDto userDto = new UserDto();
        userDto.setAccountType(user.getAccountType());
        userDto.setDeleted(user.isDeleted());
        userDto.setEmail(user.getEmail());
        userDto.setHasForbidden(user.isHasForbidden());
        userDto.setPhone(user.getPhone());
        userDto.setRegisterDate(user.getRegisterDate());
        userDto.setRegisterIP(user.getRegisterIP());
        userDto.setUserName(user.getUserName());
        userDto.setUserData(buildUserDataDto(userDataService.findByUserId(user.getId())));

        return userDto;
    }

    private UserDataDto buildUserDataDto(UserData userData) {
        if(userData == null) return null;

        UserDataDto userDataDto = new UserDataDto();
        userDataDto.setArea(userData.getArea());
        userDataDto.setBirthday(userData.getBirthday());
        userDataDto.setBirthdayD(userData.getBirthdayD());
        userDataDto.setBirthdayM(userData.getBirthdayM());
        userDataDto.setBirthdayY(userData.getBirthdayY());
        userDataDto.setCity(userData.getCity());
        userDataDto.setLocation(userData.getLocation());
        userDataDto.setName(userData.getName());
        userDataDto.setProvince(userData.getProvince());
        userDataDto.setSex(userData.getSex());

        return userDataDto;

    }

}
