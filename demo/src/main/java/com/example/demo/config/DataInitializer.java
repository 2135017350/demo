package com.example.demo.config;

import com.example.demo.model.Item;
import com.example.demo.model.User;
import com.example.demo.repository.ItemRepository;
import com.example.demo.repository.UserRepository;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.io.File;
import java.math.BigDecimal;
import java.util.Arrays;

/**
 * 数据库初始化配置类
 * 在 Spring Boot 启动完成后执行，用于初始化默认数据和目录
 */
@Component
@DependsOn("entityManagerFactory") // 关键修复：确保在 Hibernate 建表后执行
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    // 添加 EntityManagerFactory 依赖，确保 JPA 初始化完成
    public DataInitializer(UserRepository userRepository, ItemRepository itemRepository, EntityManagerFactory entityManagerFactory) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. 确保上传目录存在
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
            System.out.println("创建上传目录: " + dir.getAbsolutePath());
        }

        // 2. 初始化默认管理员
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword("123456");
            admin.setNickname("超级管理员");
            admin.setAvatar("https://placeholder.co/100x100/ff8fa3/ffffff?text=Admin");
            admin.setVerified(true);
            admin.setCreditScore(999);
            admin.setBadges(Arrays.asList("官方认证"));
            userRepository.save(admin);
            System.out.println("初始化默认管理员账号: admin / 123456");
        }

        // 3. 初始化默认商品
        try {
            if (itemRepository.count() == 0) {
                Item item = new Item();
                item.setTitle("初音未来 2024 魔法未来 手办");
                item.setCategory("手办");
                item.setIp("VOCALOID");
                item.setPrice(new BigDecimal("998.00"));
                item.setSeller("System");
                item.setCoverUrl("https://placeholder.co/300x300?text=Miku");
                item.setCondition("全新");
                item.setAccessories("盒说全");
                item.setStatus(0); // 在售
                itemRepository.save(item);
                System.out.println("初始化默认商品数据");
            }
        } catch (Exception e) {
            System.err.println("初始化商品数据失败 (可能是表尚未就绪): " + e.getMessage());
        }
    }
}