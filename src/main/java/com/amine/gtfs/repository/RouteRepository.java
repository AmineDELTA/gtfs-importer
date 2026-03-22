package com.amine.gtfs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.amine.gtfs.model.Route;

@Repository
public interface RouteRepository extends JpaRepository<Route, String> {
    
}
