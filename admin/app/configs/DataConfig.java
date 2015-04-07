package configs;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import common.exceptions.AppBusinessException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import play.Play;

import java.beans.PropertyVetoException;
import java.util.HashMap;

import play.Logger;

@Configuration
@EnableTransactionManagement
public class DataConfig {

    @Bean
    public EntityManagerFactory entityManagerFactory() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setShowSql(true);
        LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactory.setPackagesToScan("models", "user.models", "product.models", "ordercenter.models");
        entityManagerFactory.setJpaVendorAdapter(vendorAdapter);
        entityManagerFactory.setDataSource(dataSource());
        entityManagerFactory.setJpaPropertyMap(new HashMap<String, String>(){{
            put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
            put("hibernate.format_sql", "true");
            put("hibernate.query.substitutions", "true 1, false 0");
            put("hibernate.default_batch_fetch_size", "50");
            put("hibernate.jdbc.batch_size", "50");
            put("hibernate.order_inserts", "true");
            put("hibernate.max_fetch_depth", "2");
            put("hibernate.current_session_context_class", "org.springframework.orm.hibernate4.SpringSessionContext");
        }});
        entityManagerFactory.afterPropertiesSet();
        return entityManagerFactory.getObject();
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager(entityManagerFactory());

        return transactionManager;
    }

    @Bean
    public DataSource dataSource(){

        play.Configuration configuration = Play.application().configuration();

        try {
            ComboPooledDataSource ds = new ComboPooledDataSource();
            ds.setDriverClass(configuration.getString("db.default.driver"));
            ds.setJdbcUrl(configuration.getString("db.default.url"));
            ds.setUser(configuration.getString("db.default.user"));
            ds.setPassword(configuration.getString("db.default.password"));
            ds.setMaxPoolSize(configuration.getInt("db.default.maxPoolSize"));
            ds.setMinPoolSize(configuration.getInt("db.default.minPoolSize"));
            ds.setInitialPoolSize(configuration.getInt("db.default.initialPoolSize"));
            ds.setAcquireIncrement(configuration.getInt("db.default.acquireIncrement"));
            ds.setMaxIdleTime(configuration.getInt("db.default.maxIdleTime"));
            ds.setIdleConnectionTestPeriod(configuration.getInt("db.default.idleConnectionTestPeriod"));
            ds.setMaxStatements(configuration.getInt("db.default.maxStatements"));
            ds.setCheckoutTimeout(configuration.getInt("db.default.checkoutTimeout"));
            ds.setBreakAfterAcquireFailure(configuration.getBoolean("db.default.breakAfterAcquireFailure"));
            ds.setTestConnectionOnCheckin(configuration.getBoolean("db.default.testConnectionOnCheckin"));
            ds.setPreferredTestQuery(configuration.getString("db.default.preferredTestQuery"));

            return ds;

        } catch (PropertyVetoException e) {
            Logger.error("", e);
            throw new AppBusinessException("启动加载数据库连接配置失败");
        }

//        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setDriverClassName(configuration.getString("db.default.driver"));
//        dataSource.setUrl(configuration.getString("db.default.url"));
//        return dataSource;
    }

}