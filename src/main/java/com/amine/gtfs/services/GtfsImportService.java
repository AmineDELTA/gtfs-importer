package com.amine.gtfs.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.springframework.stereotype.Service;

@Service
public class GtfsImportService {
    private final StopServices stopServices;
    private final RouteServices routeServices;
    private final TripServices tripServices;
    private final StopTimeServices stopTimeServices;

    public GtfsImportService(StopServices stopServices, RouteServices routeServices, TripServices tripServices, StopTimeServices stopTimeServices) {
        this.stopServices = stopServices;
        this.routeServices = routeServices;
        this.tripServices = tripServices;
        this.stopTimeServices = stopTimeServices;
    }

    public void importGtfs(InputStream zipInputStream) throws IOException {
        HashMap<String, byte[]> gtfsData = new HashMap<>();
        try (ZipInputStream zis = new ZipInputStream(zipInputStream)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    String name = entry.getName().toLowerCase();
                    if (name.equals("stops.txt") || name.equals("routes.txt") || name.equals("trips.txt") || name.equals("stop_times.txt")) {
                    gtfsData.put(name, zis.readAllBytes());
                    }
                }
                zis.closeEntry();
            }
        }

        if (gtfsData.containsKey("stops.txt")) {
            try (InputStream stopsStream = new ByteArrayInputStream(gtfsData.get("stops.txt"))) {
                this.stopServices.importStops(stopsStream);
            }
        }

        if (gtfsData.containsKey("routes.txt")) {
            try (InputStream routesStream = new ByteArrayInputStream(gtfsData.get("routes.txt"))) {
                this.routeServices.importRoutes(routesStream);
            }
        }

        if (gtfsData.containsKey("trips.txt")) {
            try (InputStream tripsStream = new ByteArrayInputStream(gtfsData.get("trips.txt"))) {
                this.tripServices.importTrips(tripsStream);
            }
        }

        if (gtfsData.containsKey("stop_times.txt")) {
            try (InputStream stopTimesStream = new ByteArrayInputStream(gtfsData.get("stop_times.txt"))) {
                this.stopTimeServices.importStopTimes(stopTimesStream);
            }
        }
    }
}