package com.example.backend.controller;

import com.example.backend.model.*;
import com.example.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/feedbacks")
@CrossOrigin(origins = "*")
public class FeedbackController {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private SpecialistAvailabilityRepository availabilityRepository;

    @PostMapping
    public ResponseEntity<String> createFeedback(@RequestBody Feedback feedback) {
        if (feedbackRepository.existsByOrderID(feedback.getOrderID())) {
            return ResponseEntity.badRequest().body("Feedback already submitted for this order");
        }
        feedbackRepository.save(feedback);
        return ResponseEntity.ok("Feedback submitted successfully");
    }

    @GetMapping("/specialist/{specialistID}")
    public ResponseEntity<List<Map<String, Object>>> getSpecialistFeedback(@PathVariable Integer specialistID) {
        List<Feedback> feedbacks = feedbackRepository.findBySpecialistID(specialistID);
        return ResponseEntity.ok(feedbacks.stream().map(f -> {
            Map<String, Object> result = new HashMap<>();
            result.put("feedbackID", f.getFeedbackID());
            result.put("rating", f.getRating());
            result.put("comment", f.getComment() != null ? f.getComment() : "");
            result.put("createdAt", f.getCreatedAt().toString().substring(0, 16));

            Optional<Order> order = orderRepository.findByOrderID(f.getOrderID());
            if (order.isPresent()) {
                result.put("orderID", order.get().getOrderID());

                Optional<Appointment> appt = appointmentRepository.findById(order.get().getAppointmentID());
                if (appt.isPresent()) {
                    result.put("date", appt.get().getDate());
                    result.put("timeSlot", appt.get().getTimeSlot());

                    Optional<Customer> customer = customerRepository.findById(appt.get().getCustomerID());
                    result.put("customerName", customer.map(Customer::getCustomerName).orElse("Unknown"));

                    Optional<SpecialistAvailability> avail = availabilityRepository
                        .findBySpecialistIDAndAvailableDateAndSpecialistTimeslot(
                            specialistID, appt.get().getDate(), appt.get().getTimeSlot());
                    result.put("roomNumber", avail.isPresent() ? "Room " + (avail.get().getAvailabilityID() % 10 + 100) : "TBD");
                }
            }
            return result;
        }).toList());
    }
}