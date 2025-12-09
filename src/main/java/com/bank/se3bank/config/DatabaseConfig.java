package com.bank.se3bank.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * تطبيق Singleton Pattern لإدارة اتصالات قاعدة البيانات
 * يتم إنشاء DataSource واحد فقط للتطبيق بأكمله
 */
@Configuration
@EnableTransactionManagement
@Slf4j
public class DatabaseConfig {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${spring.datasource.driver-class-name}")
    private String dbDriver;

    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String hibernateDdlAuto;

    @Value("${spring.jpa.show-sql:false}")
    private boolean showSql;

    @Value("${spring.jpa.properties.hibernate.dialect}")
    private String hibernateDialect;

    /**
     * Singleton Bean - DataSource واحد للتطبيق بأكمله
     * يتم إدارة اتصالات Connection Pool باستخدام HikariCP
     */
    @Bean
    public DataSource dataSource() {
        log.info("📊 تهيئة Singleton DataSource...");
        
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dbUrl);
        config.setUsername(dbUsername);
        config.setPassword(dbPassword);
        config.setDriverClassName(dbDriver);
        
        // إعدادات Connection Pool الأمثل
        config.setMaximumPoolSize(20);
        config.setMinimumIdle(5);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setPoolName("BankDBPool");
        
        // إعدادات إضافية لأداء أفضل
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        
        HikariDataSource dataSource = new HikariDataSource(config);
        log.info("✅ تم تهيئة Singleton DataSource بنجاح");
        
        return dataSource;
    }

    /**
     * Entity Manager Factory باستخدام DataSource السينجلتون
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        log.info("🏗️ تهيئة Entity Manager Factory...");
        
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.bank.se3bank");
        
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", hibernateDdlAuto);
        properties.put("hibernate.show_sql", showSql);
        properties.put("hibernate.dialect", hibernateDialect);
        properties.put("hibernate.format_sql", true);
        properties.put("hibernate.jdbc.batch_size", 20);
        properties.put("hibernate.order_inserts", true);
        properties.put("hibernate.order_updates", true);
        properties.put("hibernate.generate_statistics", true);
        
        em.setJpaPropertyMap(properties);
        
        log.info("✅ تم تهيئة Entity Manager Factory بنجاح");
        return em;
    }

    /**
     * Transaction Manager باستخدام Entity Manager Factory
     */
    @Bean
    public PlatformTransactionManager transactionManager(
            LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory.getObject());
        return transactionManager;
    }

    /**
     * طريقة لاختبار اتصال قاعدة البيانات
     */
    public void testConnection() {
        try (var connection = dataSource().getConnection()) {
            log.info("✅ اتصال قاعدة البيانات ناجح: {}", 
                connection.getMetaData().getDatabaseProductName());
        } catch (Exception e) {
            log.error("❌ فشل اتصال قاعدة البيانات: {}", e.getMessage());
            throw new RuntimeException("فشل اتصال قاعدة البيانات", e);
        }
    }
}