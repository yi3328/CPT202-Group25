package com.example.backend.controller;

import com.example.backend.model.*;
import com.example.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * AdminController handles all administrative operations.
 * Provides endpoints for managing specialists, customers, orders, and time slots.
 *
 * Base path: /api/admin
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired private AdminRepository adminRepository;
    @Autowired private SpecialistRepository specialistRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private AppointmentRepository appointmentRepository;
    @Autowired private PaymentRepository paymentRepository;
    @Autowired private SpecialistAvailabilityRepository availabilityRepository;

    // ==================== Authentication ====================

    /**
     * Authenticates an admin user.
     *
     * @param loginData Map containing adminNumber and adminPassword
     * @return Admin ID and number if successful, error message otherwise
     */
    @PostMapping("/login")
    public ResponseEntity<?> adminLogin(@RequestBody Map<String, String> loginData) {
        String number = loginData.get("adminNumber");
        String password = loginData.get("adminPassword");

        var adminOpt = adminRepository.login(number, password);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            Map<String, Object> result = new HashMap<>();
            result.put("adminID", admin.getAdminID());
            result.put("adminNumber", admin.getAdminNumber());
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Invalid credentials"));
    }

    // ==================== Specialist Management ====================

    /**
     * Retrieves all specialists in the system.
     *
     * @return List of all specialists with their details
     */
    @GetMapping("/specialists")
    public ResponseEntity<List<Map<String, Object>>> getAllSpecialists() {
        List<Specialist> specialists = specialistRepository.findAll();
        List<Map<String, Object>> result = specialists.stream().map(s -> {
            Map<String, Object> map = new HashMap<>();
            map.put("specialistID", s.getSpecialistID());
            map.put("specialistNumber", s.getSpecialistNumber());
            map.put("specialistName", s.getSpecialistName());
            map.put("specialistExpertise", s.getSpecialistExpertise());
            map.put("isActive", s.getIsActive());
            map.put("roomNumber", s.getRoomNumber());
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    /**
     * Adds a new specialist to the system.
     *
     * @param data Map containing specialist details (number, name, password, expertise, roomNumber)
     * @return Success message or error if validation fails
     */
    @PostMapping("/specialists")
    public ResponseEntity<?> addSpecialist(@RequestBody Map<String, String> data) {
        String number = data.get("specialistNumber");
        String name = data.get("specialistName");
        String password = data.get("specialistPassword");
        String expertise = data.get("specialistExpertise");

        if (number == null || name == null || password == null || expertise == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "All fields are required"));
        }

        var existing = specialistRepository.findBySpecialistNumber(number);
        if (existing.isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Specialist number already exists"));
        }

        Specialist s = new Specialist();
        s.setSpecialistNumber(number);
        s.setSpecialistName(name);
        s.setSpecialistPassword(password);
        s.setSpecialistExpertise(expertise);
        s.setIsActive(1);
        if (data.containsKey("roomNumber")) s.setRoomNumber(data.get("roomNumber"));
        specialistRepository.save(s);
        return ResponseEntity.ok(Map.of("message", "Specialist added successfully"));
    }

    /**
     * Updates an existing specialist's information.
     *
     * @param id   Specialist ID to update
     * @param data Map containing fields to update (name, password, expertise, roomNumber)
     * @return Success message or error if specialist not found
     */
    @PutMapping("/specialists/{id}")
    public ResponseEntity<?> updateSpecialist(@PathVariable Integer id, @RequestBody Map<String, String> data) {
        var opt = specialistRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Specialist not found"));
        }

        Specialist s = opt.get();
        if (data.containsKey("specialistName")) s.setSpecialistName(data.get("specialistName"));
        if (data.containsKey("specialistPassword") && !data.get("specialistPassword").isEmpty()) {
            s.setSpecialistPassword(data.get("specialistPassword"));
        }
        if (data.containsKey("specialistExpertise")) s.setSpecialistExpertise(data.get("specialistExpertise"));
        if (data.containsKey("roomNumber")) s.setRoomNumber(data.get("roomNumber"));
        specialistRepository.save(s);
        return ResponseEntity.ok(Map.of("message", "Specialist updated successfully"));
    }

    /**
     * Deletes a specialist from the system.
     *
     * @param id Specialist ID to delete
     * @return Success message or error if specialist not found
     */
    @DeleteMapping("/specialists/{id}")
    public ResponseEntity<?> deleteSpecialist(@PathVariable Integer id) {
        if (specialistRepository.existsById(id)) {
            specialistRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Specialist deleted"));
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Specialist not found"));
    }

    /**
     * Toggles a specialist's active status (enable/disable).
     *
     * @param id Specialist ID to toggle
     * @return New status message ("Enabled" or "Disabled")
     */
    @PutMapping("/specialists/{id}/toggle")
    public ResponseEntity<?> toggleSpecialistStatus(@PathVariable Integer id) {
        var opt = specialistRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Specialist not found"));
        }
        Specialist s = opt.get();
        s.setIsActive(s.getIsActive() == 1 ? 0 : 1);
        specialistRepository.save(s);
        return ResponseEntity.ok(Map.of("message", s.getIsActive() == 1 ? "Enabled" : "Disabled"));
    }

    // ==================== Customer Management ====================

    /**
     * Retrieves all customers in the system.
     *
     * @return List of all customers with their details
     */
    @GetMapping("/customers")
    public ResponseEntity<List<Map<String, Object>>> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        List<Map<String, Object>> result = customers.stream().map(c -> {
            Map<String, Object> map = new HashMap<>();
            map.put("customerID", c.getCustomerID());
            map.put("customerNumber", c.getCustomerNumber());
            map.put("customerName", c.getCustomerName());
            map.put("isActive", c.getIsActive());
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    /**
     * Adds a new customer to the system.
     *
     * @param data Map containing customer details (number, name, password)
     * @return Success message or error if validation fails
     */
    @PostMapping("/customers")
    public ResponseEntity<?> addCustomer(@RequestBody Map<String, String> data) {
        String number = data.get("customerNumber");
        String name = data.get("customerName");
        String password = data.get("customerPassword");

        if (number == null || name == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "All fields are required"));
        }

        var existing = customerRepository.findByCustomerNumber(number);
        if (existing.isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Customer number already exists"));
        }

        Customer c = new Customer();
        c.setCustomerNumber(number);
        c.setCustomerName(name);
        c.setCustomerPassword(password);
        c.setIsActive(1);
        customerRepository.save(c);
        return ResponseEntity.ok(Map.of("message", "Customer added successfully"));
    }

    /**
     * Updates an existing customer's information.
     *
     * @param id   Customer ID to update
     * @param data Map containing fields to update (name, password)
     * @return Success message or error if customer not found
     */
    @PutMapping("/customers/{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable Integer id, @RequestBody Map<String, String> data) {
        var opt = customerRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Customer not found"));
        }

        Customer c = opt.get();
        if (data.containsKey("customerName")) c.setCustomerName(data.get("customerName"));
        if (data.containsKey("customerPassword") && !data.get("customerPassword").isEmpty()) {
            c.setCustomerPassword(data.get("customerPassword"));
        }
        customerRepository.save(c);
        return ResponseEntity.ok(Map.of("message", "Customer updated successfully"));
    }

    /**
     * Deletes a customer from the system.
     *
     * @param id Customer ID to delete
     * @return Success message or error if customer not found
     */
    @DeleteMapping("/customers/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable Integer id) {
        if (customerRepository.existsById(id)) {
            customerRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Customer deleted"));
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Customer not found"));
    }

    /**
     * Toggles a customer's active status (enable/disable).
     *
     * @param id Customer ID to toggle
     * @return New status message ("Enabled" or "Disabled")
     */
    @PutMapping("/customers/{id}/toggle")
    public ResponseEntity<?> toggleCustomerStatus(@PathVariable Integer id) {
        var opt = customerRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Customer not found"));
        }
        Customer c = opt.get();
        c.setIsActive(c.getIsActive() == 1 ? 0 : 1);
        customerRepository.save(c);
        return ResponseEntity.ok(Map.of("message", c.getIsActive() == 1 ? "Enabled" : "Disabled"));
    }

    // ==================== Search ====================

    /**
     * Searches specialists by name, number, or expertise.
     *
     * @param query Search term (optional)
     * @return List of matching specialists
     */
    @GetMapping("/specialists/search")
    public ResponseEntity<List<Map<String, Object>>> searchSpecialists(@RequestParam(required = false) String query) {
        List<Specialist> specialists;
        if (query == null || query.isEmpty()) {
            specialists = specialistRepository.findAll();
        } else {
            specialists = specialistRepository.findAll().stream()
                .filter(s -> s.getSpecialistName().toLowerCase().contains(query.toLowerCase())
                    || s.getSpecialistNumber().toLowerCase().contains(query.toLowerCase())
                    || s.getSpecialistExpertise().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
        }
        List<Map<String, Object>> result = specialists.stream().map(s -> {
            Map<String, Object> map = new HashMap<>();
            map.put("specialistID", s.getSpecialistID());
            map.put("specialistNumber", s.getSpecialistNumber());
            map.put("specialistName", s.getSpecialistName());
            map.put("specialistExpertise", s.getSpecialistExpertise());
            map.put("isActive", s.getIsActive());
            map.put("roomNumber", s.getRoomNumber());
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    /**
     * Searches customers by name or number.
     *
     * @param query Search term (optional)
     * @return List of matching customers
     */
    @GetMapping("/customers/search")
    public ResponseEntity<List<Map<String, Object>>> searchCustomers(@RequestParam(required = false) String query) {
        List<Customer> customers;
        if (query == null || query.isEmpty()) {
            customers = customerRepository.findAll();
        } else {
            customers = customerRepository.findAll().stream()
                .filter(c -> c.getCustomerName().toLowerCase().contains(query.toLowerCase())
                    || c.getCustomerNumber().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
        }
        List<Map<String, Object>> result = customers.stream().map(c -> {
            Map<String, Object> map = new HashMap<>();
            map.put("customerID", c.getCustomerID());
            map.put("customerNumber", c.getCustomerNumber());
            map.put("customerName", c.getCustomerName());
            map.put("isActive", c.getIsActive());
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    // ==================== Order Management ====================

    /**
     * Retrieves all orders with full details including customer, specialist, and payment info.
     *
     * @return List of all orders with enriched details
     */
    @GetMapping("/orders")
    public ResponseEntity<List<Map<String, Object>>> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Order order : orders) {
            Map<String, Object> map = new HashMap<>();
            map.put("orderID", order.getOrderID());
            map.put("orderStatus", order.getOrderStatus());
            map.put("createdAt", order.getCreatedAt());
            map.put("paidAt", order.getPaidAt());

            // Enrich with appointment details
            appointmentRepository.findById(order.getAppointmentID()).ifPresent(appt -> {
                map.put("date", appt.getDate());
                map.put("timeSlot", appt.getTimeSlot());
                map.put("specialistID", appt.getSpecialistID());

                // Get specialist name
                specialistRepository.findById(appt.getSpecialistID()).ifPresent(spec -> {
                    map.put("specialistName", spec.getSpecialistName());
                });

                // Get customer name and number
                customerRepository.findById(appt.getCustomerID()).ifPresent(cust -> {
                    map.put("customerName", cust.getCustomerName());
                    map.put("customerNumber", cust.getCustomerNumber());
                });
            });

            // Enrich with payment details
            paymentRepository.findByOrderID(order.getOrderID()).ifPresent(pay -> {
                map.put("paymentAmount", pay.getPaymentAmount());
                map.put("paymentStatus", pay.getPaymentStatus());
            });

            result.add(map);
        }
        return ResponseEntity.ok(result);
    }

    /**
     * Retrieves order statistics by status.
     *
     * @return Map containing count of orders in each status category
     */
    @GetMapping("/orders/stats")
    public ResponseEntity<Map<String, Long>> getOrderStats() {
        List<Order> allOrders = orderRepository.findAll();

        long unpaid = allOrders.stream().filter(o -> o.getOrderStatus() == 1).count();
        long unconfirmed = allOrders.stream().filter(o -> o.getOrderStatus() == 2).count();
        long confirmed = allOrders.stream().filter(o -> o.getOrderStatus() == 3).count();
        long completed = allOrders.stream().filter(o -> o.getOrderStatus() == 4).count();
        long cancelled = allOrders.stream().filter(o -> o.getOrderStatus() == 5).count();
        long autoCancelled = allOrders.stream().filter(o -> o.getOrderStatus() == 6).count();

        Map<String, Long> stats = new HashMap<>();
        stats.put("unpaid", unpaid);
        stats.put("unconfirmed", unconfirmed);
        stats.put("confirmed", confirmed);
        stats.put("completed", completed);
        stats.put("cancelled", cancelled);
        stats.put("autoCancelled", autoCancelled);
        stats.put("total", (long) allOrders.size());

        return ResponseEntity.ok(stats);
    }

    // ==================== Time Slot Management ====================

    /**
     * Retrieves all specialist availability time slots.
     *
     * @return List of all time slots with specialist names
     */
    @GetMapping("/availabilities")
    public ResponseEntity<List<Map<String, Object>>> getAllAvailabilities() {
        List<SpecialistAvailability> avails = availabilityRepository.findAll();
        List<Map<String, Object>> result = avails.stream().map(a -> {
            Map<String, Object> map = new HashMap<>();
            map.put("availabilityID", a.getAvailabilityID());
            map.put("specialistID", a.getSpecialistID());
            map.put("date", a.getAvailableDate());
            map.put("timeSlot", a.getSpecialistTimeslot());
            map.put("isBooked", a.getIsBooked());
            specialistRepository.findById(a.getSpecialistID()).ifPresent(s -> map.put("specialistName", s.getSpecialistName()));
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    /**
     * Adds a new availability time slot for a specialist.
     *
     * @param data Map containing specialistID, date, and timeSlot
     * @return Success message or error if time slot already exists
     */
    @PostMapping("/availabilities")
    public ResponseEntity<?> addAvailability(@RequestBody Map<String, String> data) {
        Integer specialistID = Integer.parseInt(data.get("specialistID"));
        String dateStr = data.get("date");
        String timeSlot = data.get("timeSlot");

        var existing = availabilityRepository.findBySpecialistIDAndAvailableDateAndSpecialistTimeslot(
            specialistID, java.time.LocalDate.parse(dateStr), timeSlot);
        if (existing.isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Time slot already exists"));
        }

        SpecialistAvailability av = new SpecialistAvailability();
        av.setSpecialistID(specialistID);
        av.setAvailableDate(java.time.LocalDate.parse(dateStr));
        av.setSpecialistTimeslot(timeSlot);
        av.setIsBooked(0);
        availabilityRepository.save(av);
        return ResponseEntity.ok(Map.of("message", "Time slot added"));
    }

    /**
     * Updates an existing availability time slot.
     *
     * @param id   Availability ID to update
     * @param data Map containing fields to update (date, timeSlot)
     * @return Success message or error if time slot not found
     */
    @PutMapping("/availabilities/{id}")
    public ResponseEntity<?> updateAvailability(@PathVariable Integer id, @RequestBody Map<String, String> data) {
        var opt = availabilityRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Time slot not found"));
        }

        SpecialistAvailability av = opt.get();
        if (data.containsKey("date")) av.setAvailableDate(java.time.LocalDate.parse(data.get("date")));
        if (data.containsKey("timeSlot")) av.setSpecialistTimeslot(data.get("timeSlot"));
        availabilityRepository.save(av);
        return ResponseEntity.ok(Map.of("message", "Time slot updated"));
    }

    /**
     * Deletes an availability time slot.
     *
     * @param id Availability ID to delete
     * @return Success message or error if time slot not found
     */
    @DeleteMapping("/availabilities/{id}")
    public ResponseEntity<?> deleteAvailability(@PathVariable Integer id) {
        if (availabilityRepository.existsById(id)) {
            availabilityRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Time slot deleted"));
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Time slot not found"));
    }
}