package com.example.backend.model;

import jakarta.persistence.*;

/**
 * Specialist entity representing a consultation expert in the system.
 * Each specialist has unique credentials, expertise area, and room assignment.
 */
@Entity
@Table(name = "specialists")
public class Specialist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer specialistID;

    private String specialistNumber;      // Unique login identifier for the specialist
    private String specialistName;         // Full name of the specialist
    private String specialistPassword;    // Password for authentication
    private String specialistExpertise;    // Area of expertise (e.g., IT, Legal, Finance)

    @Column(columnDefinition = "int default 1")
    private Integer isActive = 1;          // Account status: 1 = active, 0 = disabled

    private String roomNumber;             // Assigned room number for consultations

    // ==================== Getters and Setters ====================

    public Integer getSpecialistID() {
        return specialistID;
    }

    public void setSpecialistID(Integer specialistID) {
        this.specialistID = specialistID;
    }

    public String getSpecialistNumber() {
        return specialistNumber;
    }

    public void setSpecialistNumber(String specialistNumber) {
        this.specialistNumber = specialistNumber;
    }

    public String getSpecialistName() {
        return specialistName;
    }

    public void setSpecialistName(String specialistName) {
        this.specialistName = specialistName;
    }

    public String getSpecialistPassword() {
        return specialistPassword;
    }

    public void setSpecialistPassword(String specialistPassword) {
        this.specialistPassword = specialistPassword;
    }

    public String getSpecialistExpertise() {
        return specialistExpertise;
    }

    public void setSpecialistExpertise(String specialistExpertise) {
        this.specialistExpertise = specialistExpertise;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }
}