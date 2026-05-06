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

/**
 * PaymentController handles payment processing for orders.
 *
 * Base path: /api/payments
 *
 * Order Status Codes:
 *   1 = Unpaid, 2 = Paid/Unconfirmed, 3 = Confirmed, 4 = Completed, 5 = Cancelled, 6 = Auto-Cancelled
 */
@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired private PaymentRepository paymentRepository;
    @Autowired private OrderRepository orderRepository;

    /**
     * Processes a payment for an order.
     * Updates the order status to Paid/Unconfirmed (2) and creates a payment record.
     * Only orders with status=1 (Unpaid) can be paid.
     *
     * @param request Payment object containing orderID and paymentAmount
     * @return Success message or error if order not found or already processed
     */
    @PostMapping("/pay")
    @Transactional
    public ResponseEntity<String> processPayment(@RequestBody Payment request) {

        Optional<Order> orderOpt = orderRepository.findById(request.getOrderID());
        if (orderOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Error: Order not found.");
        }

        Order order = orderOpt.get();
        // Only unpaid orders can be paid; already paid/confirmed/completed orders are rejected
        if (order.getOrderStatus() == 2 || order.getOrderStatus() == 3 || order.getOrderStatus() == 4) {
            return ResponseEntity.badRequest().body("Error: This order is already processed.");
        }

        // Update order to Paid/Unconfirmed status (2)
        order.setOrderStatus(2);
        order.setPaidAt(LocalDateTime.now());
        orderRepository.save(order);

        // Create payment record
        request.setPaymentStatus(1); // 1 = Payment Successful
        request.setOrderorderID(request.getOrderID());
        paymentRepository.save(request);

        return ResponseEntity.ok("Payment processed successfully!");
    }
}