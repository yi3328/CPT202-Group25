package com.example.backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * SpecialistAvailability entity representing a time slot offered by a specialist.
 * Tracks whether each time slot is available for booking.
 *
 * isBooked Status Codes:
 *   0 = Available (can be booked by customers)
 *   1 = Booked (already reserved by a customer)
 *   2 = Unavailable (marked unavailable by specialist)
 */
@Entity
@Table(name = "specialist_availability")
public class SpecialistAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer availabilityID;    // Primary key, auto-generated

    private Integer specialistID;      // Foreign key to the specialist
    private LocalDate availableDate;  // Date of the available time slot
    private String specialistTimeslot; // Time slot (e.g., "09:00 - 10:00")
    private Integer isBooked;         // Booking status: 0=Available, 1=Booked, 2=Unavailable

    // ==================== Getters and Setters ====================

    public Integer getAvailabilityID() {
        return availabilityID;
    }

    public void setAvailabilityID(Integer availabilityID) {
        this.availabilityID = availabilityID;
    }

    public Integer getSpecialistID() {
        return specialistID;
    }

    public void setSpecialistID(Integer specialistID) {
        this.specialistID = specialistID;
    }

    public LocalDate getAvailableDate() {
        return availableDate;
    }

    public void setAvailableDate(LocalDate availableDate) {
        this.availableDate = availableDate;
    }

    public String getSpecialistTimeslot() {
        return specialistTimeslot;
    }

    public void setSpecialistTimeslot(String specialistTimeslot) {
        this.specialistTimeslot = specialistTimeslot;
    }

    public Integer getIsBooked() {
        return isBooked;
    }

    public void setIsBooked(Integer isBooked) {
        this.isBooked = isBooked;
    }
}