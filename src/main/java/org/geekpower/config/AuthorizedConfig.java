package org.geekpower.config;

import org.geekpower.intercepter.AuthorizedInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 权限配置
 * 
 * @author songyz
 * @createTime 2020-05-31 12:08:15
 */
@Configuration
public class AuthorizedConfig implements WebMvcConfigurer {

    @Autowired
    private AuthorizedInterceptor authorizedInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authorizedInterceptor)//
                .addPathPatterns("/**") // 拦截的路径
                .excludePathPatterns("/user/login")// 排除的路径
        ;
    }
}
