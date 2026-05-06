package com.example.backend.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Payment entity representing a payment transaction for an order.
 * Tracks payment amount, method, and status.
 *
 * Payment Status Codes:
 *   1 = Success
 *
 * Payment Method Codes:
 *   1 = Credit Card, 2 = PayPal (examples)
 */
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer paymentID;         // Primary key, auto-generated

    @Column(unique = true)
    private Integer orderID;           // Foreign key to the associated order (unique identifier from design)

    private BigDecimal paymentAmount;  // Payment amount (decimal format)

    private Integer paymentMethod;     // Payment method: 1 = Credit Card, 2 = PayPal
    private Integer paymentStatus;     // Payment status: 1 = Success

    private Integer OrderorderID;       // Order reference (follows ERD naming convention)

    // ==================== Getters and Setters ====================

    public Integer getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(Integer paymentID) {
        this.paymentID = paymentID;
    }

    public Integer getOrderID() {
        return orderID;
    }

    public void setOrderID(Integer orderID) {
        this.orderID = orderID;
    }

    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public Integer getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(Integer paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Integer getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(Integer paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Integer getOrderorderID() {
        return OrderorderID;
    }

    public void setOrderorderID(Integer OrderorderID) {
        this.OrderorderID = OrderorderID;
    }
}