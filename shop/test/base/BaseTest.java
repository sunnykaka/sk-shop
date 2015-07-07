package base;

import common.services.GeneralDao;
import common.utils.play.BaseGlobal;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import play.Application;
import play.Play;
import play.api.ApplicationLoader;
import play.api.ApplicationLoader$;
import play.api.Environment$;
import play.api.Mode$;
import play.core.DefaultWebCommands;
import play.test.Helpers;
import play.test.WithApplication;
import scala.Option;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.io.File;

/**
 * Created by liubin on 15/4/6.
 */
public abstract class BaseTest extends WithApplication {

//    private static boolean setUpIsDone = false;
//
//    @Before
//    @Override
//    public void startPlay() {
//        if(!setUpIsDone) {
//            app = provideApplication();
//            Helpers.start(app);
//            setUpIsDone = true;
//        }
//    }
//
//    @After
//    @Override
//    public void stopPlay() {
//        //do nothing
////        if (app != null) {
////            Helpers.stop(app);
////            app = null;
////        }
//    }


    @Override
    protected Application provideApplication() {
        ApplicationLoader.Context context = ApplicationLoader$.MODULE$.createContext(
                Environment$.MODULE$.simple(new File("."), Mode$.MODULE$.Test()),
                new scala.collection.immutable.HashMap<>(),
                Option.empty(),
                new DefaultWebCommands());

        ApplicationLoader loader = ApplicationLoader$.MODULE$.apply(context);

        return loader.load(context).injector().instanceOf(Application.class);
    }


    protected <T> T doInTransaction(EntityManagerCallback<T> callback) {
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

    protected <T> T doInTransactionWithGeneralDao(GeneralDaoCallback<T> callback) {
        return doInTransaction(em -> {
            GeneralDao generalDao = new GeneralDao(em);
            return callback.call(generalDao);
        });
    }


    @FunctionalInterface
    protected static interface EntityManagerCallback<T> {
        T call(EntityManager em);
    }

    @FunctionalInterface
    protected static interface GeneralDaoCallback<T> {
        T call(GeneralDao generalDao);
    }
}
