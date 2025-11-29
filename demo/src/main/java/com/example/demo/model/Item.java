package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "items")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String category;
    private String ip;
    private BigDecimal price;
    private String seller;

    private String coverUrl; // 封面图路径

    @ElementCollection // 存储多图
    private List<String> detailImages = new ArrayList<>();

    private String videoUrl;
    private String condition; // 全新/二手
    private String accessories;
    private String serialNum;

    private Integer status; // 0:在售

    private boolean isVerified;
    private String verifyReport;

    @Column(length = 1000)
    private String description;
}