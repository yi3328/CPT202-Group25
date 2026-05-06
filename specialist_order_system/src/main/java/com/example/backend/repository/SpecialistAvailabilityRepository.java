package com.example.backend.repository;

import com.example.backend.model.SpecialistAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

/**
 * Repository for SpecialistAvailability entity operations.
 * Provides methods for querying specialist time slots.
 */
public interface SpecialistAvailabilityRepository extends JpaRepository<SpecialistAvailability, Integer> {

    /**
     * Finds a specific time slot by specialist, date, and time.
     *
     * @param specialistID    Specialist ID
     * @param availableDate   Date of the slot
     * @param specialistTimeslot Time slot string (e.g., "09:00 - 10:00")
     * @return Optional containing the availability record if found
     */
    Optional<SpecialistAvailability> findBySpecialistIDAndAvailableDateAndSpecialistTimeslot(
        Integer specialistID, java.time.LocalDate availableDate, String specialistTimeslot);

    /**
     * Finds all time slots for a specific specialist.
     *
     * @param specialistID Specialist ID
     * @return List of availability records for the specialist
     */
    List<SpecialistAvailability> findBySpecialistID(Integer specialistID);
}