package com.abb.bye.web.config;

import com.abb.bye.web.interceptors.JsonInterceptor;
import com.abb.bye.web.interceptors.LoginInterceptor;
import com.abb.bye.web.interceptors.ToolsInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @author cenpeng.lwm
 * @since 2019/5/26
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Resource
    private LoginInterceptor loginInterceptor;
    @Resource
    private ToolsInterceptor toolsInterceptor;
    @Resource
    private JsonInterceptor jsonInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jsonInterceptor);
        registry.addInterceptor(toolsInterceptor);
        registry.addInterceptor(loginInterceptor).addPathPatterns("/**").excludePathPatterns("/login.htm", "/sign_in.htm");
    }
}
