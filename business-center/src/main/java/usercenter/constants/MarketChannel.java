package usercenter.constants;

import common.models.utils.ViewEnum;
import org.apache.commons.lang3.StringUtils;

/**
 * 市场渠道
 */
public enum MarketChannel implements ViewEnum {

    WEB,

    iOS,

    GOOGLE_PLAY,

    WAP,

    UNKNOWN;


    @Override
    public String getName() {
        return this.toString();
    }

    @Override
    public String getValue() {
        return this.toString();
    }

    public static MarketChannel fromLegacyClient(String client) {
        if(StringUtils.isBlank(client)) {
            return null;
        }
        switch (client) {
            case "Browser":
                return WEB;
            case "IOSApp":
                return iOS;
            case "AndroidApp":
                return GOOGLE_PLAY;
            default:
                return UNKNOWN;
        }
    }




}
