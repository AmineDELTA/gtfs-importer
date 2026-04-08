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
import com.amine.gtfs.model.Stop;
import com.amine.gtfs.model.StopTime;
import com.amine.gtfs.model.Trip;
import com.amine.gtfs.repository.StopRepository;
import com.amine.gtfs.repository.StopTimeRepository;
import com.amine.gtfs.repository.TripRepository;

import jakarta.transaction.Transactional;

@Service
public class StopTimeService {
    private static final Logger log = LoggerFactory.getLogger(StopTimeService.class);
    private static final int BATCH_SIZE = 1000;
    private final StopTimeRepository stopTimeRepository;
    private final TripRepository tripRepository;
    private final StopRepository stopRepository;

    public StopTimeService(StopTimeRepository stopTimeRepository, TripRepository tripRepository, StopRepository stopRepository) {
        this.stopTimeRepository = stopTimeRepository;
        this.tripRepository = tripRepository;
        this.stopRepository = stopRepository;
    }

    @Transactional
    public void importStopTimes(InputStream inputStream) {
        try (BufferedReader reader =
                new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            HashMap<String, Trip> tripById = new HashMap<>();
            for (Trip trip : tripRepository.findAll()) {
                tripById.put(trip.getId(), trip);
            }

            HashMap<String, Stop> stopById = new HashMap<>();
            for (Stop stop : stopRepository.findAll()) {
                stopById.put(stop.getId(), stop);
            }

            String line;
            boolean firstLine = true;
            int lineNumber = 0;
            int importedCount = 0;
            ArrayList<StopTime> stopTimes = new ArrayList<>();
            HashMap<String, Integer> headerMap = new HashMap<>();
            int maxIndex = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;

                if (firstLine) {
                    firstLine = false;
                    headerMap = GtfsParserUtils.parseHeader(line);
                    if (!GtfsParserUtils.hasRequiredColumns(headerMap, "trip_id", "stop_id", "arrival_time", "departure_time", "stop_sequence")) {
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

                String arrivalTime = headerMap.containsKey("arrival_time")
                    ? fields[headerMap.get("arrival_time")].trim()
                    : "";

                String departureTime = headerMap.containsKey("departure_time")
                    ? fields[headerMap.get("departure_time")].trim()
                    : "";

                Integer stopSequence = headerMap.containsKey("stop_sequence")
                    ? GtfsParserUtils.parseInteger(fields[headerMap.get("stop_sequence")].trim())
                    : null;
                    
                if (stopSequence == null || stopSequence <= 0) {
                    log.warn("Skipping line {}: invalid stop sequence '{}'", lineNumber, fields[headerMap.get("stop_sequence")]);
                    continue;
                }

                String tripId = fields[headerMap.get("trip_id")].trim();
                String stopId = fields[headerMap.get("stop_id")].trim();

                Trip trip = tripById.get(tripId);
                if (trip == null) {
                    log.warn("Skipping line {}: trip not found", lineNumber);
                    continue;
                }

                Stop stop = stopById.get(stopId);
                if (stop == null) {
                    log.warn("Skipping line {}: stop not found", lineNumber);
                    continue;
                }

                StopTime stopTime = new StopTime();
                stopTime.setTrip(trip);
                stopTime.setStop(stop);
                stopTime.setArrivalTime(arrivalTime);
                stopTime.setDepartureTime(departureTime);
                stopTime.setStopSequence(stopSequence);
                stopTimes.add(stopTime);

                if (stopTimes.size() >= BATCH_SIZE) {
                    stopTimeRepository.saveAll(stopTimes);
                    importedCount += stopTimes.size();
                    stopTimes.clear();
                }
            }

            if (!stopTimes.isEmpty()) {
                stopTimeRepository.saveAll(stopTimes);
                importedCount += stopTimes.size();
                stopTimes.clear();
            }

            if (importedCount > 0) {
                log.info("Imported {} valid stop times", importedCount);
            } else {
                log.info("No valid stop times to import");
            }
        } catch (IOException e) {
            log.error("Failed to import stop times", e);
            throw new GtfsImportException("Failed to import stop times", e);
        }
    }

    public void importZip(InputStream zipInputStream) throws IOException {
        GtfsParserUtils.importFromZip(zipInputStream, "stop_times.txt", this::importStopTimes);
    }

public Page<StopTime> getStopTimes(Pageable pageable) {
    return stopTimeRepository.findAll(pageable);
}
}