package common.utils.play.interceptor;

import play.libs.F;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

/**
 * Created by liubin on 15/4/12.
 */
public class ActionFilterChain extends Action.Simple {

    private final ActionInterceptor[] interceptors;

    public ActionFilterChain(ActionInterceptor... interceptors) {
        this.interceptors = interceptors;
    }

    @Override
    public F.Promise<Result> call(Http.Context ctx) throws Throwable {

        if(interceptors != null) {
            for(ActionInterceptor interceptor : interceptors) {
                interceptor.preHandle();
            }
        }

        try {
            return delegate.call(ctx);

        } finally {
            if(interceptors != null) {
                for(int i=interceptors.length-1; i>=0; i--) {
                    ActionInterceptor interceptor = interceptors[i];
                    interceptor.postHandle();
                }
            }
        }
    }
}
