package controllers.api.shop;

import cmscenter.dtos.AppHomeDto;
import cmscenter.dtos.AppThemeBreviaryDto;
import cmscenter.dtos.AppThemeDto;
import cmscenter.dtos.AppThemeSimpleDto;
import cmscenter.models.AppHome;
import cmscenter.models.AppTheme;
import cmscenter.models.DeviceToken;
import cmscenter.services.AppThemeService;
import cmscenter.services.ThemeCollectService;
import common.exceptions.AppBusinessException;
import common.exceptions.ErrorCode;
import common.utils.JsonUtils;
import common.utils.ParamUtils;
import common.utils.page.Page;
import controllers.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import play.mvc.Result;
import productcenter.services.ProductPictureService;
import productcenter.services.ProductService;
import usercenter.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * CMS专题内容
 */
@org.springframework.stereotype.Controller
public class ThemeApiController extends BaseController {

    @Autowired
    private AppThemeService appThemeService;

    @Autowired
    private ThemeCollectService themeCollectService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductPictureService productPictureService;

    public static final int APP_THEME_NUM = 7;

    /**
     * app首页图片
     *
     * @return
     */
    public Result getAppHomeList(){

        List<AppHome> appHomeList = appThemeService.getAppHomes();
        List<AppHomeDto> appHomeDtos = new ArrayList<>();
        for(cmscenter.models.AppHome appHome:appHomeList){
            appHomeDtos.add(AppHomeDto.build(appHome));
        }

        return ok(JsonUtils.object2Node(appHomeDtos));

    }

    public Result getAppTheme(int themeId){
        User user = this.currentUser();
        String deviceId = ParamUtils.getByKey(request(), "deviceId");

        AppTheme appTheme = appThemeService.getAppThemeById(themeId);

        if(appTheme == null){
            throw new AppBusinessException(ErrorCode.Conflict, "没有该专题信息");
        }

        AppThemeDto appThemeDto = AppThemeDto.build(appTheme,appThemeService,themeCollectService,productService,productPictureService,user,deviceId);

        return ok(JsonUtils.object2Node(appThemeDto));
    }

    public Result getAppThemeHome(){

        User user = this.currentUser();
        String deviceId = ParamUtils.getByKey(request(), "deviceId");

        Page<AppTheme> page = new Page<>(1,APP_THEME_NUM);

        List<AppTheme> appThemeList = appThemeService.findAppThemePageList(Optional.of(page));
        List<AppThemeBreviaryDto> appThemeDtos = new ArrayList<>();
        for(AppTheme appTheme:appThemeList){
            appThemeDtos.add(AppThemeBreviaryDto.build(appTheme,themeCollectService,appThemeService,user,deviceId));
        }

        return ok(JsonUtils.object2Node(appThemeDtos));

    }

    public Result getAppThemeList(int pageNo,int pageSize){

        Page<AppTheme> page = new Page<>(pageNo,pageSize);
        Page<AppThemeSimpleDto> pageDto = new Page<>(pageNo,pageSize);

        List<AppTheme> appThemeList = appThemeService.findAppThemePageList(Optional.of(page));
        List<AppThemeSimpleDto> appThemeSimpleDtos = new ArrayList<>();
        for(AppTheme appTheme:appThemeList){
            appThemeSimpleDtos.add(AppThemeSimpleDto.build(appTheme));
        }
        pageDto.setResult(appThemeSimpleDtos);

        return ok(JsonUtils.object2Node(pageDto));

    }

    public Result saveDeviceToken(){

        String token = ParamUtils.getByKey(request(),"token");
        if(null != token){
            token = token.replace(" ","");
            if(null != token && token.length() == 64){

                DeviceToken deviceToken = appThemeService.findBytoken(token);
                if(null == deviceToken){
                    appThemeService.saveDeviceToken(new DeviceToken(token,DeviceToken.BADGE_DEFAULT_INT));
                }

            }
        }

        return noContent();

    }

}