package com.xjbg.sso.server.config;

import com.xjbg.sso.core.util.StringUtil;
import com.xjbg.sso.server.filter.CrosFilter;
import com.xjbg.sso.server.filter.SafeUrlFilter;
import com.xjbg.sso.server.properties.CasProperties;
import com.xjbg.sso.server.properties.SafeUrlProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author kesc
 * @since 2019/5/28
 */
@Configuration
@EnableConfigurationProperties(value = {SafeUrlProperties.class})
public class FilterConfig {

    @Bean
    public FilterRegistrationBean corsFilter(CasProperties casProperties) {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new CrosFilter());
        registrationBean.setName("corsFilter");
        if (StringUtil.isNotBlank(casProperties.getAllowOrigins())) {
            registrationBean.addInitParameter("allowOrigins", casProperties.getAllowOrigins());
        }
        registrationBean.addInitParameter("allowMethods", "GET,POST,PUT,DELETE,OPTIONS");
        registrationBean.addInitParameter("allowCredentials", "true");
        registrationBean.addInitParameter("allowHeaders", "Origin,Content-Type,Accept-Encoding,Accept-Language,Cookie,ticket,Referer,Host,User-Agent,Accept,X-Requested-With");
        registrationBean.addInitParameter("exposeHeaders", "ETag,Link,Location");
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(2);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean safeUrlFilter(SafeUrlProperties safeUrlProperties) {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new SafeUrlFilter());
        registrationBean.setName("safeUrlFilter");
        if (StringUtil.isNotBlank(safeUrlProperties.getWhiteList())) {
            registrationBean.addInitParameter(SafeUrlFilter.WHITE_LIST_KEY, safeUrlProperties.getWhiteList());
        }
        if (StringUtil.isNotBlank(safeUrlProperties.getBlackList())) {
            registrationBean.addInitParameter(SafeUrlFilter.BLACK_LIST_KEY, safeUrlProperties.getBlackList());
        }
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }

}
