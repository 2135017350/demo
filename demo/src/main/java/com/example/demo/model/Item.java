package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    private Long id;
    private String title;       // 商品标题
    private String category;    // 分类
    private BigDecimal price;   // 价格
    private String seller;      // 卖家
    private String imageUrl;    // 图片地址
    private Integer status;     // 0:在售, 1:交易中, 2:已售出
    private String description; // 描述
}
