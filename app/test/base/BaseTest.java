package base;

import common.exceptions.ErrorCode;
import common.utils.JsonUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import play.Application;
import play.Logger;
import play.api.ApplicationLoader;
import play.api.ApplicationLoader$;
import play.api.Environment$;
import play.api.Mode$;
import play.core.DefaultWebCommands;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import scala.Option;
import utils.Global;

import java.io.File;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static play.test.Helpers.contentAsString;

/**
 * Created by liubin on 15/4/6.
 */
public abstract class BaseTest {

    protected static Application app;

    protected static Application provideApplication() {
        ApplicationLoader.Context context = ApplicationLoader$.MODULE$.createContext(
                Environment$.MODULE$.simple(new File("."), Mode$.MODULE$.Test()),
                new scala.collection.immutable.HashMap<>(),
                Option.empty(),
                new DefaultWebCommands());

        ApplicationLoader loader = ApplicationLoader$.MODULE$.apply(context);

        return loader.load(context).injector().instanceOf(Application.class);

    }

    @BeforeClass
    public static void startPlay() {
        app = provideApplication();
        Helpers.start(app);
    }

    @AfterClass
    public static void stopPlay() {
        if (app != null) {
            Helpers.stop(app);
            app = null;
        }
    }

    public static Result routeWithExceptionHandle(Http.RequestBuilder requestBuilder) {

        Result result;
        Http.RequestImpl req = requestBuilder.build();

        try {
            result = Helpers.route(requestBuilder);
        } catch (Exception e) {
            Global global = new Global();
            return global.onError(req, e).get(3000000L);
        }

        String s = Helpers.contentAsString(result);
        Logger.debug(String.format("request: %s, response: %s", req.toString(), s));
        return result;
    }

    protected void assertResultAsError(Result result, ErrorCode expectedErrorCode) {
        assertThat(result.status(), is(expectedErrorCode.status));
        api.response.Error error = JsonUtils.json2Object(contentAsString(result), api.response.Error.class);
        assertThat(error.getCode(), is(expectedErrorCode.getName()));
    }




}
