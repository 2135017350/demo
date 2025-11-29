package com.example.demo.controller;

import com.example.demo.model.Item;
import com.example.demo.model.User;
import com.example.demo.repository.ItemRepository;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class TradeController {

    @Autowired
    private ItemRepository itemRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    /**
     * 首页 - 支持筛选和搜索
     * @param category 分类 (可选)
     * @param keyword 搜索关键词 (可选)
     */
    @GetMapping("/")
    public String index(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            Model model,
            HttpSession session) {

        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);

        // 处理空字符串参数（前端传空串时转为null）
        if (category != null && category.isEmpty()) category = null;
        if (keyword != null && keyword.isEmpty()) keyword = null;

        List<Item> items;
        // 如果有筛选条件，使用高级查询
        if (category != null || keyword != null) {
            items = itemRepository.searchItems(category, keyword);
        } else {
            // 否则显示所有在售/预售商品
            items = itemRepository.findByStatusNotAndStatusNot(2, 4);
        }

        model.addAttribute("items", items);

        // 回显当前的筛选状态，用于前端高亮按钮
        model.addAttribute("currentCategory", category);
        model.addAttribute("currentKeyword", keyword);

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
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path path = Paths.get(uploadDir + "/" + fileName);
            Files.write(path, file.getBytes());
            String fileUrl = "/uploads/" + fileName;

            Item item = new Item();
            item.setTitle(title);
            item.setIp(ip);
            item.setCategory(category);

            // 修复点：使用 setItemCondition
            item.setItemCondition(condition);

            item.setAccessories(accessories);
            item.setPrice(price);
            item.setDescription(description);
            item.setStatus(status);
            item.setSeller(user.getNickname());
            item.setCoverUrl(fileUrl);
            item.setVerified(false);

            itemRepository.save(item);

            return Map.of("success", true, "msg", "发布成功！");
        } catch (IOException e) {
            e.printStackTrace();
            return Map.of("success", false, "msg", "图片上传失败：" + e.getMessage());
        }
    }

    @PostMapping("/api/trade/buy")
    @ResponseBody
    public Map<String, Object> buyItem(@RequestBody Map<String, Long> payload, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Map.of("success", false, "msg", "请先登录后再购买！", "needLogin", true);
        }

        Long itemId = payload.get("itemId");
        Item targetItem = itemRepository.findById(itemId).orElse(null);

        if (targetItem == null) return Map.of("success", false, "msg", "商品不存在");
        if (targetItem.getStatus() != 0 && targetItem.getStatus() != 3) {
            return Map.of("success", false, "msg", "商品当前不可购买");
        }

        targetItem.setStatus(1);
        itemRepository.save(targetItem);

        return Map.of("success", true, "msg", "交易请求已发送，买家：" + user.getNickname());
    }
}