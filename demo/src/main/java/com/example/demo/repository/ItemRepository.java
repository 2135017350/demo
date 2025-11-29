package com.example.demo.repository;

import com.example.demo.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    // 查询未售出(status != 2) 且 未删除(status != 4) 的商品
    List<Item> findByStatusNotAndStatusNot(Integer s1, Integer s2);
}