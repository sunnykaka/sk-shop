package utils;

import api.response.Error;
import com.fasterxml.jackson.databind.JsonNode;
import common.exceptions.AppBusinessException;
import common.exceptions.AppException;
import common.exceptions.ErrorCode;
import common.utils.JsonUtils;
import common.utils.play.BaseGlobal;
import common.utils.play.interceptor.OpenEntityManagerInViewActionFilter;
import common.utils.scheduler.StateCoordinator;
import configs.AppConfig;
import configs.DataConfig;
import controllers.BaseController;
import play.Application;
import play.Logger;
import play.libs.F;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;

import javax.persistence.EntityManagerFactory;
import java.lang.reflect.Method;
import java.util.Optional;


public class Global extends BaseGlobal {

    StateCoordinator stateCoordinator = StateCoordinator.create("app");

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


//        stateCoordinator.start(Akka.system().scheduler(), Akka.system().dispatcher());
    }

    @Override
    protected void stopSchedulers() {
//        stateCoordinator.stop();
    }


    @Override
    public Action onRequest(Http.Request request, Method actionMethod) {
        final EntityManagerFactory emf = ctx.getBean(EntityManagerFactory.class);

        return createActionWithActionFilters(new OpenEntityManagerInViewActionFilter(emf));
    }


    @Override
    public F.Promise<Result> onError(Http.RequestHeader request, Throwable t) {

        String errorMessage = "服务器发生错误";
        ErrorCode errorCode = ErrorCode.InternalError;
        if(t instanceof AppException || t instanceof AppBusinessException) {
            //业务异常
            errorMessage = t.getMessage();
            if(t instanceof AppException) {
                errorCode = ((AppException)t).getErrorCode();
            } else {
                errorCode = ((AppBusinessException)t).getErrorCode();
            }
            Logger.debug(t.getMessage());
        } else {
            Logger.error("服务器发生错误: " + t.getMessage(), t);
        }

        return getResultPromise(request, errorCode, Optional.of(errorMessage));
    }

    @Override
    public F.Promise<Result> onHandlerNotFound(Http.RequestHeader request) {
        return getResultPromise(request, ErrorCode.NotFound, Optional.empty());
    }

    @Override
    public F.Promise<Result> onBadRequest(Http.RequestHeader request, String error) {
        return getResultPromise(request, ErrorCode.BadRequest, Optional.empty());
    }

    private F.Promise<Result> getResultPromise(Http.RequestHeader request, ErrorCode errorCode, Optional<String> errorMsg) {
        Result result;

        Error error = new Error(errorCode, errorMsg, request.uri());
        JsonNode jsonNode = JsonUtils.object2Node(error);

        switch (errorCode.status) {
            case Http.Status.BAD_REQUEST:
                result = Results.badRequest(jsonNode);
                break;
            case Http.Status.NOT_FOUND:
                result = Results.notFound(jsonNode);
                break;
            case Http.Status.CONFLICT:
                result = BaseController.conflict(jsonNode);
                break;
            case Http.Status.UNAUTHORIZED:
                result = BaseController.conflict(jsonNode);
                break;
            default:
                result = Results.internalServerError(jsonNode);
                break;
        }

        return showResult(result, true);
    }


}