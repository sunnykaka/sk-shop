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
import configs.AppConfig;
import configs.DataConfig;
import org.joda.time.DateTime;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import play.Application;
import play.Logger;
import play.data.format.Formatters;
import play.libs.Akka;
import play.libs.F;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;
import scheduler.ExhibitionStartReminderTask;
import usercenter.dtos.DesignerView;
import usercenter.services.DesignerService;
import views.html.error_400;
import views.html.error_404;
import views.html.error_500;

import javax.persistence.EntityManagerFactory;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

        runSchedulers();
    }

    private void runSchedulers() {

        Akka.system().scheduler().schedule(
                Duration.create(20, TimeUnit.SECONDS),
                Duration.create(60, TimeUnit.SECONDS),
                ExhibitionStartReminderTask.getInstance(),
                Akka.system().dispatcher()
        );

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

        String errorMessage = "oops! 服务器开小差了, 请过会儿再来吧。";
        if(t instanceof AppException || t instanceof AppBusinessException) {
            //业务异常
            errorMessage = t.getMessage();
        } else {
            Logger.error("服务器发生错误", t);
        }

        Result result;
        boolean jsonResponse = isJsonResponse(request);
        if(jsonResponse) {
            //需要返回json结果
            result = Results.internalServerError(new JsonResult(false, errorMessage).toNode());
        } else {
            List<DesignerView> designerViews = new ArrayList<>();
            try {
                designerViews = ctx.getBean(DesignerService.class).lastCreateDesigner(4);
            } catch (Exception e) {
                Logger.error("", e);
            }
            result = Results.internalServerError(error_500.render(errorMessage, designerViews));

        }
        return showResult(result, jsonResponse);
    }

    @Override
    public F.Promise<Result> onHandlerNotFound(Http.RequestHeader request) {
        String errorMessage = "您请求的页面没有找到，去其他地方逛逛吧";
        Result result;
        boolean jsonResponse = isJsonResponse(request);
        if(jsonResponse) {
            //需要返回json结果
            result = Results.notFound(new JsonResult(false, errorMessage).toNode());
        } else {
            List<DesignerView> designerViews = new ArrayList<>();
            try {
                designerViews = ctx.getBean(DesignerService.class).lastCreateDesigner(4);
            } catch (Exception e) {
                Logger.error("", e);
            }
            result = Results.notFound(error_404.render(errorMessage, designerViews));
        }
        return showResult(result, jsonResponse);
    }

    @Override
    public F.Promise<Result> onBadRequest(Http.RequestHeader request, String error) {
        String errorMessage = "您请求的参数有误";
        Result result;
        boolean jsonResponse = isJsonResponse(request);
        if(jsonResponse) {
            //需要返回json结果
            result = Results.badRequest(new JsonResult(false, errorMessage).toNode());
        } else {
            List<DesignerView> designerViews = new ArrayList<>();
            try {
                designerViews = ctx.getBean(DesignerService.class).lastCreateDesigner(4);
            } catch (Exception e) {
                Logger.error("", e);
            }
            result = Results.badRequest(error_400.render(errorMessage, designerViews));
        }
        return showResult(result, jsonResponse);

    }

    private boolean isJsonResponse(Http.RequestHeader request) {
        return request.acceptedTypes().stream().filter(mr -> mr.toString().contains("json")).findFirst().isPresent();
    }

    private F.Promise<Result> showResult(Result result, boolean jsonResponse) {
        if(!jsonResponse && isDev()) {
            //开发环境显示play的错误页面
            return null;
        } else {
            return F.Promise.pure(result);
        }
    }
}