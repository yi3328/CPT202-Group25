package com.example.backend.repository;

import com.example.backend.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository for Payment entity operations.
 * Provides methods for querying payments by order.
 */
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    /**
     * Finds a payment by its associated order ID.
     *
     * @param orderID The order ID to search for
     * @return Optional containing the payment if found
     */
    Optional<Payment> findByOrderID(Integer orderID);
}