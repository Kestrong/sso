# 简介

该项目的初衷是为了解决公司内部多个系统多套账户的登录体系、微服务平台架构以及对外提供的开放平台的认证和鉴权问题。后来整理了一下打算写成一个通用的项目。

项目分成2部分，**sso-server**和**sso-client**，前者需要单独部署用，后者用于客户端（后端，默认使用feign调用，所以需要微服务的支持以及一个注册中心，~~后续看情况添加httpclient支持~~），目前通过restful api的形式提供服务，所以没有前端界面，仅提供swagger接口文档。

# 目录
* [Sso Server](#sso-server)
* [Sso Client](#sso-client)
* [Cas](#cas)
* [OAuth](#oauth)
* [Response Code](#response-code)
* [Todo](#todo)

# Sso Server

该项目需要单独部署，用于提供身份认证和鉴权服务，默认提供了一些配置和可插拔接口，如果无法满足可以自定义配置和接口实现。

**接口文档：**`${scheme}://${host}:${port}/${context}/swagger-ui.html`

**[必选配置]**

* **cas**
  * [CasConfig](/src/main/java/com/xjbg/sso/server/config/CasConfig.java)
  * [CasService](/src/main/java/com/xjbg/sso/server/service/cas/CasService.java)
* **oauth**
  * [OauthService](/src/main/java/com/xjbg/sso/server/service/oauth/OauthService.java)
  * [AuthTokenInterceptor](/src/main/java/com/xjbg/sso/server/service/oauth/AuthTokenInterceptor.java)

**[可选配置]**

* [CrosFilter](/src/main/java/com/xjbg/sso/server/filter/CrosFilter.java) 跨域
* [SafeUrlFilter](/src/main/java/com/xjbg/sso/server/filter/SafeUrlFilter.java) 白名单访问

# Sso Client

**项目依赖：**
```xml
<dependency>
    <groupId>com.xjbg</groupId>
    <artifactId>sso-client</artifactId>
    <version>${sso-client-version}</version>
</dependency>
```
**api**
* [CasApi](/src/main/java/com/xjbg/sso/client/api/CasApi.java)
* [OauthApi](/src/main/java/com/xjbg/sso/client/api/OauthApi.java)
* [UserApi](/src/main/java/com/xjbg/sso/client/api/UserApi.java)

**[必选配置]**

* **cas** 配置filter先后顺序如下
  * [CasSingleLogoutFilter](/src/main/java/com/xjbg/sso/client/filter/CasSingleLogoutFilter.java) 用于单点登出
  * [CasAuthenticationFilter](/src/main/java/com/xjbg/sso/client/filter/CasAuthenticationFilter.java) 用于检查认证，其中`casServerLoginUrl`必须配置为`${sso-server-path}/cas/login`
  * [CasValidationFilter](/src/main/java/com/xjbg/sso/client/filter/CasValidationFilter.java) 用于调用sso-server的cas服务校验ticket
* **oauth**
  * [AuthTokenInterceptor](/src/main/java/com/xjbg/sso/client/interceptor/AuthTokenInterceptor.java) 配合 [AuthScope](/src/main/java/com/xjbg/sso/core/annonation/AuthScope.java)使用

**[可选配置]**

* **cas**
  * [SessionStorage](/src/main/java/com/xjbg/sso/client/session/SessionStorage.java) 用于保存ticket和session的关系，默认用hashmap维护session，**用于单点登出**，可选方案(基于redis)：spring-session和shiro-session
  * [SingleSignOutHttpSessionListener](/src/main/java/com/xjbg/sso/client/session/SingleSignOutHttpSessionListener.java) 使用**hashmap**的**SessionStorage**时必须配置该监听器用于移除过期的session避免oom

# Cas

1. cas登录流程：引导至登录页提交用户密码获取tgt->用tgt获取ticket->使用ticket调用接口。*（注：ticket只能使用一次不论成功与否，成功之后客户端会在session维持登录状态不需要重复获取）*
2. cas代理模式：代理端每次调用接口前都需要使用tgt获取proxyTicket，使用proxyTicket调用被代理端（用于纯后端交互的场景，不涉及session和cookie）。
*（注：为了兼容微服务和简化流程，rest风格的cas代理模式跟2.0协议有所不同）*
3. HttpStatus为403时为ticket无效或者拒绝访问
# OAuth

HttpStatus为401时为未授权或者权限（scope）不够

| grandType  | refreshToken  | support  |
| :------------: | :------------: | :------------: |
| 授权码模式（authorization code） | ✔  |  ✔ |
| 简化模式（implicit）  | **×**  | ✔  |
| 密码模式（resource owner password credentials）  | ✔  |  **×** |
| 客户端模式（client credentials）  |**×**   |  ✔ |

**(注：grandType和responseType见枚举类：[GrandType](/src/main/java/com/xjbg/sso/core/enums/GrandType.java)和[ResponseType](/src/main/java/com/xjbg/sso/core/enums/ResponseType.java))**

# Response Code

| 返回码  |说明   |
| :------------: | :------------: |
| B10011  | redirect uri的值与后台配置（注册时应用的回调uri）不一致  |
|  401 |  未授权或者权限不够 |
|  500 |  系统异常 |

# Todo
后续有需求或者有空继续`Developer`和`Administrator`功能的开发（应该是没空了。。）。