package services.api.user;

import api.response.user.RefreshTokenResult;
import common.utils.DateUtils;
import common.utils.RedisUtils;
import common.utils.play.BaseGlobal;
import dtos.UserToken;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import play.Logger;
import play.Play;
import play.cache.CacheApi;
import usercenter.models.User;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Optional;

/**
 * Created by liubin on 15-8-6.
 */
@Service
public class UserTokenProvider {

    private Charset DEFAULT_CHARSET = Charset.forName("ISO8859-1");
    public static final String ACCESS_TOKEN_KEY = "access_token";


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
     * "user_tokens:access_token:$accessToken:$userId"
     * "user_tokens:refresh_token:$refreshToken:$accessToken,$userId,$username"
     *
     * @param user
     * @param userToken
     */
    private void saveUserToken(Integer userId, String username, UserToken userToken) {

        cacheApi().set(
                getAccessTokenKey(userToken.getAccessToken()),
                userId,
                userToken.getAccessTokenExpiresInSeconds());
        cacheApi().set(
                getRefreshTokenKey(userToken.getRefreshToken()),
                userToken.getAccessToken() + "," + userId + "," + username,
                userToken.getRefreshTokenExpiresInSeconds());

    }


    private String getRefreshTokenKey(String refreshToken) {
        return RedisUtils.buildKey("user_tokens", "refresh_token", refreshToken);
    }

    private String getAccessTokenKey(String accessToken) {
        return RedisUtils.buildKey("user_tokens", "access_token", accessToken);
    }

    private String generateToken(String subject) {
        String keySource = subject + DateUtils.current().getMillis() + RandomStringUtils.randomAlphanumeric(8);
        byte [] tokenByte = Base64.getEncoder().encode(keySource.getBytes(DEFAULT_CHARSET));
        return new String(tokenByte, DEFAULT_CHARSET);
    }

    public Optional<Integer> retrieveUserIdByAccessToken(String accessToken) {
        if(StringUtils.isNoneBlank(accessToken)) {
            Integer userId = cacheApi().get(getAccessTokenKey(accessToken));
            return Optional.ofNullable(userId);
        }

        return Optional.empty();
    }

    public Optional<RefreshTokenResult> refreshToken(String refreshToken) {
        if(StringUtils.isNotBlank(refreshToken)) {
            String accessTokenAndUserId = cacheApi().get(getRefreshTokenKey(refreshToken));
            if(StringUtils.isNotBlank(accessTokenAndUserId)) {
                String[] array = accessTokenAndUserId.split(",");
                if(array.length == 3) {
                    String accessToken = array[0];
                    String username = array[2];

                    Optional<Integer> userId = retrieveUserIdByAccessToken(accessToken);
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
        }

        return Optional.empty();
    }
}
