package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // [已删除] init() 方法已移至 DataInitializer.java 以修复启动报错

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

    // 资料更新接口
    @PostMapping("/api/profile/update")
    @ResponseBody
    public Map<String, Object> updateProfile(@RequestBody Map<String, String> payload, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) return Map.of("success", false, "msg", "未登录");

        User dbUser = userRepository.findById(currentUser.getId()).orElse(null);
        if (dbUser != null) {
            dbUser.setNickname(payload.get("nickname"));
            dbUser.setSignature(payload.get("signature"));
            dbUser.setTags(payload.get("tags"));
            dbUser.setAddress(payload.get("address"));

            if ("true".equals(payload.get("verify"))) {
                if (!dbUser.isVerified()) {
                    dbUser.setVerified(true);
                    dbUser.setCreditScore(dbUser.getCreditScore() + 20);
                }
            }

            userRepository.save(dbUser);
            session.setAttribute("user", dbUser);
            return Map.of("success", true, "msg", "资料已更新");
        }
        return Map.of("success", false, "msg", "用户不存在");
    }
}