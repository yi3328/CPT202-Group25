package com.example.backend.controller;

import com.example.backend.model.*;
import com.example.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/specialists")
@CrossOrigin(origins = "*")
public class SpecialistController {

    @Autowired private SpecialistRepository specialistRepository;
    @Autowired private AppointmentRepository appointmentRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private SpecialistAvailabilityRepository availabilityRepository;

    // ==================== 登录 ====================

    @PostMapping("/login")
    public ResponseEntity<?> specialistLogin(@RequestBody Map<String, String> loginData) {
        String number = loginData.get("specialistNumber");
        String password = loginData.get("specialistPassword");

        Optional<Specialist> specialistOpt = specialistRepository
            .findBySpecialistNumberAndSpecialistPassword(number, password);

        if (specialistOpt.isPresent()) {
            Specialist s = specialistOpt.get();
            Map<String, Object> result = new HashMap<>();
            result.put("specialistID", s.getSpecialistID());
            result.put("specialistNumber", s.getSpecialistNumber());
            result.put("specialistName", s.getSpecialistName());
            result.put("specialistExpertise", s.getSpecialistExpertise());
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.badRequest().body(Map.of("message", "Invalid credentials"));
    }

    // ==================== 日程（所有预约） ====================

    @GetMapping("/{specialistId}/schedule")
    public ResponseEntity<List<Map<String, Object>>> getSpecialistSchedule(@PathVariable Integer specialistId) {
        List<Appointment> appointments = appointmentRepository.findBySpecialistID(specialistId);
        return ResponseEntity.ok(enrichAppointments(appointments));
    }

    // ==================== 预约详情 ====================

    @GetMapping("/{specialistId}/appointments/{appointmentId}")
    public ResponseEntity<?> getAppointmentDetail(@PathVariable Integer specialistId,
                                                   @PathVariable Integer appointmentId) {
        Optional<Appointment> apptOpt = appointmentRepository.findById(appointmentId);
        if (apptOpt.isEmpty() || !apptOpt.get().getSpecialistID().equals(specialistId)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Appointment not found"));
        }

        Appointment appt = apptOpt.get();
        Map<String, Object> detail = new HashMap<>();
        detail.put("appointmentID", appt.getAppointmentID());
        detail.put("date", appt.getDate());
        detail.put("timeSlot", appt.getTimeSlot());

        // 获取客户信息
        Optional<Customer> customer = customerRepository.findById(appt.getCustomerID());
        detail.put("customerName", customer.map(Customer::getCustomerName).orElse("Unknown"));
        detail.put("customerNumber", customer.map(Customer::getCustomerNumber).orElse(""));

        // 获取订单信息（按appointmentID精确查找）
        Optional<Order> order = orderRepository.findByAppointmentID(appt.getAppointmentID());
        if (order.isPresent()) {
            detail.put("orderID", order.get().getOrderID());
            detail.put("orderStatus", order.get().getOrderStatus());
        }

        // 获取专家可用性信息（房间号等）
        Optional<SpecialistAvailability> avail = availabilityRepository
            .findBySpecialistIDAndAvailableDateAndSpecialistTimeslot(
                specialistId, appt.getDate(), appt.getTimeSlot());
        if (avail.isPresent()) {
            detail.put("roomNumber", "Room " + (avail.get().getAvailabilityID() % 10 + 100));
        } else {
            detail.put("roomNumber", "TBD");
        }

        return ResponseEntity.ok(detail);
    }

    // ==================== 待确认订单 ====================

    @GetMapping("/{specialistId}/pending-orders")
    public ResponseEntity<List<Map<String, Object>>> getPendingOrders(@PathVariable Integer specialistId) {
        List<Appointment> appointments = appointmentRepository.findBySpecialistID(specialistId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Appointment appt : appointments) {
            Optional<Order> order = orderRepository.findByAppointmentID(appt.getAppointmentID());

            if (order.isPresent() && order.get().getOrderStatus() == 2) {
                Map<String, Object> item = new HashMap<>();
                item.put("appointmentID", appt.getAppointmentID());
                item.put("orderID", order.get().getOrderID());
                item.put("customerID", appt.getCustomerID());
                item.put("date", appt.getDate());
                item.put("timeSlot", appt.getTimeSlot());

                Optional<Customer> customer = customerRepository.findById(appt.getCustomerID());
                item.put("customerName", customer.map(Customer::getCustomerName).orElse("Unknown"));

                // 获取专家专业领域
                Optional<Specialist> specialist = specialistRepository.findById(specialistId);
                item.put("specialistExpertise", specialist.map(Specialist::getSpecialistExpertise).orElse("General"));

                // 检查是否超过30秒
                item.put("paidAt", order.get().getPaidAt());
                item.put("submitTime", order.get().getPaidAt() != null ?
                    order.get().getPaidAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "--");
                item.put("withinTime", isWithin30Seconds(order.get().getPaidAt()));
                item.put("orderStatus", "Unconfirmed");

                result.add(item);
            }
        }
        return ResponseEntity.ok(result);
    }

    // ==================== 确认订单 ====================

    @PutMapping("/orders/{orderId}/confirm")
    @Transactional
    public ResponseEntity<String> confirmOrder(@PathVariable Integer orderId) {
        Optional<Order> orderOpt = orderRepository.findByOrderID(orderId);
        if (orderOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Error: Order not found");
        }

        Order order = orderOpt.get();
        if (order.getOrderStatus() != 2) {
            return ResponseEntity.badRequest().body("Error: Order cannot be confirmed");
        }

        // 检查是否超过30秒
        if (!isWithin30Seconds(order.getPaidAt())) {
            return ResponseEntity.badRequest().body("Error: Order exceeded 30 seconds, auto-cancelled");
        }

        order.setOrderStatus(3); // 改为已确认 (3 = Confirmed)
        orderRepository.save(order);
        return ResponseEntity.ok("Success: Order confirmed");
    }

    // ==================== 已完成订单 ====================

    @GetMapping("/{specialistId}/completed-orders")
    public ResponseEntity<List<Map<String, Object>>> getCompletedOrders(@PathVariable Integer specialistId) {
        List<Appointment> appointments = appointmentRepository.findBySpecialistID(specialistId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Appointment appt : appointments) {
            Optional<Order> order = orderRepository.findByAppointmentID(appt.getAppointmentID());

            if (order.isPresent() && order.get().getOrderStatus() == 3) {
                Map<String, Object> item = new HashMap<>();
                item.put("appointmentID", appt.getAppointmentID());
                item.put("orderID", order.get().getOrderID());
                item.put("customerID", appt.getCustomerID());
                item.put("date", appt.getDate());
                item.put("timeSlot", appt.getTimeSlot());

                Optional<Customer> customer = customerRepository.findById(appt.getCustomerID());
                item.put("customerName", customer.map(Customer::getCustomerName).orElse("Unknown"));

                // 检查是否已过预约时间
                item.put("isTimeOver", isTimeOver(appt.getDate(), appt.getTimeSlot()));
                item.put("orderStatus", "Confirmed");

                result.add(item);
            }
        }
        return ResponseEntity.ok(result);
    }

    // ==================== 完成订单 ====================

    @PutMapping("/orders/{orderId}/complete")
    @Transactional
    public ResponseEntity<String> completeOrder(@PathVariable Integer orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Error: Order not found");
        }

        Order order = orderOpt.get();
        if (order.getOrderStatus() != 3) {
            return ResponseEntity.badRequest().body("Error: Order must be confirmed before completing");
        }

        order.setOrderStatus(4); // 已完成 (4 = Completed)
        orderRepository.save(order);
        return ResponseEntity.ok("Success: Order completed");
    }

