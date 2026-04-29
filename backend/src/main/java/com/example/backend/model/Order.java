package com.example.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderID;

    private Integer customerID;
    private Integer specialistID;
    private Integer appointmentID;

    // 1 = Unpaid (未付款), 2 = Paid/Unconfirmed (已付款待确认), 3 = Confirmed (已确认), 4 = Overdue (逾期)
    private Integer orderStatus;

    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
    private LocalDateTime updatedAt;

    public Order() {
        this.createdAt = LocalDateTime.now();
        this.orderStatus = 1; // 默认未付款
    }

    // --- Getters & Setters ---
    public Integer getOrderID() { return orderID; }
    public void setOrderID(Integer orderID) { this.orderID = orderID; }
    public Integer getCustomerID() { return customerID; }
    public void setCustomerID(Integer customerID) { this.customerID = customerID; }
    public Integer getSpecialistID() { return specialistID; }
    public void setSpecialistID(Integer specialistID) { this.specialistID = specialistID; }
    public Integer getAppointmentID() { return appointmentID; }
    public void setAppointmentID(Integer appointmentID) { this.appointmentID = appointmentID; }
    public Integer getOrderStatus() { return orderStatus; }
    public void setOrderStatus(Integer orderStatus) { this.orderStatus = orderStatus; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}