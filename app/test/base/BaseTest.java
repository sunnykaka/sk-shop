package base;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import play.Application;
import play.api.ApplicationLoader;
import play.api.ApplicationLoader$;
import play.api.Environment$;
import play.api.Mode$;
import play.core.DefaultWebCommands;
import play.test.Helpers;
import scala.Option;

import java.io.File;

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



}
