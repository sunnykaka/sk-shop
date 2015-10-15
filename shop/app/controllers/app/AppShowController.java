package controllers.app;

import cmscenter.dtos.AppThemeDto;
import cmscenter.models.AppTheme;
import cmscenter.services.AppThemeService;
import cmscenter.services.ThemeCollectService;
import common.exceptions.AppBusinessException;
import common.exceptions.ErrorCode;
import common.utils.ParamUtils;
import org.springframework.beans.factory.annotation.Autowired;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;
import productcenter.services.ProductPictureService;
import productcenter.services.ProductService;
import usercenter.dtos.DesignerView;
import usercenter.models.User;
import usercenter.services.DesignerService;
import usercenter.utils.SessionUtils;
import views.html.app.*;
import views.html.error_404;

import java.util.ArrayList;
import java.util.List;

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

    @Autowired
    private DesignerService designerService;

    public Result appTheme(int themeNo) {

        AppTheme appTheme = appThemeService.getAppThemeByThemeNo(themeNo);

        if(appTheme == null){
            List<DesignerView> designerViews = new ArrayList<>();
            try {
                designerViews = designerService.lastCreateDesigner(4);
            } catch (Exception e) {
                Logger.error("", e);
            }
            return Results.notFound(error_404.render("专题不存在", designerViews));
        }
        AppThemeDto appThemeDto = AppThemeDto.build(appTheme,appThemeService);
        return ok(appShowTheme.render(appThemeDto));
    }

    public Result appThemeDesc(int themeNo) {

        AppTheme appTheme = appThemeService.getAppThemeByThemeNo(themeNo);

        if(appTheme == null){
            List<DesignerView> designerViews = new ArrayList<>();
            try {
                designerViews = designerService.lastCreateDesigner(4);
            } catch (Exception e) {
                Logger.error("", e);
            }
            return Results.notFound(error_404.render("专题不存在", designerViews));
        }

        AppThemeDto appThemeDto = AppThemeDto.build(appTheme,appThemeService,themeCollectService,productService,productPictureService,null,null);

        return ok(appThemeDesc.render(appThemeDto));
    }

    public Result download(int themeNo) {

        AppTheme appTheme = appThemeService.getAppThemeByThemeNo(themeNo);

        if(appTheme == null){
            List<DesignerView> designerViews = new ArrayList<>();
            try {
                designerViews = designerService.lastCreateDesigner(4);
            } catch (Exception e) {
                Logger.error("", e);
            }
            return Results.notFound(error_404.render("专题不存在", designerViews));
        }

        AppThemeDto appThemeDto = AppThemeDto.build(appTheme,appThemeService,themeCollectService,productService,productPictureService,null,null);


        return ok(appShowDownload.render(appThemeDto));
    }

}