package cmscenter.dtos;


import cmscenter.models.AppThemeContent;

/**
 * Created by Zhb on 2015/9/15.
 */
public class AppThemeContentDto {

    private String content;

    public static AppThemeContentDto build(AppThemeContent appThemeContent){
        AppThemeContentDto appThemeContentDto = new AppThemeContentDto();
        appThemeContentDto.setContent(appThemeContent.getContent());

        return appThemeContentDto;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
