package com.example.demo.controller;

import com.example.demo.model.Item;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class TradeController {

    // 模拟数据库
    private static List<Item> itemList = new ArrayList<>();

    static {
        // 初始化一些模拟数据
        itemList.add(new Item(1L, "初音未来 2024 魔法未来 手办", "手办", new BigDecimal("998.00"), "MikuFan01", "https://placeholder.co/300x300?text=Miku+Figure", 0, "全新未拆，盒况完美"));
        itemList.add(new Item(2L, "进击的巨人 利威尔 痛包套装", "周边套装", new BigDecimal("250.00"), "SurveyCorps", "https://placeholder.co/300x300?text=Levi+Bag", 0, "包含吧唧、挂件，仅背过一次"));
        itemList.add(new Item(3L, "间谍过家家 阿尼亚 吧唧 (闪光版)", "吧唧", new BigDecimal("35.00"), "PeanutLover", "https://placeholder.co/300x300?text=Anya+Badge", 0, "复数回血，无伤"));
        itemList.add(new Item(4L, "原神 钟离 官方立牌", "立牌", new BigDecimal("88.00"), "GeoDaddy", "https://placeholder.co/300x300?text=Zhongli+Stand", 1, "交易锁定中"));
    }

    @GetMapping("/")
    public String index(Model model) {
        List<Item> activeItems = itemList.stream()
                .filter(i -> i.getStatus() != 2)
                .collect(Collectors.toList());
        model.addAttribute("items", activeItems);
        return "index";
    }

    @PostMapping("/api/trade/buy")
    @ResponseBody
    public Map<String, Object> buyItem(@RequestBody Map<String, Long> payload) {
        Long itemId = payload.get("itemId");
        Item targetItem = itemList.stream().filter(i -> i.getId().equals(itemId)).findFirst().orElse(null);

        if (targetItem == null) return Map.of("success", false, "msg", "商品不存在");
        if (targetItem.getStatus() != 0) return Map.of("success", false, "msg", "商品不可交易");

        targetItem.setStatus(1); // 标记为交易中
        return Map.of("success", true, "msg", "交易请求已发送，请联系卖家：" + targetItem.getSeller());
    }
}