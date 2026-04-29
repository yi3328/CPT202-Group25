package com.example.backend.repository;

import com.example.backend.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    
    // Custom method to find all appointments for a specific customer
    List<Appointment> findByCustomerID(Integer customerID);

    // Custom method to find all appointments for a specific specialist
    List<Appointment> findBySpecialistID(Integer specialistID);
}