package cmscenter.dtos;


import cmscenter.models.AppHome;

/**
 * Created by Zhb on 2015/9/15.
 */
public class AppHomeDto {

    private String size;

    private String picUrl;

    public static AppHomeDto build(AppHome appHome){
        AppHomeDto appHomeDto = new AppHomeDto();

        appHomeDto.setSize(appHome.getSize());
        appHomeDto.setPicUrl(appHome.getPicUrl());

        return appHomeDto;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }
}
