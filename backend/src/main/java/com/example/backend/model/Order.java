package com.example.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "orders") // 在SQL中order是关键字，所以表名加了s
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderID;

    private Integer customerID;
    private Integer specialistID;
    
    // 0 = Unpaid (待支付), 1 = Paid (已支付), 2 = Cancelled (已取消)
    private Integer orderStatus; 

    // --- Getters & Setters ---
    public Integer getOrderID() { return orderID; }
    public void setOrderID(Integer orderID) { this.orderID = orderID; }
    public Integer getCustomerID() { return customerID; }
    public void setCustomerID(Integer customerID) { this.customerID = customerID; }
    public Integer getSpecialistID() { return specialistID; }
    public void setSpecialistID(Integer specialistID) { this.specialistID = specialistID; }
    public Integer getOrderStatus() { return orderStatus; }
    public void setOrderStatus(Integer orderStatus) { this.orderStatus = orderStatus; }
}