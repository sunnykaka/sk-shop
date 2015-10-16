package common.utils.test;

import common.services.GeneralDao;
import common.utils.play.BaseGlobal;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;

/**
 * 如果测试类要访问数据库，可以实现这个接口
 * Created by liubin on 15-10-14.
 */
public interface DbTest {

    /**
     * 在一个数据库事务中执行参数传递的逻辑, 同时GeneralDao当做回调参数传递
     * @param callback
     * @param <T>
     * @return
     */
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

    /**
     * 在一个session中执行参数传递的逻辑, 同时GeneralDao当做回调参数传递。使用类似OpenSessionInView的解决方案
     * @param callback
     * @param <T>
     * @return
     */
    default <T> T doInSingleSession(EntityManagerCallback<T> callback) {

        EntityManagerFactory emf = BaseGlobal.ctx.getBean(EntityManagerFactory.class);
        EntityManager em;
        try {
            em = emf.createEntityManager();
            EntityManagerHolder emHolder = new EntityManagerHolder(em);
            TransactionSynchronizationManager.bindResource(emf, emHolder);
        } catch (PersistenceException ex) {
            throw new DataAccessResourceFailureException("Could not create JPA EntityManager", ex);
        }

        try {
            return callback.call(em);

        } finally {
            EntityManagerHolder emHolder = (EntityManagerHolder)
                    TransactionSynchronizationManager.unbindResource(emf);
            EntityManagerFactoryUtils.closeEntityManager(emHolder.getEntityManager());
        }

    }


    /**
     * 在一个数据库事务中执行参数传递的逻辑, 同时GeneralDao当做回调参数传递
     * @param callback
     * @param <T>
     * @return
     */
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


}
