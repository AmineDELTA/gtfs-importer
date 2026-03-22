package com.amine.gtfs.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.amine.gtfs.model.Trip;

@Repository
public interface TripRepo extends JpaRepository<Trip, String> {

	@Override
	@EntityGraph(attributePaths = "route")
	Page<Trip> findAll(Pageable pageable);
}
