package com.example.demo.repository;

import com.example.demo.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    // 基础查询：未售出且未下架
    List<Item> findByStatusNotAndStatusNot(Integer s1, Integer s2);

    // === 新增：高级筛选查询 ===
    // 逻辑：
    // 1. 筛选状态必须是 在售(0) 或 预售(3)
    // 2. 如果 category 不为空，匹配分类
    // 3. 如果 keyword 不为空，匹配标题或IP
    @Query("SELECT i FROM Item i WHERE " +
            "(i.status = 0 OR i.status = 3) AND " +
            "(:category IS NULL OR i.category = :category) AND " +
            "(:keyword IS NULL OR i.title LIKE %:keyword% OR i.ip LIKE %:keyword%)")
    List<Item> searchItems(@Param("category") String category, @Param("keyword") String keyword);
}