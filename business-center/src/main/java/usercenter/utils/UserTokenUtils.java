package usercenter.utils;

import common.utils.DateUtils;
import common.utils.RedisUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.Charset;
import java.util.Base64;

/**
 * Created by liubin on 15-11-20.
 */
public class UserTokenUtils {

    public static Charset DEFAULT_CHARSET = Charset.forName("ISO8859-1");

    public static String getRefreshTokenKey(String refreshToken) {
        return RedisUtils.buildKey("user_tokens", "refresh_token", refreshToken);
    }

    public static String getAccessTokenKey(String accessToken) {
        return RedisUtils.buildKey("user_tokens", "access_token", accessToken);
    }

    public static String generateToken(String subject) {
        String keySource = subject + DateUtils.current().getMillis() + RandomStringUtils.randomAlphanumeric(8);
        byte [] tokenByte = Base64.getEncoder().encode(keySource.getBytes(DEFAULT_CHARSET));
        return new String(tokenByte, DEFAULT_CHARSET);
    }

    public static String createTokenValueInCache(String token, Integer userId, String username) {
        return token + "," + userId + "," + username;
    }

    public static String[] retrieveTokenValueFromCache(String value) {
        if(StringUtils.isNotBlank(value)) {
            return value.split(",");
        }
        return new String[0];
    }


}
