package com.example.backend.model;

import jakarta.persistence.*;

/**
 * Customer entity representing a user who books consultation appointments.
 * Customers can browse specialists, create bookings, and manage their appointments.
 */
@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer customerID;

    @Column(length = 255, unique = true, nullable = false)
    private String customerNumber;         // Unique login identifier for the customer

    @Column(length = 255)
    private String customerName;           // Full name of the customer

    @Column(length = 255, nullable = false)
    private String customerPassword;       // Password for authentication

    @Column(columnDefinition = "int default 1")
    private Integer isActive = 1;           // Account status: 1 = active, 0 = disabled

    public Customer() {
        // Default constructor required by JPA
    }

    // ==================== Getters and Setters ====================

    public Integer getCustomerID() {
        return customerID;
    }

    public void setCustomerID(Integer customerID) {
        this.customerID = customerID;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPassword() {
        return customerPassword;
    }

    public void setCustomerPassword(String customerPassword) {
        this.customerPassword = customerPassword;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }
}