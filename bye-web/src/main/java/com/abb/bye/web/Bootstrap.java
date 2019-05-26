package com.abb.bye.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ImportResource;

/**
 * @author: camelweiming@163.com
 * @since: 2018/6/8 下午7:37
 */
@SpringBootApplication(scanBasePackages = {"com.abb.bye.web", "com.abb.flowable"}, exclude = DataSourceAutoConfiguration.class)
@ImportResource({"classpath:/application-context.xml"})
public class Bootstrap {
    public static void main(String[] args) {
        SpringApplication.run(Bootstrap.class, args);
    }
}
