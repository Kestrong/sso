package com.xjbg.sso.server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xjbg.sso.core.util.CollectionUtil;
import com.xjbg.sso.core.util.CustomObjectMapper;
import com.xjbg.sso.server.properties.EnvProperties;
import com.xjbg.sso.server.service.oauth.AuthTokenInterceptor;
import com.xjbg.sso.server.service.oauth.OauthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.*;

/**
 * @author kesc
 * @since 2019/2/27
 */
@Configuration
@EnableConfigurationProperties(EnvProperties.class)
public class WebMvcConfig extends WebMvcConfigurationSupport {
    @Autowired
    private EnvProperties envProperties;
    @Autowired
    private LocalValidatorFactoryBean validator;
    @Autowired
    private OauthService oauthService;

    @Bean
    public AuthTokenInterceptor authTokenInterceptor() {
        AuthTokenInterceptor authTokenInterceptor = new AuthTokenInterceptor();
        authTokenInterceptor.setOauthService(oauthService);
        return authTokenInterceptor;
    }

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authTokenInterceptor()).addPathPatterns("/user/**");
        super.addInterceptors(registry);
    }

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        Map<String, String> resourceMapping = envProperties.getResourceMapping();
        if (CollectionUtil.isNotEmpty(resourceMapping)) {
            for (Map.Entry<String, String> entry : resourceMapping.entrySet()) {
                registry.addResourceHandler(entry.getKey()).addResourceLocations(entry.getValue());
            }
        }
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.addAll(messageConverters());
    }

    private List<HttpMessageConverter<?>> messageConverters() {
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        jsonConverter.setObjectMapper(createDefaultObjectMapper());
        messageConverters.add(jsonConverter);
        return messageConverters;
    }

    private ObjectMapper createDefaultObjectMapper() {
        CustomObjectMapper mapper = new CustomObjectMapper();
        mapper.setCamelCaseToLowerCaseWithUnderscores(false);
        mapper.setDateFormatPattern("yyyy-MM-dd HH:mm:ss");
        mapper.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        mapper.init();
        return mapper;
    }

    @Bean
    public HttpMessageConverters getHttpMessageConverters() {
        return new HttpMessageConverters(Collections.emptyList());
    }

    @Bean
    @Primary
    public CommonsMultipartResolver getCommonsMultipartResolver() {
        CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver();
        commonsMultipartResolver.setMaxUploadSize(1024 * 1024 * 100);
        commonsMultipartResolver.setMaxInMemorySize(1024 * 2);
        commonsMultipartResolver.setDefaultEncoding("UTF-8");
        commonsMultipartResolver.setResolveLazily(Boolean.TRUE);
        return commonsMultipartResolver;
    }

    @Override
    protected Validator getValidator() {
        return validator;
    }
}
