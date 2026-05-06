package com.example.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Order entity representing a customer's booking transaction.
 * Tracks the lifecycle of an appointment from creation through completion.
 *
 * Order Status Codes:
 *   1 = Unpaid (initial state after booking)
 *   2 = Paid/Unconfirmed (payment received, awaiting specialist confirmation)
 *   3 = Confirmed (specialist confirmed within 30 seconds)
 *   4 = Completed (appointment finished successfully)
 *   5 = Cancelled (manually cancelled by customer or auto-cancelled)
 *   6 = Auto-Cancelled (timeout - specialist failed to confirm within 30 seconds)
 */
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderID;

    private Integer customerID;      // Foreign key to the customer who placed the order
    private Integer specialistID;  // Foreign key to the specialist for this appointment
    private Integer appointmentID; // Foreign key to the associated appointment record

    // Order status: 1=Unpaid, 2=Paid/Unconfirmed, 3=Confirmed, 4=Completed, 5=Cancelled, 6=Auto-Cancelled
    private Integer orderStatus;

    private LocalDateTime createdAt;  // Timestamp when order was created (booking time)
    private LocalDateTime paidAt;    // Timestamp when payment was received
    private LocalDateTime updatedAt; // Timestamp when order was last updated

    public Order() {
        this.createdAt = LocalDateTime.now();
        this.orderStatus = 1; // Default to Unpaid status
    }

    // ==================== Getters and Setters ====================

    public Integer getOrderID() {
        return orderID;
    }

    public void setOrderID(Integer orderID) {
        this.orderID = orderID;
    }

    public Integer getCustomerID() {
        return customerID;
    }

    public void setCustomerID(Integer customerID) {
        this.customerID = customerID;
    }

    public Integer getSpecialistID() {
        return specialistID;
    }

    public void setSpecialistID(Integer specialistID) {
        this.specialistID = specialistID;
    }

    public Integer getAppointmentID() {
        return appointmentID;
    }

    public void setAppointmentID(Integer appointmentID) {
        this.appointmentID = appointmentID;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}