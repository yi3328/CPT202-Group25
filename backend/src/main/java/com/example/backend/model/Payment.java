package com.example.backend.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer paymentID;

    @Column(unique = true)
    private Integer orderID; // 对应图纸带 U 的唯一标识

    private BigDecimal paymentAmount; // 对应 decimal(19, 0)
    
    private Integer paymentMethod; // 例如：1 = Credit Card, 2 = PayPal
    private Integer paymentStatus; // 1 = Success
    
    private Integer OrderorderID; // 严格遵守 ERD 的拼写

    // --- Getters & Setters ---
    public Integer getPaymentID() { return paymentID; }
    public void setPaymentID(Integer paymentID) { this.paymentID = paymentID; }
    public Integer getOrderID() { return orderID; }
    public void setOrderID(Integer orderID) { this.orderID = orderID; }
    public BigDecimal getPaymentAmount() { return paymentAmount; }
    public void setPaymentAmount(BigDecimal paymentAmount) { this.paymentAmount = paymentAmount; }
    public Integer getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(Integer paymentMethod) { this.paymentMethod = paymentMethod; }
    public Integer getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(Integer paymentStatus) { this.paymentStatus = paymentStatus; }
    public Integer getOrderorderID() { return OrderorderID; }
    public void setOrderorderID(Integer orderorderID) { OrderorderID = orderorderID; }
}