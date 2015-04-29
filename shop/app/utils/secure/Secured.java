package utils.secure;

import common.exceptions.AppBusinessException;
import common.exceptions.AppException;
import play.libs.F;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import usercenter.models.User;
import usercenter.services.UserService;
import usercenter.utils.SessionUtils;
import utils.Global;

/**
 * Protects an action with User
 *
 * Sample usage:
 *
 *  @SecuredAction
 *  public static Result index() {
 *      User user = SessionUtils.currentUser();
 *      return ok("Hello " + user.userName);
 *  }
 */
public class Secured extends Action<SecuredAction> {

    private SecuredActionResponses responses;

    @Override
    public F.Promise<Result> call(final Http.Context ctx) throws Throwable {

        responses = configuration.responses().newInstance();

        User user = SessionUtils.currentUser();

        if(user == null) {

            Integer userId = SessionUtils.getUserFromRememberMe();
            if(userId != null) {

                UserService userService = Global.ctx.getBean(UserService.class);
                try {
                    user = userService.loginByCookie(userId);
                } catch (AppBusinessException e) {
                    //忽略业务异常
                }

            }
        }

        if(user == null) {
            return responses.notAuthenticatedResult(ctx);
        } else {
            return delegate.call(ctx);
        }


    }
}