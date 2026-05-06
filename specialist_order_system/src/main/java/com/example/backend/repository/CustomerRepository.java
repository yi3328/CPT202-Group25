package com.example.backend.repository;

import com.example.backend.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository for Customer entity operations.
 * Provides methods for querying customers by their credentials.
 */
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    /**
     * Finds a customer by their unique customer number.
     *
     * @param customerNumber The customer's unique identifier
     * @return Optional containing the customer if found
     */
    Optional<Customer> findByCustomerNumber(String customerNumber);
}