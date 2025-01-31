package com.buddy.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 우선 C:/uploads/images/에서 이미지를 찾고, 없으면 classpath:/static/images/에서 찾음
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:///C:/uploads/images/")  // 우선순위 1: 로컬 경로
                .addResourceLocations("classpath:/static/images/"); // 우선순위 2: static 경로
    }
}
