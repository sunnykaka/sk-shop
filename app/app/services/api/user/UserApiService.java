package services.api.user;

import api.response.user.LoginResult;
import api.response.user.UserDto;
import common.exceptions.AppBusinessException;
import common.exceptions.ErrorCode;
import dtos.UserToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usercenter.dtos.LoginForm;
import usercenter.dtos.RegisterForm;
import usercenter.models.User;
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

    public void logout(String accessToken) {
        userTokenProvider.deleteByAccessToken(accessToken);
    }


    /**
     * 返回登录结果
     * @param user
     * @return
     */
    private LoginResult createLoginResult(User user) {

        UserDto userDto = UserDto.build(user, userDataService.findByUserId(user.getId()));

        UserToken userToken = userTokenProvider.createToken(user);

        return new LoginResult(userToken.getAccessToken(), userToken.getRefreshToken(),
                userToken.getAccessTokenExpiresInSeconds(), userDto);
    }

}
