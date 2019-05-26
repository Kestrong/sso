package com.xjbg.sso.server.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.xjbg.sso.core.util.AESOperator;
import com.xjbg.sso.server.properties.DruidDataSourceProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * @author kesc
 * @since 2018/8/24
 */
@Configuration
@EnableConfigurationProperties(DruidDataSourceProperties.class)
@Slf4j
public class DruidDatasourceConfig {
    @Autowired
    private DruidDataSourceProperties druidDataSourceProperties;
    @Autowired
    private AESOperator aesOperator;

    @Bean
    public ServletRegistrationBean druidServlet() {
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
        // IP白名单
        servletRegistrationBean.addInitParameter("allow", druidDataSourceProperties.getAllow());
        // IP黑名单(共同存在时，deny优先于allow)
        servletRegistrationBean.addInitParameter("deny", druidDataSourceProperties.getDeny());
        try {
            //控制台管理用户
            servletRegistrationBean.addInitParameter("loginUsername", aesOperator.decrypt(druidDataSourceProperties.getStatLoginName()));
            servletRegistrationBean.addInitParameter("loginPassword", aesOperator.decrypt(druidDataSourceProperties.getStatLoginPassword()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        //是否能够重置数据 禁用HTML页面上的“Reset All”功能
        servletRegistrationBean.addInitParameter("resetEnable", "false");
        return servletRegistrationBean;
    }

    @Primary
    @Bean(name = "dataSource")
    public DataSource dataSource() {
        DruidDataSource datasource = new DruidDataSource();
        datasource.setValidationQuery("select 1");
        try {
            datasource.setUrl(aesOperator.decrypt(druidDataSourceProperties.getUrl()));
            datasource.setUsername(aesOperator.decrypt(druidDataSourceProperties.getUsername()));
            datasource.setPassword(aesOperator.decrypt(druidDataSourceProperties.getPassword()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        datasource.setDriverClassName(druidDataSourceProperties.getDriverClassName());
        datasource.setDbType(druidDataSourceProperties.getDbType());
        //configuration
        datasource.setInitialSize(druidDataSourceProperties.getInitialSize());
        datasource.setMinIdle(druidDataSourceProperties.getMinIdle());
        datasource.setMaxActive(druidDataSourceProperties.getMaxActive());
        datasource.setMaxWait(druidDataSourceProperties.getMaxWait());
        datasource.setTimeBetweenEvictionRunsMillis(druidDataSourceProperties.getTimeBetweenEvictionRunsMillis());
        datasource.setMinEvictableIdleTimeMillis(druidDataSourceProperties.getMinEvictableIdleTimeMillis());
        datasource.setTestWhileIdle(druidDataSourceProperties.isTestWhileIdle());
        datasource.setLogAbandoned(druidDataSourceProperties.isLogAbandoned());
        datasource.setRemoveAbandoned(druidDataSourceProperties.isRemoveAbandoned());
        datasource.setResetStatEnable(druidDataSourceProperties.isResetStatEnable());
        datasource.setRemoveAbandonedTimeoutMillis(druidDataSourceProperties.getRemoveAbandonedTimeoutMillis());
        datasource.setTestOnBorrow(druidDataSourceProperties.isTestOnBorrow());
        datasource.setTestOnReturn(druidDataSourceProperties.isTestOnReturn());
        datasource.setPoolPreparedStatements(druidDataSourceProperties.isPoolPreparedStatements());
        datasource.setMaxPoolPreparedStatementPerConnectionSize(druidDataSourceProperties.getMaxPoolPreparedStatementPerConnectionSize());
        datasource.setClearFiltersEnable(false);
        datasource.setQueryTimeout(druidDataSourceProperties.getQueryTimeout());
        datasource.setTransactionQueryTimeout(druidDataSourceProperties.getTransactionQueryTimeout());
        try {
            datasource.setFilters(druidDataSourceProperties.getFilters());
        } catch (SQLException e) {
            log.error("druid configuration initialization filter: " + e);
        }
        datasource.setConnectionProperties(druidDataSourceProperties.getConnectionProperties());
        return datasource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSource());
        jdbcTemplate.setFetchSize(20);
        jdbcTemplate.setQueryTimeout(druidDataSourceProperties.getQueryTimeout());
        return jdbcTemplate;
    }
}
