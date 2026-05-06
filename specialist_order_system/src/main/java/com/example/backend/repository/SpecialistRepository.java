package com.example.backend.repository;

import com.example.backend.model.Specialist;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository for Specialist entity operations.
 * Provides methods for querying specialists by their credentials.
 */
public interface SpecialistRepository extends JpaRepository<Specialist, Integer> {

    /**
     * Finds a specialist by number and password for authentication.
     *
     * @param specialistNumber Specialist's unique number
     * @param specialistPassword Specialist's password
     * @return Optional containing the specialist if credentials match
     */
    Optional<Specialist> findBySpecialistNumberAndSpecialistPassword(String specialistNumber, String specialistPassword);

    /**
     * Finds a specialist by their unique number.
     *
     * @param specialistNumber Specialist's unique identifier
     * @return Optional containing the specialist if found
     */
    Optional<Specialist> findBySpecialistNumber(String specialistNumber);
}