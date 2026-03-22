package com.amine.gtfs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.amine.gtfs.model.StopTime;

@Repository
public interface StopTimeRepo extends JpaRepository<StopTime, Long> {
    
}
 