package utils;

import common.utils.Money;
import common.utils.play.JodaDateFormatter;
import common.utils.play.MoneyFormatter;
import org.joda.time.DateTime;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.ApplicationContext;
import play.GlobalSettings;
import play.Application;

import configs.AppConfig;
import configs.DataConfig;
import play.Logger;
import play.data.format.Formatters;

public class Global extends GlobalSettings {

    public static ApplicationContext ctx;

    @Override
    public synchronized void onStart(Application app) {
        if(ctx != null) {
            Logger.warn("Global 启动的时候发现ctx != null");
            return;
        }
        ctx = new AnnotationConfigApplicationContext(AppConfig.class, DataConfig.class);

        Formatters.register(DateTime.class, new JodaDateFormatter());
        Formatters.register(Money.class, new MoneyFormatter());

    }

    @Override
    public <A> A getControllerInstance(Class<A> clazz) {
        return ctx.getBean(clazz);
    }

}