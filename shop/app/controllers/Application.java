package controllers;

import models.CmsContent;
import models.CmsExhibition;
import models.ExhibitionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import play.mvc.Controller;
import play.mvc.Result;
import services.CmsService;
import usercenter.dtos.DesignerView;
import usercenter.models.User;
import usercenter.services.DesignerService;
import usercenter.utils.SessionUtils;
import utils.secure.SecuredAction;
import views.html.designers;
import views.html.index;
import views.html.myOrder;
import views.html.preview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Controller
public class Application extends Controller {

    /**
     * 首页轮播图
     */
    private static final String SLIDER_BOX = "sliderBox";


    @Autowired
    private CmsService cmsService;

    @Autowired
    private DesignerService sesignerService;


    /**
     * 首页，首发专场
     * @return
     */
    public Result index() {
        Map<ExhibitionStatus, List<CmsExhibition>> exhibitions = cmsService.queryAllExhibition();
        List<CmsExhibition> sellingList = exhibitions.get(ExhibitionStatus.SELLING);
        List<CmsContent> contents = cmsService.allContents();
        List<CmsContent> sliderBoxs = new ArrayList<>();
        List<CmsContent> fontList = new ArrayList<>();
        for (CmsContent content : contents) {
            if (content.getType().equals("PIC") && content.getPosition().equals("sliderBox")) {
                sliderBoxs.add(content);
            }
            if (content.getType().equals("FONT")) {
                fontList.add(content);
            }
        }
        return ok(index.render(SessionUtils.currentUser(), sellingList, fontList, sliderBoxs));
    }


    /**
     * 预告
     * @return
     */
    public Result preview() {
        Map<ExhibitionStatus, List<CmsExhibition>> exhibitions = cmsService.queryAllExhibition();
        List<CmsExhibition> sellingList = exhibitions.get(ExhibitionStatus.PREPARE);
        List<CmsContent> contents = cmsService.allContents();
        List<CmsContent> sliderBoxs = new ArrayList<>();
        List<CmsContent> fontList = new ArrayList<>();
        for (CmsContent content : contents) {
            if (content.getType().equals("PIC") && content.getPosition().equals("sliderBox")) {
                sliderBoxs.add(content);
            }
            if (content.getType().equals("FONT")) {
                fontList.add(content);
            }
        }
        return ok(preview.render(SessionUtils.currentUser(), sellingList, fontList, sliderBoxs));
    }



    public Result designers(){
        List<DesignerView>  list = sesignerService.getAllDesigner();
        return ok(designers.render(list));
    }


    @SecuredAction
    public Result myOrder() {

        User user = SessionUtils.currentUser();
        System.out.println("username" + user.getUserName());

        return ok(myOrder.render(user));

    }


}