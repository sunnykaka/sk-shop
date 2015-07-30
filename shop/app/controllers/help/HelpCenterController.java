package controllers.help;

import models.Link;
import org.springframework.beans.factory.annotation.Autowired;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;
import services.LinkService;
import usercenter.dtos.DesignerView;
import usercenter.services.DesignerService;
import views.html.error_404;
import views.html.help.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 帮助中心、静态页面
 */
@org.springframework.stereotype.Controller
public class HelpCenterController extends Controller {


    @Autowired
    private DesignerService designerService;

    @Autowired
    private LinkService linkService;

    public Result index(String name) {

        if ("service".equals(name)) {
            return ok(service.render());
        } else if ("contact".equals(name)) {
            return ok(contact.render());
        } else if ("deliverInfo".equals(name)) {
            return ok(deliverInfo.render());
        } else if ("memberClause".equals(name)) {
            return ok(memberClause.render());
        } else if ("payType".equals(name)) {
            return ok(payType.render());
        } else if ("rightsInfo".equals(name)) {
            return ok(rightsInfo.render());
        } else if ("shopping".equals(name)) {
            return ok(shopping.render());
        } else if ("sizeInfo".equals(name)) {
            return ok(sizeInfo.render());
        } else if ("joinUs".equals(name)) {
            return ok(joinUs.render());
        } else if("friendLink".equals(name)) { //友情链接
            List<Link> linkList = linkService.getLinkList();
            return ok(friendLink.render(linkList));
        }

        List<DesignerView> designerViews = new ArrayList<>();
        try {
            designerViews = designerService.lastCreateDesigner(4);
        } catch (Exception e) {
            Logger.error("", e);
        }
        return Results.notFound(error_404.render("您请求的页面没有找到，去其他地方逛逛吧", designerViews));

    }

}