package com.example.backend.repository;

import com.example.backend.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
    List<Feedback> findBySpecialistID(Integer specialistID);
    boolean existsByOrderID(Integer orderID);
}