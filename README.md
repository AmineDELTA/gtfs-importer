# GTFS Importer — Spring Boot Backend

A Spring Boot backend for importing and querying GTFS (General Transit Feed Specification) transit data. Upload a GTFS zip and the tool parses, validates, and stores stops, routes, trips, and stop times into PostgreSQL.

> **What is GTFS?** The standard format used by transit agencies worldwide to publish schedules and geographic data.

---

## Tech Stack

Java 17 · Spring Boot · Spring Data JPA · PostgreSQL · Maven

---

## Features

- Import a full GTFS zip in one request
- Header-mapping CSV parser — handles any column order
- Validates coordinates, route types, and required fields
- Batched inserts for large stop_times files
- Paginated REST API for querying data

---

## Getting Started

```sh
git clone https://github.com/your-username/gtfs-importer.git
cd gtfs-importer
./mvnw spring-boot:run
```

Configure your database in `src/main/resources/application.properties`.

---

## Getting GTFS Data

- **Sample feed for testing:** https://developers.google.com/static/transit/gtfs/examples/sample-feed.zip
- **Real city feeds:** https://transitfeeds.com or https://mobilitydatabase.org

---

## API Endpoints

### Import

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/gtfs/import` | Import a full GTFS zip |
| POST | `/api/gtfs/import-stops` | Import stops.txt |
| POST | `/api/gtfs/import-routes` | Import routes.txt |
| POST | `/api/gtfs/import-trips` | Import trips.txt |
| POST | `/api/gtfs/import-stop-times` | Import stop_times.txt |

All endpoints accept `multipart/form-data` with a field named `file`.

> ⚠️ When importing individually, order matters: `stops → routes → trips → stop times`

### Query

| Method | Endpoint |
|--------|----------|
| GET | `/api/gtfs/stops?page=0&size=10` |
| GET | `/api/gtfs/routes?page=0&size=10` |
| GET | `/api/gtfs/trips?page=0&size=10` |
| GET | `/api/gtfs/stop-times?page=0&size=10` |

---

## Project Structure

```
src/main/java/com/amine/gtfs/
├── controller/       # REST endpoints
├── model/            # Stop, Route, Trip, StopTime
├── repository/       # JPA repositories
└── services/         # Import logic + GtfsParserUtils
```

---

## Data Model

```
Stop ←── StopTime ──→ Trip ──→ Route
```

---

## License

MIT License