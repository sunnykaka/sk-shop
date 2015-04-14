package common.utils.play.interceptor;

import play.libs.F;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by liubin on 15/4/12.
 */
public class ActionFilterChain extends Action.Simple {

    public final ActionFilter[] filters;

    public int filterIndex = 0;

    public F.Promise<Result> result;

    public Http.Context ctx;

    public ActionFilterChain(Http.Context ctx, ActionFilter... filters) {
        if(filters == null) {
            filters = new ActionFilter[0];
        }

        this.filters = filters;
        this.ctx = ctx;
    }

    @Override
    public F.Promise<Result> call(Http.Context ctx) throws Throwable {

        filters[0].doFilter(this);

        return result;

    }

    public void doFilter() throws Throwable {
        if(filters.length <= filterIndex) {
            result = delegate.call(this.ctx);
        } else {
            filters[filterIndex++].doFilter(this);
        }
    }
}
