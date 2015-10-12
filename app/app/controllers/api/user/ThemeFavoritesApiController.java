package controllers.api.user;

import api.response.user.FavoritesDto;
import cmscenter.models.AppTheme;
import cmscenter.models.ThemeCollect;
import cmscenter.services.AppThemeService;
import cmscenter.services.ThemeCollectService;
import common.exceptions.AppBusinessException;
import common.exceptions.ErrorCode;
import common.utils.JsonUtils;
import common.utils.ParamUtils;
import common.utils.page.Page;
import controllers.BaseController;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import play.mvc.Result;
import usercenter.models.*;
import utils.secure.SecuredAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 专题收藏
 */
@org.springframework.stereotype.Controller
public class ThemeFavoritesApiController extends BaseController {

    @Autowired
    private AppThemeService appThemeService;

    @Autowired
    private ThemeCollectService themeCollectService;

    /**
     * 收藏商品列表
     *
     * @return
     */
    @SecuredAction
    public Result list(int pageNo, int pageSize) {

        User user = this.currentUser();

        Page<ThemeCollect> page = new Page(pageNo,pageSize);
        Page<FavoritesDto> pageDto = new Page(pageNo,pageSize);
        List<ThemeCollect> pageThemeCollect = themeCollectService.getThemeCollectList(Optional.of(page), user.getId());
        List<FavoritesDto> themeFavoritesDtos = new ArrayList<>();
        for(ThemeCollect themeCollect:pageThemeCollect){
            AppTheme appTheme = appThemeService.getAppThemeByThemeNo(themeCollect.getThemeId());
            themeCollect.setThemeName(appTheme == null ? "" : appTheme.getName());
            themeCollect.setThemePic(appTheme == null ? "" : appTheme.getPicUrl());
            themeFavoritesDtos.add(FavoritesDto.build(themeCollect));
        }
        pageDto.setResult(themeFavoritesDtos);

        return ok(JsonUtils.object2Node(pageDto));

    }

    /**
     * 删除我的收藏
     *
     * @return
     */
    public Result del(int themeNo) {

        User user = this.currentUser();

        String deviceId = ParamUtils.getByKey(request(), "deviceId");

        try {
            themeCollectService.deleteThemeCollect(themeNo, user, deviceId);
        }catch (AppBusinessException e){
            throw new AppBusinessException(ErrorCode.Conflict, "删除失败");
        }

        return noContent();

    }

    public Result add(){

        User user = this.currentUser();

        int themeNo = ParamUtils.getObjectId(request());
        String deviceId = ParamUtils.getByKey(request(), "deviceId");

        AppTheme appTheme = appThemeService.getAppThemeByThemeNo(themeNo);
        if(null == appTheme){
            throw new AppBusinessException(ErrorCode.Conflict, "该专题不存在");
        }

        ThemeCollect themeCollect = themeCollectService.findMyThemeCollect(themeNo, user, deviceId);
        if(null != themeCollect){
            throw new AppBusinessException(ErrorCode.Conflict, "已收藏该专题");
        }

        ThemeCollect newThemeCollect = new ThemeCollect();
        newThemeCollect.setCollectTime(DateTime.now());
        newThemeCollect.setThemeId(themeNo);
        newThemeCollect.setDeviceId(deviceId);
        newThemeCollect.setUserId(user == null ? 0 : user.getId());

        themeCollectService.createThemeCollect(newThemeCollect);

        return noContent();

    }



}