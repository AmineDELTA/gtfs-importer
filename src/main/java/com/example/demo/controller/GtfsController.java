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

import com.example.demo.model.Route;
import com.example.demo.model.Stop;
import com.example.demo.repository.RouteRepo;
import com.example.demo.repository.StopRepo;
import com.example.demo.services.RouteServices;
import com.example.demo.services.StopServices;

@RestController
@RequestMapping("/api/gtfs")
public class GtfsController {

    private final StopRepo stopRepository;
    private final StopServices stopServices;
    private final RouteRepo routeRepository;
    private final RouteServices routeServices;

    public GtfsController(StopRepo stopRepository, StopServices stopServices, RouteRepo routeRepository, RouteServices routeServices) {
        this.stopRepository = stopRepository;
        this.stopServices = stopServices;
        this.routeRepository = routeRepository;
        this.routeServices = routeServices;
    }

    @PostMapping("/import-stops")
    public ResponseEntity<String> importStops(@RequestParam MultipartFile file) {
        try {
            stopServices.importStops(file.getInputStream());
        } catch (IOException | RuntimeException e) {
            return ResponseEntity.status(500).body("Error importing stops: " + e.getMessage());
        }
        return ResponseEntity.ok("Stops imported successfully");
    }

    @GetMapping("/stops")
    public ResponseEntity<Page<Stop>> getStops(Pageable pageable) {
        return ResponseEntity.ok(stopRepository.findAll(pageable));
    }

    @PostMapping("/import-routes")
    public ResponseEntity<String> importRoutes(@RequestParam MultipartFile file) {
        try {
            routeServices.importRoutes(file.getInputStream());
        } catch (IOException | IllegalArgumentException e) {
          return ResponseEntity.status(500).body("Error importing routes: " + e.getMessage());
        }
        return ResponseEntity.ok("Routes imported successfully");
    }

    @GetMapping("/routes")
    public ResponseEntity<Page<Route>> getRoutes(Pageable pageable) {
        return ResponseEntity.ok(routeRepository.findAll(pageable));
    }
}