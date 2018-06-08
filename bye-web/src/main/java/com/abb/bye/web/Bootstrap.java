package com.abb.bye.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @author: camelweiming@163.com
 * @since: 2018/6/8 下午7:37
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class Bootstrap {
    public static void main(String[] args) {
        SpringApplication.run(Bootstrap.class, args);
    }
}
