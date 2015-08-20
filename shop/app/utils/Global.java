package utils;

import common.exceptions.AppBusinessException;
import common.exceptions.AppException;
import common.utils.JsonResult;
import common.utils.play.BaseGlobal;
import common.utils.play.interceptor.OpenEntityManagerInViewActionFilter;
import common.utils.scheduler.StateCoordinator;
import configs.AppConfig;
import configs.DataConfig;
import play.Application;
import play.Logger;
import play.libs.Akka;
import play.libs.F;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import scala.concurrent.duration.Duration;
import scheduler.MessageJobExecuteTask;
import scheduler.PayOrderTipTask;
import scheduler.SysCancelOrderTask;
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

import static java.util.Optional.of;


public class Global extends BaseGlobal {

    StateCoordinator stateCoordinator = StateCoordinator.create("shop");

    @Override
    public void onStart(Application app) {
        super.start(app, AppConfig.class, DataConfig.class);
    }

    @Override
    public void onStop(Application app) {
        super.stop(app);
    }

    @Override
    protected void runSchedulers() {

//        stateCoordinator.addScheduler(
//                of(Duration.create(20, TimeUnit.SECONDS)),
//                of(Duration.create(60, TimeUnit.SECONDS)),
//                ExhibitionStartReminderTask.getInstance());

        stateCoordinator.addScheduler(
                of(Duration.create(1, TimeUnit.MINUTES)),
                of(Duration.create(5, TimeUnit.MINUTES)),
                SysCancelOrderTask.getInstance());

        stateCoordinator.addScheduler(
                of(Duration.create(20, TimeUnit.SECONDS)),
                of(Duration.create(10, TimeUnit.SECONDS)),
                PayOrderTipTask.getInstance());

        stateCoordinator.addScheduler(
                of(Duration.create(20, TimeUnit.SECONDS)),
                of(Duration.create(10, TimeUnit.SECONDS)),
                MessageJobExecuteTask.getInstance());

        stateCoordinator.start(Akka.system().scheduler(), Akka.system().dispatcher());
    }

    @Override
    protected void stopSchedulers() {
        stateCoordinator.stop();
    }


    @Override
    public Action onRequest(Http.Request request, Method actionMethod) {
        final EntityManagerFactory emf = ctx.getBean(EntityManagerFactory.class);

        return createActionWithActionFilters(new OpenEntityManagerInViewActionFilter(emf));
    }

    @Override
    public F.Promise<Result> onError(Http.RequestHeader request, Throwable t) {

        String errorMessage = "oops! 服务器开小差了, 请过会儿再来吧。";
        if(t instanceof AppException || t instanceof AppBusinessException) {
            //业务异常
            errorMessage = t.getMessage();
        } else {
            Logger.error("服务器发生错误: " + t.getMessage(), t);
        }

        Result result;
        boolean jsonResponse = isJsonResponse(request);
        if(jsonResponse) {
            //需要返回json结果
            result = Results.internalServerError(new JsonResult(false, errorMessage).toNode());
        } else {
            result = show500();
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
            result = show404();
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
            result = show400();
        }
        return showResult(result, jsonResponse);

    }

    public static Result show404() {
        String errorMessage = "您请求的页面没有找到，去其他地方逛逛吧";
        List<DesignerView> designerViews = new ArrayList<>();
        try {
            designerViews = ctx.getBean(DesignerService.class).lastCreateDesigner(4);
        } catch (Exception e) {
            Logger.error("", e);
        }
        return Results.notFound(error_404.render(errorMessage, designerViews));
    }

    public static Result show500() {
        String errorMessage = "oops! 服务器开小差了, 请过会儿再来吧。";
        List<DesignerView> designerViews = new ArrayList<>();
        try {
            designerViews = ctx.getBean(DesignerService.class).lastCreateDesigner(4);
        } catch (Exception e) {
            Logger.error("", e);
        }
        return Results.internalServerError(error_500.render(errorMessage, designerViews));
    }

    public static Result show400() {
        String errorMessage = "您的请求参数有误。";
        List<DesignerView> designerViews = new ArrayList<>();
        try {
            designerViews = ctx.getBean(DesignerService.class).lastCreateDesigner(4);
        } catch (Exception e) {
            Logger.error("", e);
        }
        return Results.badRequest(error_400.render(errorMessage, designerViews));
    }


}