package utils;

import common.exceptions.AppBusinessException;
import common.exceptions.AppException;
import common.utils.JsonResult;
import common.utils.Money;
import common.utils.play.BaseGlobal;
import common.utils.play.JodaDateFormatter;
import common.utils.play.MoneyFormatter;
import common.utils.play.interceptor.ActionFilterChain;
import common.utils.play.interceptor.OpenEntityManagerInViewActionFilter;
import controllers.user.routes;
import org.joda.time.DateTime;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import play.GlobalSettings;
import play.Application;

import configs.AppConfig;
import configs.DataConfig;
import play.Logger;
import play.data.format.Formatters;
import play.libs.F;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import usercenter.utils.SessionUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceException;
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

    @Override
    public F.Promise<Result> onError(Http.RequestHeader request, Throwable t) {

        if ( request.accepts("text/html")) {

            //TODO 返回自定义错误页面
            return super.onError(request, t);

        } else if ( request.accepts("application/json")) {

            String errorMessage;
            if(t instanceof AppException || t instanceof AppBusinessException) {
                //业务异常
                errorMessage = t.getMessage();
            } else {
                errorMessage = "oops! 服务器开小差了, 请过会儿再来吧";
                Logger.error("服务器发生错误", t);
            }

            Result result = Results.internalServerError(new JsonResult(false, errorMessage).toNode());
            return F.Promise.pure(result);

        }


        return super.onError(request, t);

    }
}