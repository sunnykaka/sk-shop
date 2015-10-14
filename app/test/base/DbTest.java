package base;

import common.exceptions.ErrorCode;
import common.services.GeneralDao;
import common.utils.JsonUtils;
import common.utils.play.BaseGlobal;
import play.Logger;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import utils.Global;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static play.test.Helpers.contentAsString;

/**
 * Created by liubin on 15-10-14.
 */
public interface DbTest {

    default <T> T doInTransaction(EntityManagerCallback<T> callback) {
        EntityManagerFactory emf = BaseGlobal.ctx.getBean(EntityManagerFactory.class);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = null;

        try {
            tx = em.getTransaction();
            tx.begin();

            T result = callback.call(em);

            tx.commit();

            return result;
        }
        catch (Exception e) {
            if ( tx != null && tx.isActive() ) tx.rollback();
            throw e; // or display error message
        }
        finally {
            em.close();
        }

    }

    default <T> T doInTransactionWithGeneralDao(GeneralDaoCallback<T> callback) {
        return doInTransaction(em -> {
            GeneralDao generalDao = new GeneralDao(em);
            return callback.call(generalDao);
        });
    }


    @FunctionalInterface
    static interface EntityManagerCallback<T> {
        T call(EntityManager em);
    }

    @FunctionalInterface
    static interface GeneralDaoCallback<T> {
        T call(GeneralDao generalDao);
    }

    default Result routeWithExceptionHandle(Http.RequestBuilder requestBuilder) {

        Result result;
        Http.RequestImpl req = requestBuilder.build();

        try {
            result = Helpers.route(requestBuilder);
        } catch (Exception e) {
            Global global = new Global();
            return global.onError(req, e).get(3000000L);
        }

        String s = Helpers.contentAsString(result);
        Logger.debug(String.format("request: %s, response: %s", req.toString(), s));
        return result;
    }

    default void assertResultAsError(Result result, ErrorCode expectedErrorCode) {
        assertThat(result.status(), is(expectedErrorCode.status));
        api.response.Error error = JsonUtils.json2Object(contentAsString(result), api.response.Error.class);
        assertThat(error.getCode(), is(expectedErrorCode.getName()));
    }


}
