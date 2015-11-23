package utils;

import common.exceptions.AppBusinessException;
import common.exceptions.AppException;
import common.utils.JsonResult;
import common.utils.play.BaseGlobal;
import common.utils.play.interceptor.OpenEntityManagerInViewActionFilter;
import configs.AppConfig;
import configs.DataConfig;
import play.Application;
import play.Logger;
import play.libs.F;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;

import javax.persistence.EntityManagerFactory;
import java.lang.reflect.Method;


public class Global extends BaseGlobal {


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
    }

    @Override
    protected void stopSchedulers() {
    }


    @Override
    public Action onRequest(Http.Request request, Method actionMethod) {
        final EntityManagerFactory emf = ctx.getBean(EntityManagerFactory.class);

        return createActionWithActionFilters(new OpenEntityManagerInViewActionFilter(emf));
    }

    @Override
    public F.Promise<Result> onError(Http.RequestHeader request, Throwable t) {

        String errorMessage = t.getMessage();
        if(t instanceof AppException || t instanceof AppBusinessException) {
            //业务异常
        } else {
            Logger.error("服务器发生错误: " + t.getMessage(), t);
        }

        boolean jsonResponse = isJsonResponse(request);
        if(jsonResponse) {
            //需要返回json结果
            Result result = Results.internalServerError(new JsonResult(false, errorMessage).toNode());
            return F.Promise.pure(result);
        } else {
            return null;
        }
    }

    @Override
    public F.Promise<Result> onHandlerNotFound(Http.RequestHeader request) {

        boolean jsonResponse = isJsonResponse(request);
        if(jsonResponse) {
            //需要返回json结果
            Result result = Results.notFound(new JsonResult(false, "找不到请求的Controller方法").toNode());
            return F.Promise.pure(result);
        } else {
            return null;
        }

    }

    @Override
    public F.Promise<Result> onBadRequest(Http.RequestHeader request, String error) {
        boolean jsonResponse = isJsonResponse(request);
        if(jsonResponse) {
            //需要返回json结果
            Result result = Results.badRequest(new JsonResult(false, "请求的参数有误").toNode());
            return F.Promise.pure(result);
        } else {
            return null;
        }
    }

}