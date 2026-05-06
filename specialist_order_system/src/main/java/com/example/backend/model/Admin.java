package com.example.backend.model;

import jakarta.persistence.*;

/**
 * Admin entity representing a system administrator.
 * Administrators have full access to manage specialists, customers, orders, and time slots.
 */
@Entity
@Table(name = "admins")
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private Integer adminID;           // Primary key, auto-generated

    @Column(name = "admin_number")
    private String adminNumber;        // Unique login identifier for the admin

    @Column(name = "admin_password")
    private String adminPassword;      // Password for authentication

    public Admin() {
        // Default constructor required by JPA
    }

    // ==================== Getters and Setters ====================

    public Integer getAdminID() {
        return adminID;
    }

    public void setAdminID(Integer adminID) {
        this.adminID = adminID;
    }

    public String getAdminNumber() {
        return adminNumber;
    }

    public void setAdminNumber(String adminNumber) {
        this.adminNumber = adminNumber;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }
}