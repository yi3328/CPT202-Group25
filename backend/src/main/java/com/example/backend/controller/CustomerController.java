package com.example.backend.controller;

import com.example.backend.model.Customer;
import com.example.backend.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "*")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    // 1. Register Endpoint (注册接口)
    @PostMapping("/register")
    public ResponseEntity<?> registerCustomer(@RequestBody Customer newCustomer) {
        // 检查账号是否已存在
        if (customerRepository.findByCustomerNumber(newCustomer.getCustomerNumber()).isPresent()) {
            return ResponseEntity.badRequest().body("Error: Customer Number already exists.");
        }
        
        // 保存新客户
        customerRepository.save(newCustomer);
        return ResponseEntity.ok("Registration successful! You can now log in.");
    }

    // 2. Login Endpoint (登录接口)
    @PostMapping("/login")
    public ResponseEntity<?> loginCustomer(@RequestBody Customer loginRequest) {
        // 通过客户号查找用户
        Optional<Customer> customerOpt = customerRepository.findByCustomerNumber(loginRequest.getCustomerNumber());
        
        if (customerOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Error: Customer not found.");
        }

        Customer customer = customerOpt.get();
        
        // 核对密码
        if (!customer.getCustomerPassword().equals(loginRequest.getCustomerPassword())) {
            return ResponseEntity.badRequest().body("Error: Incorrect password.");
        }

        // 登录成功！返回关键数据给前端 (包括真实的 customerID)
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("message", "Login successful");
        responseData.put("customerID", customer.getCustomerID());
        responseData.put("customerName", customer.getCustomerName());

        return ResponseEntity.ok(responseData);
    }
}
