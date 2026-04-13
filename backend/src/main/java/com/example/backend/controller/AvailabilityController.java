package com.example.backend.controller;

import com.example.backend.model.Specialist;
import com.example.backend.model.SpecialistAvailability;
import com.example.backend.repository.SpecialistAvailabilityRepository;
import com.example.backend.repository.SpecialistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/availabilities")
@CrossOrigin(origins = "*")
public class AvailabilityController {

    @Autowired
    private SpecialistAvailabilityRepository availabilityRepository;

    @Autowired
    private SpecialistRepository specialistRepository;

    // 获取所有专家的排班状态，并发给前端
    @GetMapping("/all")
    public ResponseEntity<List<Map<String, Object>>> getAllAvailabilities() {
        List<SpecialistAvailability> availabilities = availabilityRepository.findAll();
        List<Map<String, Object>> responseList = new ArrayList<>();

        for (SpecialistAvailability avail : availabilities) {
            Map<String, Object> data = new HashMap<>();
            data.put("availabilityID", avail.getAvailabilityID());
            data.put("specialistID", avail.getSpecialistID());
            data.put("date", avail.getAvailableDate());
            data.put("slot", avail.getSpecialistTimeslot());
            data.put("isBooked", avail.getIsBooked()); // 关键状态：0 还是 1

            // 顺便把专家的名字查出来
            Optional<Specialist> specialist = specialistRepository.findById(avail.getSpecialistID());
            if (specialist.isPresent()) {
                data.put("name", specialist.get().getSpecialistName());
            } else {
                data.put("name", "Specialist " + avail.getSpecialistID());
            }
            
            responseList.add(data);
        }
        
        return ResponseEntity.ok(responseList);
    }
}