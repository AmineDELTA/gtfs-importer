package com.amine.gtfs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.amine.gtfs.model.Stop;

@Repository
public interface StopRepo extends JpaRepository<Stop, String> {
}