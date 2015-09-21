package cmscenter.dtos;


import cmscenter.models.AppThemeContent;

/**
 * Created by Zhb on 2015/9/15.
 */
public class AppThemeContentDto {

    private String type;

    private String content;

    private Integer height;

    public static AppThemeContentDto build(AppThemeContent appThemeContent){
        AppThemeContentDto appThemeContentDto = new AppThemeContentDto();
        appThemeContentDto.setContent(appThemeContent.getContent());
        appThemeContentDto.setType(appThemeContent.getType());
        appThemeContentDto.setHeight(appThemeContent.getHeight());

        return appThemeContentDto;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }
}
