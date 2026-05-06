package com.example.backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Appointment entity representing a scheduled consultation booking.
 * Links customers with specialists for specific date and time slots.
 */
@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer appointmentID;  // Primary key, auto-generated

    private LocalDate date;        // Date of the appointment
    private String timeSlot;       // Time slot (e.g., "09:00 - 10:00")
    private Integer customerID;    // Foreign key to the customer
    private Integer specialistID;  // Foreign key to the specialist

    public Appointment() {
        // Default constructor required by JPA
    }

    // ==================== Getters and Setters ====================

    public Integer getAppointmentID() {
        return appointmentID;
    }

    public void setAppointmentID(Integer appointmentID) {
        this.appointmentID = appointmentID;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
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
}