package com.example.backend.repository;

import com.example.backend.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// Changed the ID type from Long to Integer to match the new customerID format
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    
    // Find customer by their unique number
    Optional<Customer> findByCustomerNumber(String customerNumber);
}