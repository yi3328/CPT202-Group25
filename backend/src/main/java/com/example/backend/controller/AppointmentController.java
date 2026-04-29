package com.example.backend.controller;

import com.example.backend.model.Appointment;
import com.example.backend.model.Order;
import com.example.backend.model.Specialist;
import com.example.backend.model.SpecialistAvailability;
import com.example.backend.repository.AppointmentRepository;
import com.example.backend.repository.OrderRepository;
import com.example.backend.repository.SpecialistAvailabilityRepository;
import com.example.backend.repository.SpecialistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "*")
public class AppointmentController {

    @Autowired private AppointmentRepository appointmentRepository;
    @Autowired private SpecialistAvailabilityRepository availabilityRepository;
    @Autowired private SpecialistRepository specialistRepository;
    @Autowired private OrderRepository orderRepository; 

    @PostMapping("/book")
    @Transactional
    public ResponseEntity<String> createAppointment(@RequestBody Appointment request) {
        var availability = availabilityRepository.findBySpecialistIDAndAvailableDateAndSpecialistTimeslot(
            request.getSpecialistID(), request.getDate(), request.getTimeSlot());

        if (availability.isEmpty() || availability.get().getIsBooked() == 1) {
            return ResponseEntity.badRequest().body("Error: This slot is no longer available.");
        }

        SpecialistAvailability slot = availability.get();
        slot.setIsBooked(1); 
        availabilityRepository.save(slot);
        
        Appointment savedAppt = appointmentRepository.save(request);

        Order newOrder = new Order();
        newOrder.setCustomerID(request.getCustomerID());
        newOrder.setSpecialistID(request.getSpecialistID());
        newOrder.setAppointmentID(savedAppt.getAppointmentID());
        newOrder.setOrderStatus(1); // 1 = Unpaid
        orderRepository.save(newOrder);

        return ResponseEntity.ok("Success: Appointment confirmed!");
    }

    @GetMapping("/customer/{customerID}")
    public ResponseEntity<List<Map<String, Object>>> getCustomerBookings(@PathVariable Integer customerID) {
        List<Appointment> appointments = appointmentRepository.findByCustomerID(customerID);
        List<Map<String, Object>> responseList = new ArrayList<>();

        for (Appointment appt : appointments) {
            Map<String, Object> bookingData = new HashMap<>();
            bookingData.put("appointmentID", appt.getAppointmentID());
            bookingData.put("date", appt.getDate());
            bookingData.put("timeSlot", appt.getTimeSlot());
            
            Optional<Specialist> specialist = specialistRepository.findById(appt.getSpecialistID());
            bookingData.put("specialistName", specialist.map(Specialist::getSpecialistName).orElse("Specialist"));

            Optional<Order> order = orderRepository.findByAppointmentID(appt.getAppointmentID());

            if (order.isPresent()) {
                bookingData.put("orderID", order.get().getOrderID());
                bookingData.put("orderStatus", order.get().getOrderStatus());
                bookingData.put("createdAt", order.get().getCreatedAt());
            } else {
                // 安全兜底：如果发生异常找不到订单，强制视为未支付 (1)
                bookingData.put("orderStatus", 1);
                bookingData.put("orderID", 9999);
            }
            
            responseList.add(bookingData);
        }
        return ResponseEntity.ok(responseList);
    }

    @DeleteMapping("/cancel/{appointmentID}")
    @Transactional
    public ResponseEntity<String> cancelAppointment(@PathVariable Integer appointmentID) {
        var appointmentOpt = appointmentRepository.findById(appointmentID);
        if (appointmentOpt.isEmpty()) return ResponseEntity.badRequest().body("Error: Appointment not found.");
        
        Appointment appointment = appointmentOpt.get();
        var availability = availabilityRepository.findBySpecialistIDAndAvailableDateAndSpecialistTimeslot(
            appointment.getSpecialistID(), appointment.getDate(), appointment.getTimeSlot());

        if (availability.isPresent()) {
            SpecialistAvailability slot = availability.get();
            slot.setIsBooked(0); 
            availabilityRepository.save(slot);
        }

        // 取消订单：按appointmentID查找
        Optional<Order> order = orderRepository.findByAppointmentID(appointmentID);
        if(order.isPresent()){
            Order o = order.get();
            o.setOrderStatus(5); // 5 = Cancelled
            orderRepository.save(o);
        }

        appointmentRepository.deleteById(appointmentID);
        return ResponseEntity.ok("Success: Appointment cancelled.");
    }
}