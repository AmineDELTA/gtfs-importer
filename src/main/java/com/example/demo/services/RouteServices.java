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

import com.example.demo.model.Route;
import com.example.demo.repository.RouteRepository;

import jakarta.transaction.Transactional;
@Service
public class RouteServices {
    private static final Logger log = LoggerFactory.getLogger(RouteServices.class);
    private final RouteRepository routeRepository;

    public RouteServices(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    @Transactional
    public void importRoutes(InputStream inputStream) {
        try (BufferedReader reader =
                new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            String line;
            boolean firstLine = true;
            int lineNumber = 0;
            ArrayList<Route> routes = new ArrayList<>();
            HashMap<String, Integer> headerMap = new HashMap<>();
            int maxIndex = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;

                if (firstLine) {
                    firstLine = false;
                    headerMap = GtfsParserUtils.parseHeader(line);
                    if (!GtfsParserUtils.hasRequiredColumns(headerMap, "route_id", "route_type") ||
                        (!headerMap.containsKey("route_short_name") && !headerMap.containsKey("route_long_name"))) {
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

                String shortName = headerMap.containsKey("route_short_name")
                    ? fields[headerMap.get("route_short_name")].trim()
                    : "";
                String longName = headerMap.containsKey("route_long_name")
                    ? fields[headerMap.get("route_long_name")].trim()
                    : "";

                if (shortName.isEmpty() && longName.isEmpty()) {
                    log.warn("Skipping line {}: missing both route names", lineNumber);
                    continue;
                }

                Integer type = GtfsParserUtils.parseType(fields[headerMap.get("route_type")].trim());

                if (type == null) {
                    log.warn("Skipping line {}: invalid route type", lineNumber);
                    continue;
                }

                Route route = new Route();
                route.setId(fields[headerMap.get("route_id")].trim());
                route.setType(type);
                route.setShortName(shortName);
                route.setLongName(longName);
                routes.add(route);
            }

            if (!routes.isEmpty()) {
                routeRepository.saveAll(routes);
                log.info("Imported {} valid routes", routes.size());
            } else {
                log.info("No valid routes to import");
            }
        } catch (IOException e) {
            log.error("Failed to import routes", e);
            throw new RuntimeException("Failed to import routes", e);
        }
    }

    public void importZip(InputStream zipInputStream) throws IOException {
        GtfsParserUtils.importFromZip(zipInputStream, "routes.txt", this::importRoutes);
    }

public Page<Route> getRoutes(Pageable pageable) {
    return routeRepository.findAll(pageable);
}
}

