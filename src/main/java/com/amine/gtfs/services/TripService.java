package com.amine.gtfs.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.amine.gtfs.error.GtfsImportException;
import com.amine.gtfs.model.Route;
import com.amine.gtfs.model.Trip;
import com.amine.gtfs.repository.RouteRepository;
import com.amine.gtfs.repository.TripRepository;

import jakarta.transaction.Transactional;
@Service
public class TripService {
    private static final Logger log = LoggerFactory.getLogger(TripService.class);
    private final TripRepository tripRepository;
    private final RouteRepository routeRepository;

    public TripService(TripRepository tripRepository, RouteRepository routeRepository) {
        this.tripRepository = tripRepository;
        this.routeRepository = routeRepository;
    }

    @Transactional
    public void importTrips(InputStream inputStream) {
        try (BufferedReader reader =
                new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            HashMap<String, Route> routeById = new HashMap<>();
            for (Route route : routeRepository.findAll()) {
                routeById.put(route.getId(), route);
            }

            String line;
            boolean firstLine = true;
            int lineNumber = 0;
            ArrayList<Trip> trips = new ArrayList<>();
            HashMap<String, Integer> headerMap = new HashMap<>();
            int maxIndex = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;

                if (firstLine) {
                    firstLine = false;
                    headerMap = GtfsParserUtils.parseHeader(line);
                    if (!GtfsParserUtils.hasRequiredColumns(headerMap, "trip_id", "route_id", "service_id")) {
                        throw new GtfsImportException("Missing required columns in header");
                    }
                    maxIndex = Collections.max(headerMap.values());
                    continue; //skip header
                }

                String[] fields = line.split(GtfsParserUtils.CSV_SPLIT_REGEX, -1);

                if (fields.length <= maxIndex) {
                    log.warn("Skipping line {}: not enough fields", lineNumber);
                    continue;
                }

                String headsign = headerMap.containsKey("trip_headsign")
                    ? fields[headerMap.get("trip_headsign")].trim()
                    : "";

                Integer directionId = headerMap.containsKey("direction_id")
                    ? GtfsParserUtils.parseInteger(fields[headerMap.get("direction_id")].trim())
                    : null;

                String serviceId = fields[headerMap.get("service_id")].trim();
                Route route = routeById.get(fields[headerMap.get("route_id")].trim());

                if (serviceId.isEmpty() || route == null) {
                    log.warn("Skipping line {}: missing service_id or route not found", lineNumber); 
                    continue;
                }


                Trip trip = new Trip();
                trip.setId(fields[headerMap.get("trip_id")].trim());
                trip.setRoute(route);
                trip.setServiceId(serviceId);
                trip.setHeadsign(headsign);
                trip.setDirectionId(directionId);
                trips.add(trip);
            }

            if (!trips.isEmpty()) {
                tripRepository.saveAll(trips);
                log.info("Imported {} valid trips", trips.size());
            } else {
                log.info("No valid trips to import");
            }
        } catch (IOException e) {
            log.error("Failed to import trips", e);
            throw new GtfsImportException("Failed to import trips", e);
        }
    }

    public void importZip(InputStream zipInputStream) throws IOException {
        GtfsParserUtils.importFromZip(zipInputStream, "trips.txt", this::importTrips);
    }

public Page<Trip> getTrips(Pageable pageable) {
    return tripRepository.findAll(pageable);
}
}