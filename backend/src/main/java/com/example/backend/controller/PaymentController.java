package com.example.backend.controller;

import com.example.backend.model.Order;
import com.example.backend.model.Payment;
import com.example.backend.repository.OrderRepository;
import com.example.backend.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired private PaymentRepository paymentRepository;
    @Autowired private OrderRepository orderRepository;

    @PostMapping("/pay")
    @Transactional
    public ResponseEntity<String> processPayment(@RequestBody Payment request) {

        Optional<Order> orderOpt = orderRepository.findById(request.getOrderID());
        if (orderOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Error: Order not found.");
        }

        Order order = orderOpt.get();
        // 1 = Unpaid, 2 = Paid/Unconfirmed, 3 = Confirmed, 4 = Overdue
        if (order.getOrderStatus() == 2 || order.getOrderStatus() == 3 || order.getOrderStatus() == 4) {
            return ResponseEntity.badRequest().body("Error: This order is already processed.");
        }

        // 付款后状态改为 2 (Paid/Unconfirmed)
        order.setOrderStatus(2);
        order.setPaidAt(LocalDateTime.now());
        orderRepository.save(order);

        // 生成支付流水
        request.setPaymentStatus(1); // 1 = 支付成功
        request.setOrderorderID(request.getOrderID());
        paymentRepository.save(request);

        return ResponseEntity.ok("Payment processed successfully!");
    }
}