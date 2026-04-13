package com.example.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer customerID; // Matches integer(10) in ERD

    @Column(length = 255, unique = true, nullable = false)
    private String customerNumber; // Matches varchar(255) in ERD

    @Column(length = 255)
    private String customerName; // Matches varchar(255) in ERD

    @Column(length = 255, nullable = false)
    private String customerPassword; // Matches varchar(255) in ERD

    public Customer() {}

    // --- Getters and Setters ---
    public Integer getCustomerID() { return customerID; }
    public void setCustomerID(Integer customerID) { this.customerID = customerID; }

    public String getCustomerNumber() { return customerNumber; }
    public void setCustomerNumber(String customerNumber) { this.customerNumber = customerNumber; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPassword() { return customerPassword; }
    public void setCustomerPassword(String customerPassword) { this.customerPassword = customerPassword; }
}