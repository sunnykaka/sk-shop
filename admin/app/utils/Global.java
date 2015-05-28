package utils;

import common.utils.Money;
import common.utils.play.BaseGlobal;
import common.utils.play.JodaDateFormatter;
import common.utils.play.MoneyFormatter;
import common.utils.play.interceptor.ActionFilterChain;
import common.utils.play.interceptor.OpenEntityManagerInViewActionFilter;
import configs.AppConfig;
import configs.DataConfig;
import org.joda.time.DateTime;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import play.Application;
import play.Logger;
import play.data.format.Formatters;
import play.libs.F;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

import javax.persistence.EntityManagerFactory;
import java.lang.reflect.Method;

public class Global extends BaseGlobal {

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

    @Override
    public Action onRequest(Http.Request request, Method actionMethod) {

        final EntityManagerFactory emf = ctx.getBean(EntityManagerFactory.class);

        return new Action.Simple() {
            public F.Promise<Result> call(Http.Context ctx) throws Throwable {

                ActionFilterChain filterChain = new ActionFilterChain(
                        ctx,
                        delegate,
                        new OpenEntityManagerInViewActionFilter(emf)
                );
                filterChain.doFilter();
                return filterChain.result;

            }
        };
    }
}