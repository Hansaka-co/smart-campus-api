# Smart Campus Sensor & Room Management API
### 5COSC022W Client-Server Architectures — Coursework 2025/26

A RESTful API built with JAX-RS (Jersey) and Grizzly embedded server for managing Rooms, Sensors, and Sensor Readings across a university smart campus.

---

## Technology Stack
- **Java 21**
- **JAX-RS (Jersey 3.1.6)** — REST framework
- **Grizzly HTTP Server** — Embedded server
- **Jackson** — JSON serialization
- **Maven** — Build tool
- **In-memory storage** — ConcurrentHashMap (no database)

---

## Project Structure
smart-campus-api/
├── src/main/java/com/westminster/
│   ├── Main.java
│   ├── SmartCampusApplication.java
│   ├── model/
│   │   ├── Room.java
│   │   ├── Sensor.java
│   │   └── SensorReading.java
│   ├── resource/
│   │   ├── DiscoveryResource.java
│   │   ├── RoomResource.java
│   │   ├── SensorResource.java
│   │   └── SensorReadingResource.java
│   ├── service/
│   │   └── CampusService.java
│   ├── exception/
│   │   ├── RoomNotEmptyException.java
│   │   ├── RoomNotEmptyExceptionMapper.java
│   │   ├── LinkedResourceNotFoundException.java
│   │   ├── LinkedResourceNotFoundExceptionMapper.java
│   │   ├── SensorUnavailableException.java
│   │   ├── SensorUnavailableExceptionMapper.java
│   │   └── GlobalExceptionMapper.java
│   └── filter/
│       └── LoggingFilter.java
└── pom.xml

---

## How to Build and Run

### Prerequisites
- Java 21+
- Maven 3.9+

### Steps

**1. Clone the repository:**
```bash
git clone https://github.com/Hansaka-co/smart-campus-api.git
cd smart-campus-api
```

**2. Build the project:**
```bash
mvn clean compile
```

**3. Run the server:**
```bash
mvn exec:java
```

**4. Server is now running at:**
http://localhost:8080/api/v1

---
## API Endpoints

| Method | URL | Description |
|--------|-----|-------------|
| GET | /api/v1/discovery | API metadata and resource links |
| GET | /api/v1/rooms | Get all rooms |
| POST | /api/v1/rooms | Create a new room |
| GET | /api/v1/rooms/{roomId} | Get a specific room |
| DELETE | /api/v1/rooms/{roomId} | Delete a room (blocked if has sensors) |
| GET | /api/v1/sensors | Get all sensors (optional ?type= filter) |
| POST | /api/v1/sensors | Create a new sensor |
| GET | /api/v1/sensors/{sensorId} | Get a specific sensor |
| GET | /api/v1/sensors/{sensorId}/readings | Get all readings for a sensor |
| POST | /api/v1/sensors/{sensorId}/readings | Add a new reading |

---

## Sample curl Commands

**1. Get all rooms:**
```bash
curl -X GET http://localhost:8080/api/v1/rooms
```

**2. Create a new room:**
```bash
curl -X POST http://localhost:8080/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"id":"HALL-01","name":"Main Hall","capacity":200}'
```

**3. Get sensors filtered by type:**
```bash
curl -X GET "http://localhost:8080/api/v1/sensors?type=CO2"
```

**4. Add a reading to a sensor:**
```bash
curl -X POST http://localhost:8080/api/v1/sensors/TEMP-001/readings \
  -H "Content-Type: application/json" \
  -d '{"value":25.5}'
```

**5. Try to delete a room with sensors (409 error):**
```bash
curl -X DELETE http://localhost:8080/api/v1/rooms/LIB-301
```

---
