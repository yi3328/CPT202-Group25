package com.example.backend.controller;

import com.example.backend.model.Customer;
import com.example.backend.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * CustomerController handles customer authentication and profile management.
 *
 * Base path: /api/customers
 */
@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "*")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    /**
     * Registers a new customer account.
     *
     * @param newCustomer Customer object with customerNumber, customerName, customerPassword
     * @return Success message or error if customer number already exists
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerCustomer(@RequestBody Customer newCustomer) {
        // Check if customer number is already registered
        if (customerRepository.findByCustomerNumber(newCustomer.getCustomerNumber()).isPresent()) {
            return ResponseEntity.badRequest().body("Error: Customer Number already exists.");
        }

        // Save the new customer
        customerRepository.save(newCustomer);
        return ResponseEntity.ok("Registration successful! You can now log in.");
    }

    /**
     * Authenticates a customer.
     *
     * @param loginRequest Customer object with customerNumber and customerPassword
     * @return Customer ID and name if successful, error message otherwise
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginCustomer(@RequestBody Customer loginRequest) {
        // Find customer by customer number
        Optional<Customer> customerOpt = customerRepository.findByCustomerNumber(loginRequest.getCustomerNumber());

        if (customerOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Error: Customer not found.");
        }

        Customer customer = customerOpt.get();

        // Check if account is disabled
        if (customer.getIsActive() == 0) {
            return ResponseEntity.badRequest().body("Error: Account is disabled.");
        }

        // Verify password
        if (!customer.getCustomerPassword().equals(loginRequest.getCustomerPassword())) {
            return ResponseEntity.badRequest().body("Error: Incorrect password.");
        }

        // Return success with customer ID and name
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("message", "Login successful");
        responseData.put("customerID", customer.getCustomerID());
        responseData.put("customerName", customer.getCustomerName());

        return ResponseEntity.ok(responseData);
    }

    /**
     * Changes a customer's password.
     *
     * @param customerId Customer ID
     * @param request    Map containing currentPassword and newPassword
     * @return Success message or error if validation fails
     */
    @PutMapping("/{customerId}/password")
    public ResponseEntity<String> changePassword(
            @PathVariable Integer customerId,
            @RequestBody Map<String, String> request) {

        String currentPassword = request.get("currentPassword");
        String newPassword = request.get("newPassword");

        if (currentPassword == null || newPassword == null || currentPassword.isEmpty() || newPassword.isEmpty()) {
            return ResponseEntity.badRequest().body("Error: Current and new passwords are required");
        }

        Optional<Customer> customerOpt = customerRepository.findById(customerId);
        if (customerOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Error: Customer not found");
        }

        Customer customer = customerOpt.get();

        // Verify current password
        if (!customer.getCustomerPassword().equals(currentPassword)) {
            return ResponseEntity.badRequest().body("Error: Current password is incorrect");
        }

        // Update password
        customer.setCustomerPassword(newPassword);
        customerRepository.save(customer);

        return ResponseEntity.ok("Success: Password updated");
    }
}