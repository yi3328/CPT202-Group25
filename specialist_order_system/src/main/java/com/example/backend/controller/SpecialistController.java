package com.example.backend.controller;

import com.example.backend.model.*;
import com.example.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * SpecialistController handles specialist authentication, schedule management, and order processing.
 *
 * Base path: /api/specialists
 */
@RestController
@RequestMapping("/api/specialists")
@CrossOrigin(origins = "*")
public class SpecialistController {

    @Autowired private SpecialistRepository specialistRepository;
    @Autowired private AppointmentRepository appointmentRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private SpecialistAvailabilityRepository availabilityRepository;
    @Autowired private PaymentRepository paymentRepository;

    // ==================== Authentication ====================

    /**
     * Authenticates a specialist user.
     *
     * @param loginData Map containing specialistNumber and specialistPassword
     * @return Specialist ID, number, name, and expertise if successful; error message otherwise
     */
    @PostMapping("/login")
    public ResponseEntity<?> specialistLogin(@RequestBody Map<String, String> loginData) {
        String number = loginData.get("specialistNumber");
        String password = loginData.get("specialistPassword");

        Optional<Specialist> specialistOpt = specialistRepository
            .findBySpecialistNumberAndSpecialistPassword(number, password);

        if (specialistOpt.isPresent()) {
            Specialist s = specialistOpt.get();
            if (s.getIsActive() == 0) {
                return ResponseEntity.badRequest().body(Map.of("message", "Account is disabled"));
            }
            Map<String, Object> result = new HashMap<>();
            result.put("specialistID", s.getSpecialistID());
            result.put("specialistNumber", s.getSpecialistNumber());
            result.put("specialistName", s.getSpecialistName());
            result.put("specialistExpertise", s.getSpecialistExpertise());
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Invalid credentials"));
    }

    // ==================== Registration ====================

    /**
     * Registers a new specialist account.
     *
     * @param registerData Map containing specialistNumber, specialistPassword, specialistName, specialistExpertise
     * @return Success message or error if validation fails (e.g., duplicate number)
     */
    @PostMapping("/register")
    public ResponseEntity<?> specialistRegister(@RequestBody Map<String, String> registerData) {
        String number = registerData.get("specialistNumber");
        String password = registerData.get("specialistPassword");
        String name = registerData.get("specialistName");
        String expertise = registerData.get("specialistExpertise");

        if (number == null || password == null || name == null || expertise == null ||
            number.isEmpty() || password.isEmpty() || name.isEmpty() || expertise.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "All fields are required"));
        }

        // Check if specialist number is already registered
        Optional<Specialist> existing = specialistRepository.findBySpecialistNumber(number);
        if (existing.isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Specialist number already exists"));
        }

        Specialist specialist = new Specialist();
        specialist.setSpecialistNumber(number);
        specialist.setSpecialistPassword(password);
        specialist.setSpecialistName(name);
        specialist.setSpecialistExpertise(expertise);
        specialistRepository.save(specialist);

