package dtos;

/**
 * Created by liubin on 15-8-6.
 */
public class UserToken {

    private String accessToken;

    private String refreshToken;

    private int accessTokenExpiresInSeconds;

    private int refreshTokenExpiresInSeconds;

    public UserToken(String accessToken, String refreshToken, int accessTokenExpiresInSeconds, int refreshTokenExpiresInSeconds) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpiresInSeconds = accessTokenExpiresInSeconds;
        this.refreshTokenExpiresInSeconds = refreshTokenExpiresInSeconds;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public int getAccessTokenExpiresInSeconds() {
        return accessTokenExpiresInSeconds;
    }

    public int getRefreshTokenExpiresInSeconds() {
        return refreshTokenExpiresInSeconds;
    }
}
