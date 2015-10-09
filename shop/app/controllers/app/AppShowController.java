package controllers.app;

import cmscenter.dtos.AppThemeDto;
import cmscenter.models.AppTheme;
import cmscenter.services.AppThemeService;
import cmscenter.services.ThemeCollectService;
import common.exceptions.AppBusinessException;
import common.exceptions.ErrorCode;
import common.utils.ParamUtils;
import org.springframework.beans.factory.annotation.Autowired;
import play.mvc.Controller;
import play.mvc.Result;
import productcenter.services.ProductPictureService;
import productcenter.services.ProductService;
import usercenter.models.User;
import usercenter.utils.SessionUtils;
import views.html.app.*;

/**
 * app 分享页面
 */
@org.springframework.stereotype.Controller
public class AppShowController extends Controller {

    @Autowired
    private AppThemeService appThemeService;

    @Autowired
    private ThemeCollectService themeCollectService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductPictureService productPictureService;

    public Result appTheme(int themeId) {

        AppTheme appTheme = appThemeService.getAppThemeById(themeId);
        AppThemeDto appThemeDto = AppThemeDto.build(appTheme,appThemeService);

        return ok(appShowTheme.render(appThemeDto));
    }

    public Result appThemeDesc(int themeId) {

        AppTheme appTheme = appThemeService.getAppThemeById(themeId);

        if(appTheme == null){
            throw new AppBusinessException(ErrorCode.Conflict, "没有该专题信息");
        }

        AppThemeDto appThemeDto = AppThemeDto.build(appTheme,appThemeService,themeCollectService,productService,productPictureService,null,null);

        return ok(appThemeDesc.render(appThemeDto));
    }

    public Result download(int themeId) {

        AppTheme appTheme = appThemeService.getAppThemeById(themeId);
        AppThemeDto appThemeDto = AppThemeDto.build(appTheme,appThemeService);

        return ok(appShowDownload.render(appThemeDto));
    }

}