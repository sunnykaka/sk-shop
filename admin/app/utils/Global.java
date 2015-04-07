package utils;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.ApplicationContext;
import play.GlobalSettings;
import play.Application;

import configs.AppConfig;
import configs.DataConfig;
import play.Logger;

public class Global extends GlobalSettings {

    public static ApplicationContext ctx;

    @Override
    public synchronized void onStart(Application app) {
        if(ctx != null) {
            Logger.warn("Global 启动的时候发现ctx != null");
            return;
        }
        ctx = new AnnotationConfigApplicationContext(AppConfig.class, DataConfig.class);
    }

    @Override
    public <A> A getControllerInstance(Class<A> clazz) {
        return ctx.getBean(clazz);
    }

}