package com.example.demo.controller;

import com.example.demo.model.Item;
import com.example.demo.model.User;
import com.example.demo.repository.ItemRepository;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class TradeController {

    @Autowired
    private ItemRepository itemRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    // 初始化默认商品
    @PostConstruct
    public void init() {
        // 创建上传目录
        new File(uploadDir).mkdirs();

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
            item.setStatus(0);
            itemRepository.save(item);
        }
    }

    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        // 查询未售出(2)且未下架(4)的商品
        List<Item> activeItems = itemRepository.findByStatusNotAndStatusNot(2, 4);
        model.addAttribute("items", activeItems);
        return "index";
    }

    @GetMapping("/item/{id}")
    public String itemDetail(@PathVariable Long id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        Item item = itemRepository.findById(id).orElse(null);
        if (item == null) return "redirect:/";
        model.addAttribute("item", item);
        return "detail";
    }

    @GetMapping("/publish")
    public String publishPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);
        return "publish";
    }

    // === 发布商品接口 (支持文件上传) ===
    @PostMapping("/api/trade/publish")
    @ResponseBody
    public Map<String, Object> publishItem(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("ip") String ip,
            @RequestParam("category") String category,
            @RequestParam("condition") String condition,
            @RequestParam("accessories") String accessories,
            @RequestParam("price") BigDecimal price,
            @RequestParam("description") String description,
            @RequestParam(value = "status", defaultValue = "0") Integer status,
            HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null) return Map.of("success", false, "msg", "未登录", "needLogin", true);

        try {
            // 1. 保存图片文件
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path path = Paths.get(uploadDir + "/" + fileName);
            Files.write(path, file.getBytes());
            String fileUrl = "/uploads/" + fileName; // 访问路径

            // 2. 保存商品到数据库
            Item item = new Item();
            item.setTitle(title);
            item.setIp(ip);
            item.setCategory(category);
            item.setCondition(condition);
            item.setAccessories(accessories);
            item.setPrice(price);
            item.setDescription(description);
            item.setStatus(status);

            item.setSeller(user.getNickname());
            item.setCoverUrl(fileUrl); // 使用上传后的路径
            item.setVerified(false);

            itemRepository.save(item);

            return Map.of("success", true, "msg", "发布成功！");
        } catch (IOException e) {
            e.printStackTrace();
            return Map.of("success", false, "msg", "图片上传失败：" + e.getMessage());
        }
    }

    // buyItem 略，逻辑改为 itemRepository.findById(id) 并 save()
}