    // ==================== 搜索过滤订单 ====================

    @GetMapping("/{specialistId}/search")
    public ResponseEntity<List<Map<String, Object>>> searchOrders(
            @PathVariable Integer specialistId,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String status) {

        List<Appointment> appointments = appointmentRepository.findBySpecialistID(specialistId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Appointment appt : appointments) {
            Optional<Order> order = orderRepository.findByAppointmentID(appt.getAppointmentID());

            if (order.isPresent()) {
                Optional<Customer> customer = customerRepository.findById(appt.getCustomerID());
                String custName = customer.map(Customer::getCustomerName).orElse("");
                Integer orderStatus = order.get().getOrderStatus();

                // 过滤条件
                boolean matchName = customerName == null || customerName.isEmpty()
                    || custName.toLowerCase().contains(customerName.toLowerCase());
                boolean matchStatus = status == null || status.equals("all")
                    || statusEquals(orderStatus, status);

                if (matchName && matchStatus) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("appointmentID", appt.getAppointmentID());
                    item.put("orderID", order.get().getOrderID());
                    item.put("customerName", custName);
                    item.put("date", appt.getDate());
                    item.put("timeSlot", appt.getTimeSlot());
                    item.put("orderStatus", getStatusText(orderStatus));
                    result.add(item);
                }
            }
        }
        return ResponseEntity.ok(result);
    }

    // ==================== 辅助方法 ====================

    private List<Map<String, Object>> enrichAppointments(List<Appointment> appointments) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Appointment appt : appointments) {
            Map<String, Object> item = new HashMap<>();
            item.put("appointmentID", appt.getAppointmentID());
            item.put("date", appt.getDate());
            item.put("timeSlot", appt.getTimeSlot());

            Optional<Customer> customer = customerRepository.findById(appt.getCustomerID());
            item.put("customerName", customer.map(Customer::getCustomerName).orElse("Unknown"));

            Optional<Order> order = orderRepository.findByAppointmentID(appt.getAppointmentID());
            if (order.isPresent()) {
                item.put("orderStatus", getStatusText(order.get().getOrderStatus()));
            }

            result.add(item);
        }
        return result;
    }

    private boolean isWithinTwoHours(LocalDate date, String timeSlot) {
        try {
            String[] times = timeSlot.split("-");
            LocalTime startTime = LocalTime.parse(times[0].trim());
            LocalDateTime appointmentTime = LocalDateTime.of(date, startTime);
            return LocalDateTime.now().isBefore(appointmentTime.plusHours(2));
        } catch (Exception e) {
            return true;
        }
    }

    private boolean isWithin30Seconds(LocalDateTime paidAt) {
        if (paidAt == null) return false;
        return LocalDateTime.now().isBefore(paidAt.plusSeconds(30));
    }

    private boolean isTimeOver(LocalDate date, String timeSlot) {
        try {
            String[] times = timeSlot.split("-");
            LocalTime startTime = LocalTime.parse(times[0].trim());
            LocalDateTime appointmentTime = LocalDateTime.of(date, startTime);
            return LocalDateTime.now().isAfter(appointmentTime);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean statusEquals(Integer status, String text) {
        return switch (text) {
            case "Unconfirmed" -> status == 2;
            case "Confirmed" -> status == 3;
            case "Completed" -> status == 4;
            case "Cancelled" -> status == 5;
            default -> false;
        };
    }

    private String getStatusText(Integer status) {
        return switch (status) {
            case 1 -> "Unpaid";
            case 2 -> "Unconfirmed";
            case 3 -> "Confirmed";
            case 4 -> "Completed";
            case 5 -> "Cancelled";
            default -> "Unknown";
        };
    }
}
