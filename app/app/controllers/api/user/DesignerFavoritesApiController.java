package controllers.api.user;

import api.response.user.DesignerFavoritesDto;
import common.exceptions.AppBusinessException;
import common.exceptions.ErrorCode;
import common.utils.JsonResult;
import common.utils.JsonUtils;
import common.utils.page.Page;
import controllers.BaseController;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import play.mvc.Result;
import usercenter.constants.DesignerPictureType;
import usercenter.models.Designer;
import usercenter.models.DesignerCollect;
import usercenter.models.DesignerPicture;
import usercenter.models.User;
import usercenter.services.DesignerCollectService;
import usercenter.services.DesignerService;
import usercenter.utils.SessionUtils;
import utils.secure.SecuredAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 关注设计师
 */
@org.springframework.stereotype.Controller
public class DesignerFavoritesApiController extends BaseController {

    @Autowired
    private DesignerCollectService designerCollectService;

    @Autowired
    private DesignerService designerService;

    /**
     * 收藏商品列表
     *
     * @return
     */
    @SecuredAction
    public Result index(int pageNo, int pageSize) {

        User user = SessionUtils.currentUser();

        Page<DesignerCollect> page = new Page(pageNo,pageSize);
        Page<DesignerFavoritesDto> pageDto = new Page(pageNo,pageSize);
        List<DesignerCollect> pageProductCollcet = designerCollectService.getDesignerCollectList(Optional.of(page), user.getId());
        List<DesignerFavoritesDto> designerFavoritesDtos = new ArrayList<>();
        for(DesignerCollect designerCollect:pageProductCollcet){
            Designer designer = designerService.getDesignerById(designerCollect.getDesignerId());
            DesignerPicture designerPicture = designerService.getDesignerPicByType(designerCollect.getDesignerId(), DesignerPictureType.ListMainPic);
            designerCollect.setDesignerName(designer.getName());
            designerCollect.setDesignerPic(designerPicture.getPictureUrl());
            designerFavoritesDtos.add(DesignerFavoritesDto.build(designerCollect));
        }
        pageDto.setResult(designerFavoritesDtos);

        return ok(JsonUtils.object2Node(pageDto));

    }

    /**
     * 删除我的收藏
     *
     * @param designerId
     * @return
     */
    @SecuredAction
    public Result del(int designerId) {

        User user = SessionUtils.currentUser();

        try {
            designerCollectService.deleteMyDesignerCollect(designerId, user.getId());
        }catch (AppBusinessException e){
            throw new AppBusinessException(ErrorCode.Conflict, "删除失败");
        }

        return noContent();

    }

    @SecuredAction
    public Result add(int designerId){

        User user = SessionUtils.currentUser();

        Designer designer = designerService.getDesignerById(designerId);
        if(null == designer){
            throw new AppBusinessException(ErrorCode.Conflict, "该设计师不存在");
        }

        DesignerCollect oldDesignerCollect = designerCollectService.getByDesignerId(designerId,user.getId());
        if(null != oldDesignerCollect){
            throw new AppBusinessException(ErrorCode.Conflict, "已关注该设计师");
        }

        DesignerCollect designerCollect = new DesignerCollect();
        designerCollect.setDesignerId(designerId);
        designerCollect.setUserId(user.getId());
        designerCollect.setCollectTime(DateTime.now());
        designerCollectService.createDesignerCollect(designerCollect);

        return noContent();

    }



}