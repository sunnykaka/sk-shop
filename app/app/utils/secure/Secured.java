package utils.secure;

import api.response.Error;
import common.exceptions.ErrorCode;
import common.utils.JsonUtils;
import common.utils.ParamUtils;
import play.libs.F;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import services.api.user.UserTokenProvider;
import usercenter.utils.SessionUtils;
import utils.Global;

import java.util.Optional;

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

    @Override
    public F.Promise<Result> call(final Http.Context ctx) throws Throwable {

        String accessToken = ParamUtils.getByKey(ctx.request(), UserTokenProvider.ACCESS_TOKEN_KEY);
        Optional<Integer> userId = Global.ctx.getBean(UserTokenProvider.class).retrieveUserIdByAccessToken(accessToken);
        if(userId.isPresent()) {
            Http.Context.current().args.put(SessionUtils.USER_ID_KEY, userId.get());
            Http.Context.current().args.remove(SessionUtils.USER_KEY);
            return delegate.call(ctx);
        } else {
            api.response.Error error = new Error(ErrorCode.InvalidAccessToken, Optional.empty(), ctx.request().uri());
            return F.Promise.pure(unauthorized(JsonUtils.object2Node(error)));
        }
    }
}