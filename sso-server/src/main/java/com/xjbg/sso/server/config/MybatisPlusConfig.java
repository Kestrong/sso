package com.xjbg.sso.server.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.io.IOException;

/**
 * @author kesc
 * @since 2018/1/31
 */
@Configuration
@MapperScan(basePackages = {
        "com.xjbg.sso.server.dao"
})
public class MybatisPlusConfig {

    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor interceptor = new PaginationInterceptor();
        interceptor.setDialectType(DbType.MYSQL.getDb());
        return interceptor;
    }

    @Bean
    public MybatisSqlSessionFactoryBean sqlSessionFactory(DataSource dataSource) throws IOException {
        MybatisSqlSessionFactoryBean bean = new MybatisSqlSessionFactoryBean();
        bean.setFailFast(true);
        bean.setDataSource(dataSource);
        bean.setPlugins(new Interceptor[]{paginationInterceptor()});
        //xml and po
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        bean.setMapperLocations(resolver.getResources("classpath:com/xjbg/sso/server/mapper/*.xml"));
        bean.setTypeAliasesPackage("com.xjbg.sso.server.dao.model");
        //globalConfig
        GlobalConfig globalConfig = new GlobalConfig();
        GlobalConfig.DbConfig dbConfig = new GlobalConfig.DbConfig();
        dbConfig.setDbType(DbType.MYSQL);
        dbConfig.setIdType(IdType.INPUT);
        dbConfig.setFieldStrategy(FieldStrategy.NOT_NULL);
        dbConfig.setTableUnderline(true);
        globalConfig.setDbConfig(dbConfig);
        globalConfig.setSqlInjector(new DefaultSqlInjector());
        bean.setGlobalConfig(globalConfig);
        //configuration
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setMapUnderscoreToCamelCase(true);
        bean.setConfiguration(configuration);
        return bean;
    }

    @Bean
    @ConditionalOnMissingBean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory, ExecutorType.SIMPLE);
    }
}
