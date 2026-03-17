package com.example.demo.services;

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

import com.example.demo.model.Stop;
import com.example.demo.repository.StopRepository;

import jakarta.transaction.Transactional;

@Service
public class StopServices {
    private static final Logger log = LoggerFactory.getLogger(StopServices.class);
    private final StopRepository stopRepository;

    public StopServices(StopRepository stopRepository) {
        this.stopRepository = stopRepository;
    }

    @Transactional
    public void importStops(InputStream inputStream) {
        try (BufferedReader reader =
                new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            String line;
            boolean firstLine = true;
            int lineNumber = 0;
            ArrayList<Stop> stops = new ArrayList<>();
            HashMap<String, Integer> headerMap = new HashMap<>();
            int maxIndex = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;

                if (firstLine) {
                    firstLine = false;
                    headerMap = GtfsParserUtils.parseHeader(line);
                    if (!GtfsParserUtils.hasRequiredColumns(headerMap, "stop_id", "stop_name", "stop_lat", "stop_lon")) {
                        throw new RuntimeException("Missing required columns in header");
                    }
                    maxIndex = Collections.max(headerMap.values());
                    continue; //skip header
                }

                String[] fields = line.split(GtfsParserUtils.CSV_SPLIT_REGEX, -1);

                if (fields.length <= maxIndex) {
                    log.warn("Skipping line {}: not enough fields", lineNumber);
                    continue;
                }

                Double latitude = GtfsParserUtils.parseLatitude(fields[headerMap.get("stop_lat")].trim());
                Double longitude = GtfsParserUtils.parseLongitude(fields[headerMap.get("stop_lon")].trim());

                if (latitude == null || longitude == null) {
                    log.warn("Skipping line {}: invalid coordinates lat='{}', lon='{}'",
                            lineNumber, fields[headerMap.get("stop_lat")], fields[headerMap.get("stop_lon")]);
                    continue;
                }

                Stop stop = new Stop();
                stop.setId(fields[headerMap.get("stop_id")].trim());
                stop.setName(fields[headerMap.get("stop_name")].trim());
                stop.setLatitude(latitude);
                stop.setLongitude(longitude);
                stops.add(stop);
            }

            if (!stops.isEmpty()) {
                stopRepository.saveAll(stops);
                log.info("Imported {} valid stops", stops.size());
            } else {
                log.info("No valid stops to import");
            }
        } catch (IOException e) {
            log.error("Failed to import stops", e);
            throw new RuntimeException("Failed to import stops", e);
        }
    }

    public void importZip(InputStream zipInputStream) throws IOException {
        GtfsParserUtils.importFromZip(zipInputStream, "stops.txt", this::importStops);
    }

    public Page<Stop> getStops(Pageable pageable) {
    return stopRepository.findAll(pageable);
}
}

