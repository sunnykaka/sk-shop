/**
 * Copyright 2014 Jorge Aliss (jaliss at gmail dot com) - twitter: @jaliss
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package utils.secure;

import common.utils.JsonResult;
import controllers.user.routes;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import usercenter.utils.SessionUtils;

/**
 * The default responses sent when the invoker is not authenticated or authorized to execute a protected
 * action.
 *
 */
public class DefaultSecuredActionResponses extends Controller implements SecuredActionResponses {

    @Override
    public F.Promise<Result> notAuthenticatedResult(Http.Context ctx) {
        Http.Request req = ctx.request();
        Result result;

        SessionUtils.setOriginalUrl(ctx.request().uri());

        if (isAjaxRequest(req)) {

            result = ok(new JsonResult(false, "Credentials required").toNode());

        } else {

            result = redirect(routes.LoginController.loginPage());

        }
        return F.Promise.pure(result);
    }

    private boolean isAjaxRequest(Http.Request request) {
        String requestType = request.getHeader("X-Requested-With");
        if (requestType != null && requestType.equalsIgnoreCase("XMLHttpRequest")) {
            return true;
        } else {
            return false;
        }
    }

}
