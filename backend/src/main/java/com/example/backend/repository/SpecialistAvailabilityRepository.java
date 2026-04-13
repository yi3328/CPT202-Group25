package com.example.backend.repository;

import com.example.backend.model.SpecialistAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SpecialistAvailabilityRepository extends JpaRepository<SpecialistAvailability, Integer> {
    // Find a specific slot by specialist ID, date, and timeslot
    Optional<SpecialistAvailability> findBySpecialistIDAndAvailableDateAndSpecialistTimeslot(
        Integer specialistID, java.time.LocalDate availableDate, String specialistTimeslot);
}
