package com.example.demo.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class GtfsParserUtils {
        private GtfsParserUtils() {
        }

    @FunctionalInterface
    public interface ZipEntryImporter {
        void importEntry(InputStream inputStream) throws IOException;
    }

        public static final String CSV_SPLIT_REGEX = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";

        public static HashMap<String, Integer> parseHeader(String headerLine) {
            HashMap<String, Integer> headerMap = new HashMap<>();
            if (headerLine == null || headerLine.isBlank()) {
                return headerMap;
            }

            String[] headers = headerLine.split(CSV_SPLIT_REGEX, -1);
            for (int i = 0; i < headers.length; i++) {
                headerMap.put(headers[i].trim(), i);
            }
            return headerMap;
        }

        public static boolean hasRequiredColumns(Map<String, Integer> headerMap, String... requiredColumns) {
            if (headerMap == null || requiredColumns == null) {
                return false;
            }

            for (String column : requiredColumns) {
                if (!headerMap.containsKey(column)) {
                    return false;
                }
            }
            return true;
        }

        public static Integer parseType(String typeStr) {
            Integer type = parseInteger(typeStr);
            if (type == null || !isValidGtfsRouteType(type)) {
                return null;
            }
            return type;
        }

        public static boolean isValidGtfsRouteType(int type) {
            return (type >= 0 && type <= 7) || type == 11 || type == 12;
        }

        public static Integer parseInteger(String intStr) {
            if (intStr == null || intStr.isBlank()) {
                return null;
            }

            try {
                return Integer.parseInt(intStr.trim());
            } catch (NumberFormatException e) {
                return null;
            }
        }

        public static Double parseDouble(String doubleStr) {
            if (doubleStr == null || doubleStr.isBlank()) {
                return null;
            }

            try {
                double value = Double.parseDouble(doubleStr.trim());
                return Double.isFinite(value) ? value : null;
            } catch (NumberFormatException e) {
                return null;
            }
        }

        public static Double parseLatitude(String latStr) {
            Double lat = parseDouble(latStr);
            if (lat == null || lat < -90.0 || lat > 90.0) {
                return null;
            }
            return lat;
        }

        public static Double parseLongitude(String lonStr) {
            Double lon = parseDouble(lonStr);
            if (lon == null || lon < -180.0 || lon > 180.0) {
                return null;
            }
            return lon;
        }

        public static void importFromZip(InputStream zipInputStream, String targetEntry, ZipEntryImporter importer)
                throws IOException {
            try (ZipInputStream zis = new ZipInputStream(zipInputStream)) {
                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    if (entry.getName().equalsIgnoreCase(targetEntry)) {
                        importer.importEntry(zis);
                        break;
                    }
                }
            }
        }
    }
