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
import com.example.demo.model.StopTime;
import com.example.demo.model.Trip;
import com.example.demo.services.GtfsImportService;
import com.example.demo.services.RouteServices;
import com.example.demo.services.StopServices;
import com.example.demo.services.StopTimeServices;
import com.example.demo.services.TripServices;

@RestController
@RequestMapping("/api/gtfs")
public class GtfsController {

    private final StopServices stopServices;
    private final RouteServices routeServices;
    private final TripServices tripServices;
    private final StopTimeServices stopTimeServices;
    private final GtfsImportService gtfsImportService;

    public GtfsController(StopServices stopServices, RouteServices routeServices, TripServices tripServices, StopTimeServices stopTimeServices, GtfsImportService gtfsImportService) {
        this.stopServices = stopServices;
        this.routeServices = routeServices;
        this.tripServices = tripServices;
        this.stopTimeServices = stopTimeServices;
        this.gtfsImportService = gtfsImportService;
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
        return ResponseEntity.ok(stopServices.getStops(pageable));
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
        return ResponseEntity.ok(routeServices.getRoutes(pageable));
    }

    @PostMapping("/import-trips")
    public ResponseEntity<String> importTrips(@RequestParam MultipartFile file) {
        try {
            tripServices.importTrips(file.getInputStream());
        } catch (IOException | IllegalArgumentException e) {
          return ResponseEntity.status(500).body("Error importing trips: " + e.getMessage());
        }
        return ResponseEntity.ok("Trips imported successfully");
    }

    @GetMapping("/trips")
    public ResponseEntity<Page<Trip>> getTrips(Pageable pageable) {
        return ResponseEntity.ok(tripServices.getTrips(pageable));
    }

    @PostMapping("/import-stop-times")
    public ResponseEntity<String> importStopTimes(@RequestParam MultipartFile file) {
        try {
            stopTimeServices.importStopTimes(file.getInputStream());
        } catch (IOException | IllegalArgumentException e) {
          return ResponseEntity.status(500).body("Error importing stop times: " + e.getMessage());
        }
        return ResponseEntity.ok("Stop times imported successfully");
    }

    @GetMapping("/stop-times")    
    public ResponseEntity<Page<StopTime>> getStopTimes(Pageable pageable) {
        return ResponseEntity.ok(stopTimeServices.getStopTimes(pageable));
    }

    @PostMapping("/import")
public ResponseEntity<String> importGtfs(@RequestParam MultipartFile file) {
    try {
        gtfsImportService.importGtfs(file.getInputStream());
    } catch (IOException | RuntimeException e) {
        return ResponseEntity.status(500).body("Import failed: " + e.getMessage());
    }
    return ResponseEntity.ok("GTFS data imported successfully");
}
}