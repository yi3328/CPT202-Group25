package com.example.backend.repository;
import com.example.backend.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    // 根据订单ID精确查找
    Optional<Order> findByOrderID(Integer orderID);

    // 根据订单状态查找
    List<Order> findByOrderStatus(Integer orderStatus);

    // 根据客户和专家查找订单（按订单ID倒序）
    Optional<Order> findFirstByCustomerIDAndSpecialistIDOrderByOrderIDDesc(Integer customerID, Integer specialistID);

    // 根据预约ID查找订单（更精确）
    Optional<Order> findByAppointmentID(Integer appointmentID);
}