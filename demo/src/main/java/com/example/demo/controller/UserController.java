package com.example.demo.controller;

import com.example.demo.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class UserController {

    // 模拟用户数据库
    public static List<User> userList = new ArrayList<>();

    static {
        userList.add(new User(1L, "admin", "123456", "管理员"));
        userList.add(new User(2L, "user", "123456", "普通吃谷人"));
    }

    // === 页面跳转 ===

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("user");
        return "redirect:/";
    }

    // === API 接口 ===

    @PostMapping("/api/login")
    @ResponseBody
    public Map<String, Object> doLogin(@RequestBody Map<String, String> payload, HttpSession session) {
        String username = payload.get("username");
        String password = payload.get("password");

        User user = userList.stream()
                .filter(u -> u.getUsername().equals(username) && u.getPassword().equals(password))
                .findFirst()
                .orElse(null);

        if (user != null) {
            session.setAttribute("user", user); // 写入 Session
            return Map.of("success", true, "msg", "登录成功");
        }
        return Map.of("success", false, "msg", "用户名或密码错误");
    }

    @PostMapping("/api/register")
    @ResponseBody
    public Map<String, Object> doRegister(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String password = payload.get("password");
        String nickname = payload.get("nickname");

        if (userList.stream().anyMatch(u -> u.getUsername().equals(username))) {
            return Map.of("success", false, "msg", "用户名已存在");
        }

        long newId = userList.size() + 1L;
        userList.add(new User(newId, username, password, nickname));
        return Map.of("success", true, "msg", "注册成功，请登录");
    }
}