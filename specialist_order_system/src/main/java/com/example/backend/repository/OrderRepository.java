package com.example.backend.repository;

import com.example.backend.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Order entity operations.
 * Provides methods for querying orders by various criteria.
 */
public interface OrderRepository extends JpaRepository<Order, Integer> {

    /**
     * Finds an order by its exact order ID.
     *
     * @param orderID The order ID to search for
     * @return Optional containing the order if found
     */
    Optional<Order> findByOrderID(Integer orderID);

    /**
     * Finds all orders with a specific status.
     *
     * @param orderStatus The order status code to filter by
     * @return List of orders with the given status
     */
    List<Order> findByOrderStatus(Integer orderStatus);

    /**
     * Finds the most recent order for a customer-specialist pair.
     *
     * @param customerID   Customer ID
     * @param specialistID Specialist ID
     * @return Optional containing the most recent order if found
     */
    Optional<Order> findFirstByCustomerIDAndSpecialistIDOrderByOrderIDDesc(Integer customerID, Integer specialistID);

    /**
     * Finds an order associated with a specific appointment.
     *
     * @param appointmentID Appointment ID
     * @return Optional containing the order if found
     */
    Optional<Order> findByAppointmentID(Integer appointmentID);

    /**
     * Finds all active (non-cancelled) orders for a customer at a specific date and time slot.
     * Used to prevent double-booking at the same time.
     *
     * @param customerID Customer ID
     * @param date       Appointment date
     * @param timeSlot   Time slot string
     * @return List of active orders at the same time (excluding cancelled status 5 and auto-cancelled status 6)
     */
    @Query("SELECT o FROM Order o WHERE o.customerID = :customerID " +
           "AND o.appointmentID IN (SELECT a.appointmentID FROM Appointment a WHERE a.date = :date AND a.timeSlot = :timeSlot) " +
           "AND o.orderStatus NOT IN (5, 6)")
    List<Order> findActiveOrdersAtSameTime(
        @Param("customerID") Integer customerID,
        @Param("date") java.time.LocalDate date,
        @Param("timeSlot") String timeSlot);

    /**
     * Finds all active (non-cancelled) orders for a customer with a specific specialist on a given date.
     * Used to prevent booking the same specialist twice on the same day.
     *
     * @param customerID   Customer ID
     * @param specialistID Specialist ID
     * @param date         Appointment date
     * @return List of active orders for the same specialist on the same day
     */
    @Query("SELECT o FROM Order o WHERE o.customerID = :customerID AND o.specialistID = :specialistID " +
           "AND o.appointmentID IN (SELECT a.appointmentID FROM Appointment a WHERE a.date = :date) " +
           "AND o.orderStatus NOT IN (5, 6)")
    List<Order> findActiveOrdersSameDay(
        @Param("customerID") Integer customerID,
        @Param("specialistID") Integer specialistID,
        @Param("date") java.time.LocalDate date);
}