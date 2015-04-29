package utils.secure;

import play.libs.F;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import usercenter.models.User;
import usercenter.utils.SessionUtils;

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
            return responses.notAuthenticatedResult(ctx);
        } else {

            return delegate.call(ctx);
        }

    }
}