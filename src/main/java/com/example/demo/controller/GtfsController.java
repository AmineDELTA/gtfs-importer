package com.example.demo.controller;

import java.io.IOException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.Stop;
import com.example.demo.repository.StopRepository;
import com.example.demo.services.StopServices;

@RestController
@RequestMapping("/api/gtfs")
public class GtfsController {

    private final StopRepository stopRepository;
    private final StopServices stopServices;

    public GtfsController(StopRepository stopRepository, StopServices stopServices) {
        this.stopRepository = stopRepository;
        this.stopServices = stopServices;
    }

    @PostMapping("/import-stops")
    public ResponseEntity<String> importStops(@RequestParam MultipartFile file) {
        try {
            stopServices.importStops(file.getInputStream());
        } catch (IOException | IllegalArgumentException e) {
            return ResponseEntity.status(500).body("Error importing stops: " + e.getMessage());
        }
        return ResponseEntity.ok("Stops imported successfully");
    }

    @GetMapping("/stops")
    public Page<Stop> getStops(Pageable pageable) {
        return stopRepository.findAll(pageable);
    }
}