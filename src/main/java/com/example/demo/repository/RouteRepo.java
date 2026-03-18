package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Route;

@Repository
public interface RouteRepo extends JpaRepository<Route, String> {
    
}
