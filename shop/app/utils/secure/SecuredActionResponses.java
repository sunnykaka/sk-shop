package utils.secure;

import play.libs.F;
import play.mvc.Http;
import play.mvc.Result;

/**
 * An interface that defines the responses that will be sent wheen the invoker is not authenticated or
 * authorized to execute a protected action.
 *
 */
public interface SecuredActionResponses {

    F.Promise<Result> notAuthenticatedResult(Http.Context ctx);

}
