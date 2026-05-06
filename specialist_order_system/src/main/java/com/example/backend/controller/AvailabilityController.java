package com.example.backend.controller;

import com.example.backend.model.Specialist;
import com.example.backend.model.SpecialistAvailability;
import com.example.backend.repository.SpecialistAvailabilityRepository;
import com.example.backend.repository.SpecialistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * AvailabilityController handles retrieval of specialist availability data.
 * Provides endpoints for querying time slots for the booking interface.
 *
 * Base path: /api/availabilities
 */
@RestController
@RequestMapping("/api/availabilities")
@CrossOrigin(origins = "*")
public class AvailabilityController {

    @Autowired
    private SpecialistAvailabilityRepository availabilityRepository;

    @Autowired
    private SpecialistRepository specialistRepository;

    /**
     * Retrieves all availability time slots from today onwards.
     * Includes specialist name and expertise for each slot.
     *
     * @return List of availability records with specialist details
     */
    @GetMapping("/all")
    public ResponseEntity<List<Map<String, Object>>> getAllAvailabilities() {
        List<SpecialistAvailability> availabilities = availabilityRepository.findAll();
        List<Map<String, Object>> responseList = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (SpecialistAvailability avail : availabilities) {
            // Only include slots from today onwards
            if (avail.getAvailableDate().isBefore(today)) {
                continue;
            }
            Map<String, Object> data = new HashMap<>();
            data.put("availabilityID", avail.getAvailabilityID());
            data.put("specialistID", avail.getSpecialistID());
            data.put("date", avail.getAvailableDate());
            data.put("slot", avail.getSpecialistTimeslot());
            data.put("isBooked", avail.getIsBooked()); // Booking status: 0=Available, 1=Booked, 2=Unavailable

            // Include specialist name and expertise
            Optional<Specialist> specialist = specialistRepository.findById(avail.getSpecialistID());
            if (specialist.isPresent()) {
                data.put("name", specialist.get().getSpecialistName());
                data.put("specialistExpertise", specialist.get().getSpecialistExpertise());
            } else {
                data.put("name", "Specialist " + avail.getSpecialistID());
                data.put("specialistExpertise", "General");
            }

            responseList.add(data);
        }

        return ResponseEntity.ok(responseList);
    }

    /**
     * Retrieves all unique expertise areas from specialists in the system.
     * Used for filtering time slots by specialty in the booking interface.
     *
     * @return Sorted list of distinct expertise strings
     */
    @GetMapping("/expertises")
    public ResponseEntity<List<String>> getAllExpertises() {
        List<Specialist> specialists = specialistRepository.findAll();
        List<String> expertises = specialists.stream()
            .map(Specialist::getSpecialistExpertise)
            .filter(e -> e != null && !e.isEmpty())
            .distinct()
            .sorted()
            .toList();
        return ResponseEntity.ok(expertises);
    }

    /**
     * Retrieves all unique time slots from availability records.
     * Used for filtering time slots by time in the booking interface.
     * Sorted by start time.
     *
     * @return Sorted list of distinct time slot strings (format: "HH:MM - HH:MM")
     */
    @GetMapping("/timeslots")
    public ResponseEntity<List<String>> getAllTimeslots() {
        List<SpecialistAvailability> availabilities = availabilityRepository.findAll();
        List<String> timeslots = availabilities.stream()
            .map(SpecialistAvailability::getSpecialistTimeslot)
            .filter(t -> t != null && !t.isEmpty())
            .distinct()
            .sorted((a, b) -> {
                // Sort by start time
                String timeA = a.split(" - ")[0];
                String timeB = b.split(" - ")[0];
                return timeA.compareTo(timeB);
            })
            .toList();
        return ResponseEntity.ok(timeslots);
    }
}