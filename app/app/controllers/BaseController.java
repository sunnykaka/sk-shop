package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import common.exceptions.AppBusinessException;
import common.exceptions.ErrorCode;
import common.utils.FormUtils;
import common.utils.play.BaseGlobal;
import play.api.mvc.Codec;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http;
import play.twirl.api.Content;
import usercenter.models.User;
import usercenter.services.UserService;
import usercenter.utils.SessionUtils;
import utils.Global;

/**
 * Created by liubin on 15-8-4.
 */
public abstract class BaseController extends Controller {

    static Codec utf8 = Codec.javaSupported("utf-8");

    protected <T> Form<T> bindAndCheckForm(Class<T> clazz) {
        Form<T> form = Form.form(clazz).bindFromRequest(request());

        if(form.hasErrors()) {
            throw new AppBusinessException(ErrorCode.InvalidArgument, FormUtils.showErrorInfo(form.errors()));
        }

        return form;
    }

    /**
     * Generates a 409 CONFLICT simple result.
     */
    public static Status conflict() {
        return new Status(play.core.j.JavaResults.Conflict());
    }

    /**
     * Generates a 409 CONFLICT simple result.
     */
    public static Status conflict(JsonNode content) {
        return new Status(play.core.j.JavaResults.Conflict(), content, utf8);
    }

    protected User currentUser() {
        if(Http.Context.current().args.get(SessionUtils.USER_KEY) != null) {
            return (User)Http.Context.current().args.get(SessionUtils.USER_KEY);
        }

        Integer userId = (Integer)Http.Context.current().args.get(SessionUtils.USER_ID_KEY);
        if(userId != null) {
            User user = Global.ctx.getBean(UserService.class).getById(userId);
            Http.Context.current().args.put(SessionUtils.USER_KEY, user);
            return user;
        }

        return null;
    }
}