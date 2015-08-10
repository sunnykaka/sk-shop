package usercenter.constants;

import common.models.utils.ViewEnum;

/**
 * 市场渠道
 */
public enum MarketChannel implements ViewEnum {

    WEB,

    iOS,

    GOOGLE_PLAY,

    WAP;


    @Override
    public String getName() {
        return this.toString();
    }

    @Override
    public String getValue() {
        return this.toString();
    }




}
