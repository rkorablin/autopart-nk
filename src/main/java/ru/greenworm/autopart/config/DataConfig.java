package ru.greenworm.autopart.config;

import javax.sql.DataSource;

import org.apache.tomcat.jdbc.pool.PoolConfiguration;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
public class DataConfig {

	@Bean(name = "dataSource")
	public DataSource dataSource() {
		PoolConfiguration poolProperties = new PoolProperties();
		poolProperties.setUsername("autopart");
		poolProperties.setPassword("dfkjghn498gn4859gmwp9gw4fhgfshfg");
		poolProperties.setDriverClassName("org.postgresql.Driver");
		poolProperties.setUrl("jdbc:postgresql://127.0.0.1:51990/autopart");
		poolProperties.setInitialSize(20);
		poolProperties.setMaxActive(100);
		poolProperties.setMinIdle(20);
		poolProperties.setMaxIdle(50);
		DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource(poolProperties);
		return dataSource;
	}

	@Autowired
	@Bean(name = "sessionFactory")
	public SessionFactory sessionFactory(DataSource dataSource) {
		LocalSessionFactoryBuilder sessionBuilder = new LocalSessionFactoryBuilder(dataSource);
		sessionBuilder.scanPackages("ru.greenworm.autopart.model");
		sessionBuilder.setProperty("hibernate.show_sql", "false");
		sessionBuilder.setProperty("hibernate.hbm2ddl.auto", "update");
		sessionBuilder.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQL82Dialect");
		sessionBuilder.setProperty("hibernate.current_session_context_class", "org.springframework.orm.hibernate4.SpringSessionContext");
		sessionBuilder.setProperty("hibernate.enable_lazy_load_no_trans", "true");
		
		return sessionBuilder.buildSessionFactory();
	}

	@Autowired
	@Bean(name = "transactionManager")
	public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
		HibernateTransactionManager transactionManager = new HibernateTransactionManager(sessionFactory);
		return transactionManager;
	}

}