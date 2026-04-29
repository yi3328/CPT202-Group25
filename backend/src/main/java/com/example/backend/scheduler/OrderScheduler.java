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

@Component
public class OrderScheduler {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private SpecialistAvailabilityRepository availabilityRepository;

    // 每10秒检查一次未付款订单（30秒后自动取消）
    @Scheduled(fixedRate = 10000)
    @Transactional
    public void checkUnpaidOrders() {
        LocalDateTime threshold = LocalDateTime.now().minusSeconds(30);

        // 查找所有状态为 1 (Unpaid) 且创建时间超过30秒的订单
        List<Order> unpaidOrders = orderRepository.findByOrderStatus(1);

        for (Order order : unpaidOrders) {
            if (order.getCreatedAt() != null && order.getCreatedAt().isBefore(threshold)) {
                // 恢复时间段为可用
                restoreAvailability(order);

                order.setOrderStatus(5); // 改为已取消 (5 = Cancelled)
                orderRepository.save(order);
                System.out.println("Order " + order.getOrderID() + " auto-cancelled (unpaid)");
            }
        }
    }

    // 每10秒检查一次逾期订单（付款后30秒专家未确认）
    @Scheduled(fixedRate = 10000)
    @Transactional
    public void checkOverdueOrders() {
        LocalDateTime threshold = LocalDateTime.now().minusSeconds(30);

        // 查找所有状态为 2 (Unconfirmed) 且付款时间超过30秒的订单
        List<Order> overdueOrders = orderRepository.findByOrderStatus(2);

        for (Order order : overdueOrders) {
            if (order.getPaidAt() != null && order.getPaidAt().isBefore(threshold)) {
                order.setOrderStatus(5); // 改为逾期 (5 = Overdue)
                orderRepository.save(order);
                System.out.println("Order " + order.getOrderID() + " marked as overdue");
            }
        }
    }

    // 恢复专家时间段为可用
    private void restoreAvailability(Order order) {
        appointmentRepository.findById(order.getAppointmentID()).ifPresent(appointment -> {
            availabilityRepository.findBySpecialistIDAndAvailableDateAndSpecialistTimeslot(
                appointment.getSpecialistID(),
                appointment.getDate(),
                appointment.getTimeSlot()
            ).ifPresent(slot -> {
                slot.setIsBooked(0);
                availabilityRepository.save(slot);
            });
        });
    }
}