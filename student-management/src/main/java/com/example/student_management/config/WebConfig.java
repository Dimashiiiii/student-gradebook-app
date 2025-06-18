package com.example.student_management.config; // Проверьте правильность вашего пакета

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        // Используем AntPathMatcher для сопоставления путей
        configurer.setUseTrailingSlashMatch(true) // Опционально: разрешает или запрещает / в конце URL
                .setUseRegisteredSuffixPatternMatch(true) // Опционально: разрешает или запрещает сопоставление суффиксов (.html, .json)
                .setPatternParser(null); // Это заставляет Spring Boot использовать AntPathMatcher
    }
}
