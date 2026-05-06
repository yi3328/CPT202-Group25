package com.example.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main entry point for the Specialist Appointment Ordering System.
 *
 * This Spring Boot application provides a REST API for managing:
 *   - Customer and specialist accounts
 *   - Appointment booking and scheduling
 *   - Order processing with automatic timeout handling
 *   - Payment processing
 *
 * The application enables scheduling for automated tasks:
 *   - Auto-cancellation of unpaid orders after 30 seconds
 *   - Auto-cancellation of unconfirmed orders after 30 seconds
 */
@SpringBootApplication
@EnableScheduling
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}