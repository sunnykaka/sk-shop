package cmscenter.dtos;

import cmscenter.models.AppTheme;
import cmscenter.services.AppThemeService;
import cmscenter.services.ThemeCollectService;
import org.joda.time.DateTime;
import usercenter.models.User;

/**
 * Created by Zhb on 2015/9/15.
 */
public class AppThemeBreviaryDto {

    private Integer id;

    private String startTime;

    private int themeNo;

    private int num;

    private String picUrl;

    private String name;

    private String digest;

    private boolean isFavorites = false;

    public static AppThemeBreviaryDto build(AppTheme appTheme,ThemeCollectService themeCollectService,
                                    User user,String deviceId){

        AppThemeBreviaryDto appThemeDto = new AppThemeBreviaryDto();
        appThemeDto.setId(appTheme.getId());
        appThemeDto.setName(appTheme.getName());
        appThemeDto.setStartTime(appTheme.getStartTime().toString("yyyy-MM-dd"));
        appThemeDto.setPicUrl(appTheme.getPicUrl());
        appThemeDto.setThemeNo(appTheme.getThemeNo());
        appThemeDto.setIsFavorites(themeCollectService.isFavorites(appTheme.getId(),user,deviceId));
        appThemeDto.setNum(appTheme.getBaseNum() + themeCollectService.countThemeCollect(appTheme.getId()));

        appThemeDto.setDigest(appTheme.getDigest());

        return appThemeDto;

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getThemeNo() {
        return themeNo;
    }

    public void setThemeNo(int themeNo) {
        this.themeNo = themeNo;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public boolean isFavorites() {
        return isFavorites;
    }

    public void setIsFavorites(boolean isFavorites) {
        this.isFavorites = isFavorites;
    }
}
