# smart-campus-api
Smart Campus Sensor &amp; Room Management REST API . 5COSC022W Coursework
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