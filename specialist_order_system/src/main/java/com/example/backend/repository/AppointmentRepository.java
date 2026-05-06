package com.example.backend.repository;

import com.example.backend.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository for Appointment entity operations.
 * Provides methods for querying appointments by customer or specialist.
 */
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

    /**
     * Finds all appointments for a specific customer.
     *
     * @param customerID Customer ID
     * @return List of appointments for the customer
     */
    List<Appointment> findByCustomerID(Integer customerID);

    /**
     * Finds all appointments for a specific specialist.
     *
     * @param specialistID Specialist ID
     * @return List of appointments for the specialist
     */
    List<Appointment> findBySpecialistID(Integer specialistID);
}