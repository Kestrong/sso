package com.xjbg.sso.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.session.SessionAutoConfiguration;
import org.springframework.boot.autoconfigure.web.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.web.MultipartAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author kesc
 * @since 2019/5/13
 */
@EnableDiscoveryClient
@SpringBootApplication(
        scanBasePackages = {
                "com.xjbg.sso.server"
        }, exclude = {SessionAutoConfiguration.class, HttpMessageConvertersAutoConfiguration.class, MultipartAutoConfiguration.class}
)
public class SsoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SsoApplication.class, args);
    }

}
