package com.example.backend.controller;

import com.example.backend.model.Specialist;
import com.example.backend.model.SpecialistAvailability;
import com.example.backend.repository.SpecialistAvailabilityRepository;
import com.example.backend.repository.SpecialistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    // 获取所有专家的排班状态，并发给前端（只返回今天及之后的）
    @GetMapping("/all")
    public ResponseEntity<List<Map<String, Object>>> getAllAvailabilities() {
        List<SpecialistAvailability> availabilities = availabilityRepository.findAll();
        List<Map<String, Object>> responseList = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (SpecialistAvailability avail : availabilities) {
            // 只添加今天或之后的排班
            if (avail.getAvailableDate().isBefore(today)) {
                continue;
            }
            Map<String, Object> data = new HashMap<>();
            data.put("availabilityID", avail.getAvailabilityID());
            data.put("specialistID", avail.getSpecialistID());
            data.put("date", avail.getAvailableDate());
            data.put("slot", avail.getSpecialistTimeslot());
            data.put("isBooked", avail.getIsBooked()); // 关键状态：0 还是 1

            // 顺便把专家的名字和领域查出来
            Optional<Specialist> specialist = specialistRepository.findById(avail.getSpecialistID());
            if (specialist.isPresent()) {
                data.put("name", specialist.get().getSpecialistName());
                data.put("specialistExpertise", specialist.get().getSpecialistExpertise());
            } else {
                data.put("name", "Specialist " + avail.getSpecialistID());
                data.put("specialistExpertise", "General");
            }
            
            responseList.add(data);
        }
        
        return ResponseEntity.ok(responseList);
    }

    // 获取所有不重复的专家领域，用于下拉筛选
    @GetMapping("/expertises")
    public ResponseEntity<List<String>> getAllExpertises() {
        List<Specialist> specialists = specialistRepository.findAll();
        List<String> expertises = specialists.stream()
            .map(Specialist::getSpecialistExpertise)
            .filter(e -> e != null && !e.isEmpty())
            .distinct()
            .sorted()
            .toList();
        return ResponseEntity.ok(expertises);
    }

    // 获取所有不重复的时间段，用于下拉筛选
    @GetMapping("/timeslots")
    public ResponseEntity<List<String>> getAllTimeslots() {
        List<SpecialistAvailability> availabilities = availabilityRepository.findAll();
        List<String> timeslots = availabilities.stream()
            .map(SpecialistAvailability::getSpecialistTimeslot)
            .filter(t -> t != null && !t.isEmpty())
            .distinct()
            .sorted((a, b) -> {
                // 按开始时间排序
                String timeA = a.split(" - ")[0];
                String timeB = b.split(" - ")[0];
                return timeA.compareTo(timeB);
            })
            .toList();
        return ResponseEntity.ok(timeslots);
    }
}