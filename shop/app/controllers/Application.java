package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import usercenter.models.User;
import usercenter.utils.SessionUtils;
import utils.secure.SecuredAction;
import views.html.index;
import views.html.myOrder;

@org.springframework.stereotype.Controller
public class Application extends Controller {

    public Result index() {

        User user = SessionUtils.currentUser();

        return ok(index.render(user));

    }

    @SecuredAction
    public Result myOrder() {

        User user = SessionUtils.currentUser();
        System.out.println("username" + user.getUserName());

        return ok(myOrder.render(user));

    }


}