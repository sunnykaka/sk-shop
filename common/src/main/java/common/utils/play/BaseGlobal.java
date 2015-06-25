package common.utils.play;

import org.springframework.context.ApplicationContext;
import play.GlobalSettings;
import play.Play;

/**
 * Created by liubin on 15-4-21.
 */
public class BaseGlobal extends GlobalSettings {

    public static ApplicationContext ctx;

    public static boolean isDev() {
        return "dev".equalsIgnoreCase(Play.application().configuration().getString("shop.env"));
    }

    public static boolean isProd() {
        return "prod".equalsIgnoreCase(Play.application().configuration().getString("shop.env"));
    }

    public static boolean isTest() {
        return "test".equalsIgnoreCase(Play.application().configuration().getString("shop.env"));
    }

}
