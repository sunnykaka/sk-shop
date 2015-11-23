package usercenter.constants;

import common.models.utils.ViewEnum;

/**
 * 设计师图片类型
 * Created by lidujun on 15-5-22
 */
public enum DesignerPictureType implements ViewEnum {

    IndexLogoSmallPic("Logo小图","120*60"),
    ListLogoBigPic("Logo大图","289*60"),
    ListMainPic("主图","289*460"),
    StorePic("店铺首图","439*370"),
    StoreLogoPic("店铺页LOGO图", "1903"),
    WhiteBGPic("白底logo", "620*150");

    public String value;

    public String size;

    DesignerPictureType(String value, String size) {
        this.value = value;
        this.size = size;
    }

    @Override
    public String getName() {
        return this.toString();
    }

    @Override
    public String getValue() {
        return value;
    }

    public String getSize() {
        return size;
    }
}