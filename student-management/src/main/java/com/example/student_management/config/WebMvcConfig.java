package com.example.student_management.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Перенаправление всех не-API путей на index.html
        registry.addViewController("/{path:[^\\.]*}")
                .setViewName("forward:/index.html");

        // Обработка вложенных путей
        registry.addViewController("/{path:[^\\.]*}/**")
                .setViewName("forward:/index.html");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Явное указание расположения статических ресурсов
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }
}