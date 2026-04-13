package com.example.backend.repository;
import com.example.backend.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    
    // 核心大招：根据客户和专家寻找最新的一笔订单（通过订单ID倒序排列，取第一条）
    Optional<Order> findFirstByCustomerIDAndSpecialistIDOrderByOrderIDDesc(Integer customerID, Integer specialistID);
}