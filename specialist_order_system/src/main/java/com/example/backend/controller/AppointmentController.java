package com.example.backend.controller;

import com.example.backend.model.Appointment;
import com.example.backend.model.Order;
import com.example.backend.model.Specialist;
import com.example.backend.model.SpecialistAvailability;
import com.example.backend.repository.AppointmentRepository;
import com.example.backend.repository.OrderRepository;
import com.example.backend.repository.SpecialistAvailabilityRepository;
import com.example.backend.repository.SpecialistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * AppointmentController handles appointment booking and cancellation.
 *
 * Base path: /api/appointments
 */
@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "*")
public class AppointmentController {

    @Autowired private AppointmentRepository appointmentRepository;
    @Autowired private SpecialistAvailabilityRepository availabilityRepository;
    @Autowired private SpecialistRepository specialistRepository;
    @Autowired private OrderRepository orderRepository;

    /**
     * Creates a new appointment and associated order.
     * Validates that the customer doesn't have conflicting appointments.
     *
     * @param request Appointment details including customerID, specialistID, date, timeSlot
     * @return Success message or error if validation fails
     */
    @PostMapping("/book")
    @Transactional
    public ResponseEntity<String> createAppointment(@RequestBody Appointment request) {
        try {
            // Check if customer already has an active appointment at the same time slot
            var sameTimeOrders = orderRepository.findActiveOrdersAtSameTime(
                request.getCustomerID(), request.getDate(), request.getTimeSlot());
            if (!sameTimeOrders.isEmpty()) {
                return ResponseEntity.badRequest().body("Error: You already have an appointment at this time slot.");
            }

            // Check if customer already has an active appointment with the same specialist on the same day
            var sameDayOrders = orderRepository.findActiveOrdersSameDay(
                request.getCustomerID(), request.getSpecialistID(), request.getDate());
            if (!sameDayOrders.isEmpty()) {
                return ResponseEntity.badRequest().body("Error: You already have an appointment with this specialist on this date.");
            }

            // Verify the time slot is available
            var availability = availabilityRepository.findBySpecialistIDAndAvailableDateAndSpecialistTimeslot(
                request.getSpecialistID(), request.getDate(), request.getTimeSlot());

            if (availability.isEmpty() || availability.get().getIsBooked() != 0) {
                return ResponseEntity.badRequest().body("Error: This slot is no longer available.");
            }

            // Mark the slot as booked
            SpecialistAvailability slot = availability.get();
            slot.setIsBooked(1);
            availabilityRepository.save(slot);

            // Create the appointment
            Appointment savedAppt = appointmentRepository.save(request);

            // Create the order with status=1 (Unpaid)
            Order newOrder = new Order();
            newOrder.setCustomerID(request.getCustomerID());
            newOrder.setSpecialistID(request.getSpecialistID());
            newOrder.setAppointmentID(savedAppt.getAppointmentID());
            newOrder.setOrderStatus(1); // 1 = Unpaid
            orderRepository.save(newOrder);

            return ResponseEntity.ok("Success: Appointment confirmed!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    /**
     * Retrieves all appointments for a specific customer with order and room details.
     *
     * @param customerID The customer's ID
     * @return List of appointments with associated order status and room number
     */
    @GetMapping("/customer/{customerID}")
    public ResponseEntity<List<Map<String, Object>>> getCustomerBookings(@PathVariable Integer customerID) {
        List<Appointment> appointments = appointmentRepository.findByCustomerID(customerID);
        List<Map<String, Object>> responseList = new ArrayList<>();

        for (Appointment appt : appointments) {
            Map<String, Object> bookingData = new HashMap<>();
            bookingData.put("appointmentID", appt.getAppointmentID());
            bookingData.put("date", appt.getDate());
            bookingData.put("timeSlot", appt.getTimeSlot());

            // Enrich with specialist name and room number
            Optional<Specialist> specialist = specialistRepository.findById(appt.getSpecialistID());
            bookingData.put("specialistName", specialist.map(Specialist::getSpecialistName).orElse("Specialist"));
            bookingData.put("roomNumber", specialist.map(Specialist::getRoomNumber).orElse("TBD"));

            // Get associated order details
            Optional<Order> order = orderRepository.findByAppointmentID(appt.getAppointmentID());

            if (order.isPresent()) {
                bookingData.put("orderID", order.get().getOrderID());
                bookingData.put("orderStatus", order.get().getOrderStatus());
                bookingData.put("createdAt", order.get().getCreatedAt());
            } else {
                // Fallback: if order not found, default to Unpaid status
                bookingData.put("orderStatus", 1);
                bookingData.put("orderID", 9999);
            }

            responseList.add(bookingData);
        }
        return ResponseEntity.ok(responseList);
    }

    /**
     * Cancels an appointment and releases the time slot.
     * Sets the associated order status to Cancelled (5).
     *
     * @param appointmentID The appointment ID to cancel
     * @return Success message or error if appointment not found
     */
    @DeleteMapping("/cancel/{appointmentID}")
    @Transactional
    public ResponseEntity<String> cancelAppointment(@PathVariable Integer appointmentID) {
        var appointmentOpt = appointmentRepository.findById(appointmentID);
        if (appointmentOpt.isEmpty()) return ResponseEntity.badRequest().body("Error: Appointment not found.");

        Appointment appointment = appointmentOpt.get();

        // Release the time slot back to available
        var availability = availabilityRepository.findBySpecialistIDAndAvailableDateAndSpecialistTimeslot(
            appointment.getSpecialistID(), appointment.getDate(), appointment.getTimeSlot());

        if (availability.isPresent()) {
            SpecialistAvailability slot = availability.get();
            slot.setIsBooked(0); // 0 = Available
            availabilityRepository.save(slot);
        }

        // Cancel the associated order
        Optional<Order> order = orderRepository.findByAppointmentID(appointmentID);
        if(order.isPresent()){
            Order o = order.get();
            o.setOrderStatus(5); // 5 = Cancelled
            orderRepository.save(o);
        }

        // Note: Appointment record is NOT deleted to preserve booking history
        return ResponseEntity.ok("Success: Appointment cancelled.");
    }
}