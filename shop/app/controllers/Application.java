package controllers;

import models.CmsContent;
import models.CmsExhibition;
import models.ExhibitionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import play.mvc.Controller;
import play.mvc.Result;
import services.CmsService;
import usercenter.models.User;
import usercenter.utils.SessionUtils;
import utils.secure.SecuredAction;
import views.html.index;
import views.html.myOrder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Controller
public class Application extends Controller {

    /**
     * 首页轮播图
     */
    private static final String  SLIDER_BOX = "sliderBox";


    @Autowired
    private CmsService cmsService;

    public Result index(){
        Map<ExhibitionStatus, List<CmsExhibition>> exhibitions = cmsService.queryAllExhibition();
        List<CmsExhibition> sellingList = exhibitions.get(ExhibitionStatus.SELLING);
        List<CmsContent> contents = cmsService.allContents();
        List<CmsContent> sliderBoxs = new ArrayList<>();
        List<CmsContent> fontList = new ArrayList<>();
        for(CmsContent content : contents){
            if(content.getType().equals("PIC") && content.getPosition().equals("sliderBox")){
                sliderBoxs.add(content);
            }
            if(content.getType().equals("FONT")){
                fontList.add(content);
            }
        }

        return ok(index.render(SessionUtils.currentUser(),sellingList,fontList,sliderBoxs));
    }


    @SecuredAction
    public Result myOrder() {

        User user = SessionUtils.currentUser();
        System.out.println("username" + user.getUserName());

        return ok(myOrder.render(user));

    }


}