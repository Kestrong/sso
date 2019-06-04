# sso

* [Sso Server](#sso-server)
* [Sso Client](#sso-client)
* [Todo](#todo)

## Sso Server

单独部署，已经提供了默认的配置，可根据实际情况自己调整，restful api所以没有前端页面。

为了跨域和安全，可以选择配置CrosFilter和SafeUrlFilter。

密码默认使用hash加盐的方式，可以定制自己的AuthenticationManager、AuthenticationHandler和CredentialMatcher。

接口文档：${scheme}://${host}:${port}/${context}/swagger-ui.html

## Sso Client
默认提供了feign-client调用微服务，所以需要额外部署注册中心(Eureka)。
```xml
<dependency>
    <groupId>com.xjbg</groupId>
    <artifactId>sso-client</artifactId>
    <version>${sso-client-version}</version>
</dependency>
```
cas-client使用案例(保证filter的先后顺序如下)
```
@Configuration
public class CasConfig {

    @Bean
    public SessionStorage sessionStorage() {
        //默认用hashmap维护session，用于单点登出，可选方案(基于redis)：spring-session和shiro-session
        return new HashMapSessionStorageImpl();
    }

    @Bean
    public SingleSignOutHttpSessionListener singleSignOutHttpSessionListener(SessionStorage sessionStorage) {
        //使用hashmap时必须配置该监听器用于移除过期的session避免oom
        SingleSignOutHttpSessionListener sessionListener = new SingleSignOutHttpSessionListener();
        sessionListener.setSessionStorage(sessionStorage);
        return sessionListener;
    }

    @Bean
    public FilterRegistrationBean casAuthenticationFilter() {
        FilterRegistrationBean filter = getFilterBean(new CasAuthenticationFilter(), "webContextFilter");
        filter.addInitParameter(ConfigKeyConstants.CAS_SERVER_LOGIN_URL_KEY, "http://localhost:8080/sso-server/cas/login");
        filter.addInitParameter(ConfigKeyConstants.SERVICE_KEY, "http://localhost:8081/consume/hello");
        filter.setOrder(2);
        return filter;
    }

    @Bean
    public FilterRegistrationBean casValidationFilter() {
        FilterRegistrationBean filter = getFilterBean(new CasValidationFilter(), "casValidationFilter");
        filter.addInitParameter(ConfigKeyConstants.SERVICE_KEY, "http://localhost:8081/consume/hello");
        filter.addInitParameter(ConfigKeyConstants.ENCODE_SERVICE_URL_KEY, "false");
        filter.setOrder(3);
        return filter;
    }

    @Bean
    public FilterRegistrationBean casSingleLogoutFilter() {
        FilterRegistrationBean filter = getFilterBean(new CasSingleLogoutFilter(), "casSingleLogoutFilter");
        filter.addInitParameter(ConfigKeyConstants.SERVICE_KEY, "http://localhost:8081/consume/hello");
        filter.setOrder(1);
        return filter;
    }

    private FilterRegistrationBean getFilterBean(Filter filter, String filterName) {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(filter);
        registrationBean.setName(filterName);
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }
}
```
cas登录流程：引导至登录页提交用户密码获取tgt->用tgt获取ticket->使用ticket调用接口。
（注：ticket只能使用一次，成功之后客户端会在session维持登录状态不需要重复获取）
cas代理模式：代理端使用tgt获取proxyTicket，使用proxyTicket调用被代理端。
（注：为了兼容微服务和简化流程，rest风格的cas代理模式跟2.0协议有所不同）

## todo
oauth+openId 