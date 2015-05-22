package usercenter.constants;

import common.models.utils.ViewEnum;

/**
 * 设计师图片类型
 * Created by lidujun on 15-5-22
 */
public enum DesignerPictureType implements ViewEnum {

    IndexLogoSmallPic("首页设计师品牌Logo图","120*60"),
    ListLogoBigPic("列表页设计师品牌logo图","289*60"),
    ListMainPic("列表页设计师主图","289*460"),
    StorePic("店铺页设计师首图","439*370");

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