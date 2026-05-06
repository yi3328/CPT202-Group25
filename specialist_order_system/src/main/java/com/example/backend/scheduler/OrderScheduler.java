package com.example.backend.scheduler;

import com.example.backend.model.Order;
import com.example.backend.repository.OrderRepository;
import com.example.backend.repository.AppointmentRepository;
import com.example.backend.repository.SpecialistAvailabilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * OrderScheduler handles automated order management tasks.
 * Runs scheduled jobs to auto-cancel orders that exceed timeout thresholds.
 *
 * Business Rules:
 *   - Unpaid orders (status=1) are auto-cancelled after 30 seconds
 *   - Unconfirmed orders (status=2) are auto-cancelled 30 seconds after payment
 *   - Cancelled orders have their time slots released back to available
 */
@Component
public class OrderScheduler {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private SpecialistAvailabilityRepository availabilityRepository;

    /**
     * Checks for unpaid orders that have exceeded the 30-second payment window.
     * Runs every 10 seconds.
     * If an order remains unpaid for 30 seconds, it is cancelled and the time slot is released.
     */
    @Scheduled(fixedRate = 10000)
    @Transactional
    public void checkUnpaidOrders() {
        LocalDateTime threshold = LocalDateTime.now().minusSeconds(30);

        // Find all orders with status=1 (Unpaid) created more than 30 seconds ago
        List<Order> unpaidOrders = orderRepository.findByOrderStatus(1);

        for (Order order : unpaidOrders) {
            if (order.getCreatedAt() != null && order.getCreatedAt().isBefore(threshold)) {
                // Release the time slot back to available
                restoreAvailability(order);

                // Set order status to Cancelled (5)
                order.setOrderStatus(5);
                orderRepository.save(order);
                System.out.println("Order " + order.getOrderID() + " auto-cancelled (unpaid)");
            }
        }
    }

    /**
     * Checks for paid but unconfirmed orders that have exceeded the 30-second confirmation window.
     * Runs every 10 seconds.
     * If specialist fails to confirm within 30 seconds of payment, the order is auto-cancelled.
     */
    @Scheduled(fixedRate = 10000)
    @Transactional
    public void checkOverdueOrders() {
        LocalDateTime threshold = LocalDateTime.now().minusSeconds(30);

        // Find all orders with status=2 (Paid/Unconfirmed) paid more than 30 seconds ago
        List<Order> overdueOrders = orderRepository.findByOrderStatus(2);

        for (Order order : overdueOrders) {
            if (order.getPaidAt() != null && order.getPaidAt().isBefore(threshold)) {
                // Release the time slot back to available
                restoreAvailability(order);

                // Note: Appointment is NOT deleted to preserve booking history for my_bookings.html

                // Set order status to Auto-Cancelled (6)
                order.setOrderStatus(6);
                orderRepository.save(order);
                System.out.println("Order " + order.getOrderID() + " auto-cancelled (overdue, unconfirmed)");
            }
        }
    }

    /**
     * Releases a time slot back to available status when an order is cancelled.
     * Finds the corresponding availability record and sets isBooked to 0.
     *
     * @param order The order whose time slot should be released
     */
    private void restoreAvailability(Order order) {
        appointmentRepository.findById(order.getAppointmentID()).ifPresent(appointment -> {
            System.out.println("restoreAvailability called: appointmentID=" + order.getAppointmentID() +
                ", specialistID=" + appointment.getSpecialistID() +
                ", date=" + appointment.getDate() +
                ", timeSlot=" + appointment.getTimeSlot());

            var slotOpt = availabilityRepository
                .findBySpecialistIDAndAvailableDateAndSpecialistTimeslot(
                    appointment.getSpecialistID(),
                    appointment.getDate(),
                    appointment.getTimeSlot());

            if (slotOpt.isPresent()) {
                var slot = slotOpt.get();
                System.out.println("Found slot, current isBooked=" + slot.getIsBooked());
                // Set to Available (0)
                slot.setIsBooked(0);
                availabilityRepository.save(slot);
                System.out.println("Restored availability successfully");
            } else {
                System.out.println("Slot not found in availability table!");
            }
        });
    }
}