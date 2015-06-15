package controllers.help;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.help.*;

/**
 * 帮助中心、静态页面
 */
@org.springframework.stereotype.Controller
public class HelpCenterController extends Controller {

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
        }
        return ok();

    }

}