package com.example.student_management.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Добавляет контроллеры просмотра, чтобы все нераспознанные пути
     * (которые не являются API-эндпоинтами Spring Boot)
     * перенаправлялись на index.html. Это необходимо для работы Angular-роутинга
     * при прямом доступе к URL или при обновлении страницы.
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Перенаправляет все пути, не начинающиеся с /api/ на index.html
        registry.addViewController("/{spring:[\\w-]+}")
                .setViewName("forward:/index.html");
        registry.addViewController("/**/{spring:[\\w-]+}")
                .setViewName("forward:/index.html");
        registry.addViewController("/")
                .setViewName("forward:/index.html");
    }
}
