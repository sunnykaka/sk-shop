package api.response.user;

/**
 * Created by liubin on 15-8-3.
 */
public class LoginResult {

    private String accessToken;

    private String refreshToken;

    private int expiresIn;

    private UserDto user;

    public LoginResult() {
    }

    public LoginResult(String accessToken, String refreshToken, int expiresIn, UserDto user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.user = user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public UserDto getUser() {
        return user;
    }
}
