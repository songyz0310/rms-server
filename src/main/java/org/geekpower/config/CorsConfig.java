package org.geekpower.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 
 * 
 * @author songyz
 * @createTime 2020-06-05 16:49:48
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") //
                .allowCredentials(true) //
                .allowedHeaders("*") //
                .allowedOrigins("*") //
                .allowedMethods("*");

    }
}
