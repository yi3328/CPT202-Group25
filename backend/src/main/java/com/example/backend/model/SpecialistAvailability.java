package com.example.backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "specialist_availability")
public class SpecialistAvailability {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer availabilityID; 

    private Integer specialistID; 
    private LocalDate availableDate; 
    private String specialistTimeslot; 
    private Integer isBooked; 

    // --- 完整补齐所有的 Getter 和 Setter ---
    public Integer getAvailabilityID() { return availabilityID; }
    public void setAvailabilityID(Integer availabilityID) { this.availabilityID = availabilityID; }
    
    public Integer getSpecialistID() { return specialistID; }
    public void setSpecialistID(Integer specialistID) { this.specialistID = specialistID; }
    
    public LocalDate getAvailableDate() { return availableDate; }
    public void setAvailableDate(LocalDate availableDate) { this.availableDate = availableDate; }
    
    public String getSpecialistTimeslot() { return specialistTimeslot; }
    public void setSpecialistTimeslot(String specialistTimeslot) { this.specialistTimeslot = specialistTimeslot; }
    
    public Integer getIsBooked() { return isBooked; }
    public void setIsBooked(Integer isBooked) { this.isBooked = isBooked; }
}