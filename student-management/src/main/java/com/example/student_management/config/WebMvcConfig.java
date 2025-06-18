package com.example.student_management.config; // Убедитесь, что это ваш реальный пакет

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Перенаправляет все пути, которые не являются API-эндпоинтами, на index.html
        // Обратите внимание на \\w вместо \w
        registry.addViewController("/{spring:[\\w-]+}")
                .setViewName("forward:/");
        registry.addViewController("/**/{spring:[\\w-]+}")
                .setViewName("forward:/");
        registry.addViewController("/")
                .setViewName("forward:/index.html");
    }
}
