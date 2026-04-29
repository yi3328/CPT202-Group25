package com.example.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "specialists")
public class Specialist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer specialistID; // 对应 integer(10)

    private String specialistNumber;
    private String specialistName;
    private String specialistPassword;
    private String specialistExpertise;

    // Getters and Setters...
    public String getSpecialistNumber() { return specialistNumber; }
    public void setSpecialistNumber(String specialistNumber) { this.specialistNumber = specialistNumber; }
    public String getSpecialistPassword() { return specialistPassword; }
    public void setSpecialistPassword(String specialistPassword) { this.specialistPassword = specialistPassword; }
    public Integer getSpecialistID() { return specialistID; }
    public void setSpecialistID(Integer specialistID) { this.specialistID = specialistID; }
    public String getSpecialistName() { return specialistName; }
    public void setSpecialistName(String specialistName) { this.specialistName = specialistName; }
    public String getSpecialistExpertise() { return specialistExpertise; }
    public void setSpecialistExpertise(String specialistExpertise) { this.specialistExpertise = specialistExpertise; }
}