package controllers.app;

import cmscenter.dtos.AppThemeDto;
import cmscenter.models.AppTheme;
import cmscenter.services.AppThemeService;
import org.springframework.beans.factory.annotation.Autowired;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.app.*;

/**
 * app 分享页面
 */
@org.springframework.stereotype.Controller
public class AppShowController extends Controller {

    @Autowired
    private AppThemeService appThemeService;

    public Result appTheme(int themeId) {

        AppTheme appTheme = appThemeService.getAppThemeById(themeId);
        AppThemeDto appThemeDto = AppThemeDto.build(appTheme,appThemeService);

        return ok(appShowTheme.render(appThemeDto));
    }

    public Result download(int themeId) {

        AppTheme appTheme = appThemeService.getAppThemeById(themeId);
        AppThemeDto appThemeDto = AppThemeDto.build(appTheme,appThemeService);

        return ok(appShowDownload.render(appThemeDto));
    }

}