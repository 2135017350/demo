package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 将 /uploads/** 路径映射到本地文件系统
        // 注意：方法名应该是 addResourceHandlers (复数)，而不是 addResourceHandler
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();

        // 必须加上 "file:" 前缀，并确保路径以 / 结尾
        String location = "file:" + uploadPath.toString() + "/";

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location);
    }
}