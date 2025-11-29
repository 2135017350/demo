package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // 初始化默认管理员账号 (如果数据库为空)
    @PostConstruct
    public void init() {
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
        }
    }

    @GetMapping("/login")
    public String loginPage() { return "login"; }

    @GetMapping("/register")
    public String registerPage() { return "register"; }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("user");
        return "redirect:/";
    }

    @GetMapping("/profile")
    public String profilePage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        // 重新从数据库获取最新信息
        user = userRepository.findById(user.getId()).orElse(user);
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/api/login")
    @ResponseBody
    public Map<String, Object> doLogin(@RequestBody Map<String, String> payload, HttpSession session) {
        String username = payload.get("username");
        String password = payload.get("password");

        // 从数据库查询
        User user = userRepository.findByUsername(username).orElse(null);

        if (user != null && user.getPassword().equals(password)) {
            session.setAttribute("user", user);
            return Map.of("success", true, "msg", "登录成功");
        }
        return Map.of("success", false, "msg", "账号或密码错误");
    }

    @PostMapping("/api/register")
    @ResponseBody
    public Map<String, Object> doRegister(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");

        if (userRepository.existsByUsername(username)) {
            return Map.of("success", false, "msg", "用户名已存在");
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(payload.get("password"));
        newUser.setNickname(payload.get("nickname"));
        newUser.setPhone(payload.get("phone"));
        newUser.setEmail(payload.get("email"));
        newUser.setAvatar("https://placeholder.co/100x100/dddddd/555555?text=" + username);
        newUser.setVerified(false);
        newUser.setCreditScore(100);

        userRepository.save(newUser); // 保存到数据库
        return Map.of("success", true, "msg", "注册成功，请登录");
    }

    // 省略 updateProfile，逻辑同上，改为 userRepository.save(user)
}