        return ResponseEntity.ok(Map.of("message", "Registration successful"));
    }

    // ==================== Expertise Management ====================

    /**
     * Retrieves all unique expertise areas from specialists in the system.
     * Used for filtering and search functionality.
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
            .collect(Collectors.toList());
        return ResponseEntity.ok(expertises);
    }

    // ==================== Availability Management ====================

    /**
     * Adds a new availability time slot for a specialist.
     *
     * @param specialistId Specialist ID
     * @param request      Map containing date and timeSlot
     * @return Success message or error if time slot already exists
     */
    @PostMapping("/{specialistId}/availability")
    @Transactional
    public ResponseEntity<String> addAvailability(
            @PathVariable Integer specialistId,
            @RequestBody Map<String, String> request) {

        String dateStr = request.get("date");
        String timeSlot = request.get("timeSlot");

        if (dateStr == null || timeSlot == null || dateStr.isEmpty() || timeSlot.isEmpty()) {
            return ResponseEntity.badRequest().body("Error: Date and time slot are required");
        }

        LocalDate date = LocalDate.parse(dateStr);

        // Check for duplicate time slot
        var existing = availabilityRepository
            .findBySpecialistIDAndAvailableDateAndSpecialistTimeslot(specialistId, date, timeSlot);
        if (existing.isPresent()) {
            return ResponseEntity.badRequest().body("Error: This time slot already exists");
        }

        SpecialistAvailability avail = new SpecialistAvailability();
        avail.setSpecialistID(specialistId);
        avail.setAvailableDate(date);
        avail.setSpecialistTimeslot(timeSlot);
        avail.setIsBooked(0); // 0 = Available
        availabilityRepository.save(avail);

        return ResponseEntity.ok("Success: Time slot added");
    }

    /**
     * Retrieves all availability time slots for a specific specialist.
     *
     * @param specialistId Specialist ID
     * @return List of availability records with ID, date, time slot, and booking status
     */
    @GetMapping("/{specialistId}/my-availabilities")
    public ResponseEntity<List<Map<String, Object>>> getMyAvailabilities(@PathVariable Integer specialistId) {
        List<SpecialistAvailability> availabilities = availabilityRepository.findBySpecialistID(specialistId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (SpecialistAvailability av : availabilities) {
            Map<String, Object> item = new HashMap<>();
            item.put("availabilityID", av.getAvailabilityID());
            item.put("date", av.getAvailableDate());
            item.put("timeSlot", av.getSpecialistTimeslot());
            item.put("isBooked", av.getIsBooked());
            result.add(item);
        }

        return ResponseEntity.ok(result);
    }

    /**
     * Deletes an availability time slot for a specialist.
     * Only the specialist who owns the slot can delete it.
     *
     * @param specialistId   Specialist ID (must match the slot owner)
     * @param availabilityID Availability ID to delete
     * @return Success message or error if not found or unauthorized
     */
    @DeleteMapping("/{specialistId}/availability/{availabilityID}")
    @Transactional
    public ResponseEntity<String> deleteAvailability(
            @PathVariable Integer specialistId,
            @PathVariable Integer availabilityID) {

        var availOpt = availabilityRepository.findById(availabilityID);
        if (availOpt.isEmpty() || !availOpt.get().getSpecialistID().equals(specialistId)) {
            return ResponseEntity.badRequest().body("Error: Time slot not found");
        }

        availabilityRepository.deleteById(availabilityID);
        return ResponseEntity.ok("Success: Time slot deleted");
    }

    // ==================== Schedule Management ====================

    /**
     * Retrieves all appointments for a specific specialist (full schedule).
     *
     * @param specialistId Specialist ID
     * @return List of appointments with customer names and order status
     */
    @GetMapping("/{specialistId}/schedule")
    public ResponseEntity<List<Map<String, Object>>> getSpecialistSchedule(@PathVariable Integer specialistId) {
        List<Appointment> appointments = appointmentRepository.findBySpecialistID(specialistId);
        return ResponseEntity.ok(enrichAppointments(appointments));
    }

    /**
     * Retrieves detailed information for a specific appointment.
     * Includes customer details, order status, and room assignment.
     *
     * @param specialistId  Specialist ID (must match appointment owner)
     * @param appointmentId Appointment ID
     * @return Appointment details or error if not found/unauthorized
     */
    @GetMapping("/{specialistId}/appointments/{appointmentId}")
    public ResponseEntity<?> getAppointmentDetail(@PathVariable Integer specialistId,
                                                   @PathVariable Integer appointmentId) {
        Optional<Appointment> apptOpt = appointmentRepository.findById(appointmentId);
        if (apptOpt.isEmpty() || !apptOpt.get().getSpecialistID().equals(specialistId)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Appointment not found"));
        }

        Appointment appt = apptOpt.get();
        Map<String, Object> detail = new HashMap<>();
        detail.put("appointmentID", appt.getAppointmentID());
        detail.put("date", appt.getDate());
        detail.put("timeSlot", appt.getTimeSlot());

        // Fetch customer information
        Optional<Customer> customer = customerRepository.findById(appt.getCustomerID());
        detail.put("customerName", customer.map(Customer::getCustomerName).orElse("Unknown"));
        detail.put("customerNumber", customer.map(Customer::getCustomerNumber).orElse(""));

        // Fetch order information
        Optional<Order> order = orderRepository.findByAppointmentID(appt.getAppointmentID());
        if (order.isPresent()) {
            detail.put("orderID", order.get().getOrderID());
            detail.put("orderStatus", order.get().getOrderStatus());
        }

        // Fetch availability for room assignment
        Optional<SpecialistAvailability> avail = availabilityRepository
            .findBySpecialistIDAndAvailableDateAndSpecialistTimeslot(
                specialistId, appt.getDate(), appt.getTimeSlot());
        if (avail.isPresent()) {
            detail.put("roomNumber", "Room " + (avail.get().getAvailabilityID() % 10 + 100));
        } else {
            detail.put("roomNumber", "TBD");
        }

        return ResponseEntity.ok(detail);
    }

    // ==================== Order Processing ====================

    /**
     * Retrieves all pending (paid/unconfirmed) orders for a specialist.
     * Orders with status=2 that are within the 30-second confirmation window.
     *
     * @param specialistId Specialist ID
     * @return List of pending orders with customer info and time remaining
     */
    @GetMapping("/{specialistId}/pending-orders")
    public ResponseEntity<List<Map<String, Object>>> getPendingOrders(@PathVariable Integer specialistId) {
        List<Appointment> appointments = appointmentRepository.findBySpecialistID(specialistId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Appointment appt : appointments) {
            Optional<Order> order = orderRepository.findByAppointmentID(appt.getAppointmentID());

            if (order.isPresent() && order.get().getOrderStatus() == 2) {
                Map<String, Object> item = new HashMap<>();
                item.put("appointmentID", appt.getAppointmentID());
                item.put("orderID", order.get().getOrderID());
                item.put("customerID", appt.getCustomerID());
                item.put("date", appt.getDate());
                item.put("timeSlot", appt.getTimeSlot());

                Optional<Customer> customer = customerRepository.findById(appt.getCustomerID());
                item.put("customerName", customer.map(Customer::getCustomerName).orElse("Unknown"));

                // Fetch specialist expertise
                Optional<Specialist> specialist = specialistRepository.findById(specialistId);
                item.put("specialistExpertise", specialist.map(Specialist::getSpecialistExpertise).orElse("General"));

                // Check if within 30-second confirmation window
                item.put("paidAt", order.get().getPaidAt());
                item.put("submitTime", order.get().getPaidAt() != null ?
                    order.get().getPaidAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "--");
                item.put("withinTime", isWithin30Seconds(order.get().getPaidAt()));
                item.put("orderStatus", "Unconfirmed");

                result.add(item);
            }
        }
        return ResponseEntity.ok(result);
    }

    /**
     * Confirms a paid/unconfirmed order.
     * Only orders with status=2 (Paid/Unconfirmed) can be confirmed.
     * Confirmation must occur within 30 seconds of payment.
     *
     * @param orderId Order ID to confirm
     * @return Success message or error if order not found, already processed, or expired
     */
    @PutMapping("/orders/{orderId}/confirm")
    @Transactional
    public ResponseEntity<String> confirmOrder(@PathVariable Integer orderId) {
        Optional<Order> orderOpt = orderRepository.findByOrderID(orderId);
        if (orderOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Error: Order not found");
        }

        Order order = orderOpt.get();
        if (order.getOrderStatus() != 2) {
            return ResponseEntity.badRequest().body("Error: Order cannot be confirmed");
        }

        // Check if within 30-second window
        if (!isWithin30Seconds(order.getPaidAt())) {
            return ResponseEntity.badRequest().body("Error: Order exceeded 30 seconds, auto-cancelled");
        }

        order.setOrderStatus(3); // 3 = Confirmed
        orderRepository.save(order);
        return ResponseEntity.ok("Success: Order confirmed");
    }

    /**
     * Retrieves all confirmed (but not completed) orders for a specialist.
     *
     * @param specialistId Specialist ID
     * @return List of confirmed orders with customer info and time status
     */
    @GetMapping("/{specialistId}/completed-orders")
    public ResponseEntity<List<Map<String, Object>>> getCompletedOrders(@PathVariable Integer specialistId) {
        List<Appointment> appointments = appointmentRepository.findBySpecialistID(specialistId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Appointment appt : appointments) {
            Optional<Order> order = orderRepository.findByAppointmentID(appt.getAppointmentID());

            if (order.isPresent() && order.get().getOrderStatus() == 3) {
                Map<String, Object> item = new HashMap<>();
                item.put("appointmentID", appt.getAppointmentID());
                item.put("orderID", order.get().getOrderID());
                item.put("customerID", appt.getCustomerID());
                item.put("date", appt.getDate());
                item.put("timeSlot", appt.getTimeSlot());

                Optional<Customer> customer = customerRepository.findById(appt.getCustomerID());
                item.put("customerName", customer.map(Customer::getCustomerName).orElse("Unknown"));

                // Check if appointment time has passed
                item.put("isTimeOver", isTimeOver(appt.getDate(), appt.getTimeSlot()));
                item.put("orderStatus", "Confirmed");

                result.add(item);
            }
        }
        return ResponseEntity.ok(result);
    }

    /**
     * Marks a confirmed order as completed.
     * The appointment's time slot must have ended before completion is allowed.
     *
     * @param orderId Order ID to complete
     * @return Success message or error if order not found, wrong status, or time not ended
     */
    @PutMapping("/orders/{orderId}/complete")
    @Transactional
    public ResponseEntity<String> completeOrder(@PathVariable Integer orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Error: Order not found");
        }

        Order order = orderOpt.get();
        if (order.getOrderStatus() != 3) {
            return ResponseEntity.badRequest().body("Error: Order must be confirmed before completing");
        }

        // Get appointment info and verify time slot has ended
        Optional<Appointment> apptOpt = appointmentRepository.findById(order.getAppointmentID());
        if (apptOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Error: Appointment not found");
        }

        Appointment appt = apptOpt.get();
        if (!isTimeSlotEnded(appt.getDate(), appt.getTimeSlot())) {
            return ResponseEntity.badRequest().body("Error: Time slot has not ended yet");
        }

        order.setOrderStatus(4); // 4 = Completed
        orderRepository.save(order);
        return ResponseEntity.ok("Success: Order completed");
    }

    /**
     * Retrieves full details for a specific order including payment information.
     *
     * @param orderId Order ID
     * @return Order details including customer, appointment, and payment info
     */
    @GetMapping("/orders/{orderId}/details")
    public ResponseEntity<?> getOrderDetails(@PathVariable Integer orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Order not found"));
        }

        Order order = orderOpt.get();
        Optional<Appointment> apptOpt = appointmentRepository.findById(order.getAppointmentID());
        if (apptOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Appointment not found"));
        }

        Appointment appt = apptOpt.get();
        Optional<Customer> customerOpt = customerRepository.findById(appt.getCustomerID());

        // Fetch payment information
        Optional<Payment> paymentOpt = paymentRepository.findByOrderID(orderId);

        Map<String, Object> details = new HashMap<>();
        details.put("orderID", order.getOrderID());
        details.put("orderStatus", order.getOrderStatus());
        details.put("statusText", getStatusText(order.getOrderStatus()));
        details.put("createdAt", order.getCreatedAt());
        details.put("paidAt", order.getPaidAt());
        details.put("date", appt.getDate());
        details.put("timeSlot", appt.getTimeSlot());
        details.put("customerName", customerOpt.map(Customer::getCustomerName).orElse("Unknown"));
        details.put("customerNumber", customerOpt.map(Customer::getCustomerNumber).orElse("N/A"));
        details.put("paymentAmount", paymentOpt.map(p -> p.getPaymentAmount().toString()).orElse("$150.00"));
        details.put("paymentStatus", paymentOpt.map(Payment::getPaymentStatus).orElse(0));

        return ResponseEntity.ok(details);
    }

    // ==================== Search and Filter ====================

    /**
     * Searches and filters orders for a specialist by customer name and/or order status.
     *
     * @param specialistId  Specialist ID
     * @param customerName  Optional customer name filter (case-insensitive partial match)
     * @param status        Optional status filter (Unconfirmed, Confirmed, Completed, Cancelled)
     * @return List of matching orders with appointment and customer details
     */
    @GetMapping("/{specialistId}/search")
    public ResponseEntity<List<Map<String, Object>>> searchOrders(
            @PathVariable Integer specialistId,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String status) {

        List<Appointment> appointments = appointmentRepository.findBySpecialistID(specialistId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Appointment appt : appointments) {
            Optional<Order> order = orderRepository.findByAppointmentID(appt.getAppointmentID());

            if (order.isPresent()) {
                Optional<Customer> customer = customerRepository.findById(appt.getCustomerID());
                String custName = customer.map(Customer::getCustomerName).orElse("");
                Integer orderStatus = order.get().getOrderStatus();

                // Filter conditions
                boolean matchName = customerName == null || customerName.isEmpty()
                    || custName.toLowerCase().contains(customerName.toLowerCase());
                boolean matchStatus = status == null || status.equals("all")
                    || statusEquals(orderStatus, status);

                if (matchName && matchStatus) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("appointmentID", appt.getAppointmentID());
                    item.put("orderID", order.get().getOrderID());
                    item.put("customerName", custName);
                    item.put("date", appt.getDate());
                    item.put("timeSlot", appt.getTimeSlot());
                    item.put("orderStatus", getStatusText(orderStatus));
                    result.add(item);
                }
            }
        }
        return ResponseEntity.ok(result);
    }

    // ==================== Helper Methods ====================

    /**
     * Enriches appointment records with customer name and order status text.
     *
     * @param appointments List of appointments to enrich
     * @return List of maps with appointment details plus customer name and status text
     */
    private List<Map<String, Object>> enrichAppointments(List<Appointment> appointments) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Appointment appt : appointments) {
            Map<String, Object> item = new HashMap<>();
            item.put("appointmentID", appt.getAppointmentID());
            item.put("date", appt.getDate());
            item.put("timeSlot", appt.getTimeSlot());

            Optional<Customer> customer = customerRepository.findById(appt.getCustomerID());
            item.put("customerName", customer.map(Customer::getCustomerName).orElse("Unknown"));

            Optional<Order> order = orderRepository.findByAppointmentID(appt.getAppointmentID());
            if (order.isPresent()) {
                item.put("orderStatus", getStatusText(order.get().getOrderStatus()));
            }

            result.add(item);
        }
        return result;
    }

    /**
     * Checks if the current time is within two hours before the appointment.
     *
     * @param date     Appointment date
     * @param timeSlot Time slot string (format: "HH:MM - HH:MM")
     * @return true if within two hours of appointment start time
     */
    private boolean isWithinTwoHours(LocalDate date, String timeSlot) {
        try {
            String[] times = timeSlot.split("-");
            LocalTime startTime = LocalTime.parse(times[0].trim());
            LocalDateTime appointmentTime = LocalDateTime.of(date, startTime);
            return LocalDateTime.now().isBefore(appointmentTime.plusHours(2));
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Checks if an order is still within the 30-second confirmation window after payment.
     *
     * @param paidAt Payment timestamp
     * @return true if within 30 seconds of payment time
     */
    private boolean isWithin30Seconds(LocalDateTime paidAt) {
        if (paidAt == null) return false;
        return LocalDateTime.now().isBefore(paidAt.plusSeconds(30));
    }

    /**
     * Checks if an appointment's scheduled time has already passed.
     *
     * @param date     Appointment date
     * @param timeSlot Time slot string (format: "HH:MM - HH:MM")
     * @return true if current time is after the appointment start time
     */
    private boolean isTimeOver(LocalDate date, String timeSlot) {
        try {
            String[] times = timeSlot.split("-");
            LocalTime startTime = LocalTime.parse(times[0].trim());
            LocalDateTime appointmentTime = LocalDateTime.of(date, startTime);
            return LocalDateTime.now().isAfter(appointmentTime);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks if an appointment's time slot has ended (allowing completion).
     *
     * @param date     Appointment date
     * @param timeSlot Time slot string (format: "HH:MM - HH:MM")
     * @return true if current time is after the slot's end time
     */
    private boolean isTimeSlotEnded(LocalDate date, String timeSlot) {
        try {
            String[] times = timeSlot.split("-");
            LocalTime endTime = LocalTime.parse(times[1].trim());
            LocalDateTime slotEndTime = LocalDateTime.of(date, endTime);
            return LocalDateTime.now().isAfter(slotEndTime);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks if an order status matches a status text string.
     *
     * @param status Order status code
     * @param text   Status text to match
     * @return true if status code matches the text
     */
    private boolean statusEquals(Integer status, String text) {
        return switch (text) {
            case "Unconfirmed" -> status == 2;
            case "Confirmed" -> status == 3;
            case "Completed" -> status == 4;
            case "Cancelled" -> status == 5;
            default -> false;
        };
    }

    /**
     * Converts an order status code to human-readable text.
     *
     * Order Status Codes:
     *   1 = Unpaid, 2 = Unconfirmed, 3 = Confirmed, 4 = Completed, 5 = Cancelled, 6 = Auto-Cancelled
     *
     * @param status Order status code
     * @return Human-readable status text
     */
    private String getStatusText(Integer status) {
        return switch (status) {
            case 1 -> "Unpaid";
            case 2 -> "Unconfirmed";
            case 3 -> "Confirmed";
            case 4 -> "Completed";
            case 5 -> "Cancelled";
            case 6 -> "Auto-Cancelled";
            default -> "Unknown";
        };
    }
}