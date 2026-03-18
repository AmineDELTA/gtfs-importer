package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.StopTime;

@Repository
public interface StopTimeRepo extends JpaRepository<StopTime, String> {
    
}
 