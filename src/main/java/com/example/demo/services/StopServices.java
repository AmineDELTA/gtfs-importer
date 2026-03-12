package com.example.demo.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
                    headerMap = parseHeader(line);
                    if (!headerMap.containsKey("stop_id") || !headerMap.containsKey("stop_name") ||
                        !headerMap.containsKey("stop_lat") || !headerMap.containsKey("stop_lon")) {
                        throw new RuntimeException("Missing required columns in header");
                    }
                    maxIndex = Collections.max(headerMap.values());
                    continue; //skip header
                }

                String[] fields = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                if (fields.length <= maxIndex) {
                    log.warn("Skipping line {}: not enough fields", lineNumber);
                    continue;
                }

                Double latitude = parseLatitude(fields[headerMap.get("stop_lat")].trim());
                Double longitude = parseLongitude(fields[headerMap.get("stop_lon")].trim());

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

    private HashMap<String, Integer> parseHeader(String headerLine) {
        HashMap<String, Integer> headerMap = new HashMap<>();
        String[] headers = headerLine.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
        for (int i = 0; i < headers.length; i++){
            headerMap.put(headers[i].trim(), i);
        }
        return headerMap;
    }

    public void importZip(InputStream zipInputStream) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(zipInputStream)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().equalsIgnoreCase("stops.txt")){
                    importStops(zis);
                    break; //stops after processing stops.txt
                }
            }
        }
    }

    private Double parseLatitude(String raw) {
        Double value = parseDouble(raw);
        if (value == null || value < -90.0 || value > 90.0) {
            return null;
        }
        return value;
    }

    private Double parseLongitude(String raw) {
        Double value = parseDouble(raw);
        if (value == null || value < -180.0 || value > 180.0) {
            return null;
        }
        return value;
    }

    private Double parseDouble(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            double value = Double.parseDouble(raw);
            return Double.isFinite(value) ? value : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Page<Stop> getStops(Pageable pageable) {
    return stopRepository.findAll(pageable);
}
}

