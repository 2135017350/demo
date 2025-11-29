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

    private String coverUrl;

    @ElementCollection
    private List<String> detailImages = new ArrayList<>();

    private String videoUrl;

    // RENAME field to itemCondition to match index.html and avoid SQL keyword
    @Column(name = "item_condition")
    private String itemCondition;

    private String accessories;
    private String serialNum;

    private Integer status;

    private boolean isVerified;
    private String verifyReport;

    @Column(length = 1000)
    private String description;
}