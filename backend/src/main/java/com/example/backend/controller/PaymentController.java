package com.example.backend.controller;

import com.example.backend.model.Order;
import com.example.backend.model.Payment;
import com.example.backend.repository.OrderRepository;
import com.example.backend.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired private PaymentRepository paymentRepository;
    @Autowired private OrderRepository orderRepository;

    @PostMapping("/pay")
    @Transactional // 确保订单状态和支付流水同时更新
    public ResponseEntity<String> processPayment(@RequestBody Payment request) {
        
        Optional<Order> orderOpt = orderRepository.findById(request.getOrderID());
        if (orderOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Error: Order not found.");
        }

        Order order = orderOpt.get();
        if (order.getOrderStatus() == 1) {
            return ResponseEntity.badRequest().body("Error: This order is already paid.");
        }

        // 1. 更新订单状态为 1 (Paid)
        order.setOrderStatus(1);
        orderRepository.save(order);

        // 2. 生成支付流水存入 Payment 表
        request.setPaymentStatus(1); // 1 = 支付成功
        request.setOrderorderID(request.getOrderID()); // 遵守 ERD 的冗余字段要求
        paymentRepository.save(request);

        return ResponseEntity.ok("Payment processed successfully!");
    }
}
