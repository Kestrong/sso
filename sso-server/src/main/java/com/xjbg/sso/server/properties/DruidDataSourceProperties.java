package com.xjbg.sso.server.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author kesc
 * @since 2018/8/24
 */
@ConfigurationProperties(prefix = DruidDataSourceProperties.DB_PREFIX)
@Getter
@Setter
public class DruidDataSourceProperties {
    static final String DB_PREFIX = "spring.datasource";
    private String dbType;
    private String url;
    private String username;
    private String password;
    private String driverClassName;
    private int initialSize;
    private int minIdle;
    private int maxActive;
    private int maxWait;
    private int timeBetweenEvictionRunsMillis;
    private int minEvictableIdleTimeMillis;
    private String validationQuery;
    private boolean testWhileIdle;
    private boolean testOnBorrow;
    private boolean testOnReturn;
    private boolean removeAbandoned;
    private int removeAbandonedTimeoutMillis;
    private boolean resetStatEnable;
    private boolean logAbandoned;
    private boolean poolPreparedStatements;
    private int maxPoolPreparedStatementPerConnectionSize;
    private String filters;
    private String connectionProperties;
    private String allow = "";
    private String deny = "";
    private int transactionQueryTimeout;
    private int queryTimeout;
    private String statLoginName;
    private String statLoginPassword;
}
