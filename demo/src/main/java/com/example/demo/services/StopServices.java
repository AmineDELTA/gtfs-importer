package com.example.demo.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.demo.model.Stop;
import com.example.demo.repository.StopRepository;

@Service
public class StopServices {
    private static final Logger log = LoggerFactory.getLogger(StopServices.class);
    private final StopRepository stopRepository;

    public StopServices(StopRepository stopRepository) {
        this.stopRepository = stopRepository;
    }

    public void importStops(InputStream inputStream) {
        try (BufferedReader reader =
                new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            String line;
            boolean firstLine = true;
            int lineNumber = 0;
            ArrayList<Stop> stops = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                if (firstLine) {
                    firstLine = false;
                    continue; // skip header
                }

                String[] fields = line.split(",");
                if (fields.length < 4) {
                    log.warn("Skipping line {}: expected at least 4 fields", lineNumber);
                    continue;
                }

                Double latitude = parseLatitude(fields[2].trim());
                Double longitude = parseLongitude(fields[3].trim());

                if (latitude == null || longitude == null) {
                    log.warn("Skipping line {}: invalid coordinates lat='{}', lon='{}'",
                            lineNumber, fields[2], fields[3]);
                    continue;
                }

                Stop stop = new Stop();
                stop.setId(fields[0].trim());
                stop.setName(fields[1].trim());
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
        try (ZipInputStream zis = new ZipInputStream(zipInputStream)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().equalsIgnoreCase("stops.txt")){
                    importStops(zis);
                    break; //stop after processing stops.txt
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
}