package services.api.user;

import api.response.user.RefreshTokenResult;
import common.utils.play.BaseGlobal;
import dtos.UserToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import play.Play;
import play.cache.CacheApi;
import usercenter.models.User;
import usercenter.services.UserService;

import java.util.Optional;

import static usercenter.utils.UserTokenUtils.*;

/**
 * Created by liubin on 15-8-6.
 */
@Service
public class UserTokenProvider {

    public static final String ACCESS_TOKEN_KEY = "accessToken";

    @Autowired
    private UserService userService;


    private static CacheApi cacheApi() {
        return BaseGlobal.injector.instanceOf(CacheApi.class);
    }

    public UserToken createToken(User user) {
        String accessToken = generateToken(user.getUserName());
        String refreshToken = generateToken(user.getUserName());
        UserToken userToken = createUserToken(accessToken, refreshToken);

        saveUserToken(user.getId(), user.getUserName(), userToken);

        return userToken;
    }

    private UserToken createUserToken(String accessToken, String refreshToken) {
        int accessTokenExpiresInSeconds = Play.application().configuration().getInt("shop.accessTokenExpiresIn", 604800);
        int refreshTokenExpiresInSeconds = Play.application().configuration().getInt("shop.refreshTokenExpiresIn", 2592000);
        return new UserToken(accessToken, refreshToken, accessTokenExpiresInSeconds, refreshTokenExpiresInSeconds);
    }

    /**
     *
     * "user_tokens:access_token:$accessToken:$refreshToken,$userId,$username"
     * "user_tokens:refresh_token:$refreshToken:$accessToken,$userId,$username"
     *
     * @param user
     * @param userToken
     */
    private void saveUserToken(Integer userId, String username, UserToken userToken) {

        cacheApi().set(
                getAccessTokenKey(userToken.getAccessToken()),
                createTokenValueInCache(userToken.getRefreshToken(), userId, username),
                userToken.getAccessTokenExpiresInSeconds());
        cacheApi().set(
                getRefreshTokenKey(userToken.getRefreshToken()),
                createTokenValueInCache(userToken.getAccessToken(), userId, username),
                userToken.getRefreshTokenExpiresInSeconds());

    }

    public Optional<RefreshTokenResult> refreshToken(String refreshToken) {
        if(StringUtils.isNotBlank(refreshToken)) {
            String[] array = retrieveTokenValueFromCache(cacheApi().get(getRefreshTokenKey(refreshToken)));
            if(array.length == 3) {
                String accessToken = array[0];
                String username = array[2];

                Optional<Integer> userId = userService.retrieveUserIdByAccessToken(accessToken);
                if(!userId.isPresent()) {
                    userId = Optional.of(Integer.parseInt(array[1]));
                    //accessToken已过期,生成新的
                    accessToken = generateToken(username);
                }

                UserToken userToken = createUserToken(accessToken, refreshToken);
                saveUserToken(userId.get(), username, userToken);

                return Optional.of(new RefreshTokenResult(accessToken, refreshToken, userToken.getAccessTokenExpiresInSeconds()));
            }
        }

        return Optional.empty();
    }

    public void deleteByAccessToken(String accessToken) {
        if(StringUtils.isNoneBlank(accessToken)) {
            String accessTokenKey = getAccessTokenKey(accessToken);
            String[] array = retrieveTokenValueFromCache(cacheApi().get(accessTokenKey));
            if(array.length == 3) {
                String refreshToken = array[0];
                if(StringUtils.isNoneBlank(refreshToken)) {
                    cacheApi().remove(getRefreshTokenKey(refreshToken));
                }
            }
            cacheApi().remove(accessTokenKey);
        }

    }
}
