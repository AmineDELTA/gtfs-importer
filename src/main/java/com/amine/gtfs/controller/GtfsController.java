package com.amine.gtfs.controller;

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

import com.amine.gtfs.model.Route;
import com.amine.gtfs.model.Stop;
import com.amine.gtfs.model.StopTime;
import com.amine.gtfs.model.Trip;
import com.amine.gtfs.services.GtfsImportService;
import com.amine.gtfs.services.RouteService;
import com.amine.gtfs.services.StopService;
import com.amine.gtfs.services.StopTimeService;
import com.amine.gtfs.services.TripService;

@RestController
@RequestMapping("/api/gtfs")
public class GtfsController {

    private final StopService stopService;
    private final RouteService routeService;
    private final TripService tripService;
    private final StopTimeService stopTimeService;
    private final GtfsImportService gtfsImportService;

    public GtfsController(StopService stopService, RouteService routeService, TripService tripService, StopTimeService stopTimeService, GtfsImportService gtfsImportService) {
        this.stopService = stopService;
        this.routeService = routeService;
        this.tripService = tripService;
        this.stopTimeService = stopTimeService;
        this.gtfsImportService = gtfsImportService;
    }

    @PostMapping("/import-stops")
    public ResponseEntity<String> importStops(@RequestParam MultipartFile file) {
        try {
            stopService.importStops(file.getInputStream());
        } catch (IOException | RuntimeException e) {
            return ResponseEntity.status(500).body("Error importing stops: " + e.getMessage());
        }
        return ResponseEntity.ok("Stops imported successfully");
    }

    @GetMapping("/stops")
    public ResponseEntity<Page<Stop>> getStops(Pageable pageable) {
        return ResponseEntity.ok(stopService.getStops(pageable));
    }

    @PostMapping("/import-routes")
    public ResponseEntity<String> importRoutes(@RequestParam MultipartFile file) {
        try {
            routeService.importRoutes(file.getInputStream());
        } catch (IOException | IllegalArgumentException e) {
          return ResponseEntity.status(500).body("Error importing routes: " + e.getMessage());
        }
        return ResponseEntity.ok("Routes imported successfully");
    }

    @GetMapping("/routes")
    public ResponseEntity<Page<Route>> getRoutes(Pageable pageable) {
        return ResponseEntity.ok(routeService.getRoutes(pageable));
    }

    @PostMapping("/import-trips")
    public ResponseEntity<String> importTrips(@RequestParam MultipartFile file) {
        try {
            tripService.importTrips(file.getInputStream());
        } catch (IOException | IllegalArgumentException e) {
          return ResponseEntity.status(500).body("Error importing trips: " + e.getMessage());
        }
        return ResponseEntity.ok("Trips imported successfully");
    }

    @GetMapping("/trips")
    public ResponseEntity<Page<Trip>> getTrips(Pageable pageable) {
        return ResponseEntity.ok(tripService.getTrips(pageable));
    }

    @PostMapping("/import-stop-times")
    public ResponseEntity<String> importStopTimes(@RequestParam MultipartFile file) {
        try {
            stopTimeService.importStopTimes(file.getInputStream());
        } catch (IOException | IllegalArgumentException e) {
          return ResponseEntity.status(500).body("Error importing stop times: " + e.getMessage());
        }
        return ResponseEntity.ok("Stop times imported successfully");
    }

    @GetMapping("/stop-times")    
    public ResponseEntity<Page<StopTime>> getStopTimes(Pageable pageable) {
        return ResponseEntity.ok(stopTimeService.getStopTimes(pageable));
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