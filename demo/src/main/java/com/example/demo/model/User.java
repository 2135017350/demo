package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users") // 数据库表名
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String nickname;
    private String phone;
    private String email;

    private String avatar;
    private String signature;
    private String tags;
    private String address;

    private boolean isVerified;
    private int creditScore;

    @ElementCollection(fetch = FetchType.EAGER) // 存储 List<String>
    private List<String> badges = new ArrayList<>();
}