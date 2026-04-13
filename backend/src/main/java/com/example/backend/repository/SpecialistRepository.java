package com.example.backend.repository;

import com.example.backend.model.Specialist;
import org.springframework.data.jpa.repository.JpaRepository;

// This interface allows us to fetch Specialist details (like name) from the database
public interface SpecialistRepository extends JpaRepository<Specialist, Integer> {
}
