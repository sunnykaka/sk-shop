package cmscenter.dtos;


import cmscenter.models.AppTheme;

/**
 * Created by Zhb on 2015/9/15.
 */
public class AppThemeSimpleDto {

    private Integer id;

    private String name;

    private int themeNo;

    private String picUrl;

    public static AppThemeSimpleDto build(AppTheme appTheme){
        AppThemeSimpleDto appThemeSimpleDto = new AppThemeSimpleDto();

        appThemeSimpleDto.setId(appTheme.getId());
        appThemeSimpleDto.setName(appTheme.getName());
        appThemeSimpleDto.setPicUrl(appTheme.getPicUrl());
        appThemeSimpleDto.setThemeNo(appTheme.getThemeNo());

        return appThemeSimpleDto;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public int getThemeNo() {
        return themeNo;
    }

    public void setThemeNo(int themeNo) {
        this.themeNo = themeNo;
    }
}
