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
# Smart Campus API – Coursework Report

---

## Part 1 – Setup & Discovery

### Q1: JAX-RS Resource Lifecycle

By default, JAX-RS creates a **new instance** of every Resource class for each incoming HTTP request. This is known as **request-scoped** lifecycle. This means if data were stored directly inside a Resource class, it would be destroyed after every request and all data would be lost.

To solve this, all in-memory data is stored inside `CampusService.java` using the **Singleton pattern**:

```java
private static final CampusService INSTANCE = new CampusService();
public static CampusService getInstance() { return INSTANCE; }
```

This guarantees only one instance of the service exists for the entire lifetime of the application. All Resource classes call `CampusService.getInstance()` to access the same shared data. `ConcurrentHashMap` is used instead of a regular `HashMap` to handle concurrent requests safely, preventing race conditions where two requests might read or write data at the same time and cause data corruption or loss.

---

### Q2: Hypermedia (HATEOAS)

HATEOAS (Hypermedia as the Engine of Application State) is the principle that API responses should include links to related resources, allowing clients to navigate the entire API from a single entry point without needing hardcoded URLs.

For example, the Discovery endpoint in this API returns:
```json
{
  "version": "1.0",
  "name": "Smart Campus Sensor & Room Management API",
  "resources": {
    "rooms": "/api/v1/rooms",
    "sensors": "/api/v1/sensors"
  }
}
```

This benefits client developers in several ways. First, they only need to know one URL — the discovery endpoint. All other URLs are discovered dynamically at runtime. Second, if the API changes its URL structure in the future, clients do not break because they follow links from responses rather than relying on hardcoded paths from static documentation. Third, it makes the API truly self-documenting, reducing the dependency on external documentation and making integration faster and less error-prone.

---

## Part 2 – Room Management

### Q1: Returning IDs vs Full Objects

When returning a list of rooms, there are two approaches — returning only the IDs or returning full room objects.

Returning **only IDs** significantly reduces network bandwidth because the payload is very small. However, the client must then make a separate GET request for each room ID to fetch the full details, increasing the number of network round trips and adding latency.

Returning **full objects** increases the response payload size but gives the client everything it needs in a single request, which is faster for read-heavy operations and simpler to implement on the client side.

In this implementation, full room objects are returned in the list because campus facility managers need to see room details immediately. For very large systems with thousands of rooms, pagination and returning only IDs would be recommended to balance network bandwidth with usability.

---

### Q2: Is DELETE idempotent?

Yes, the DELETE operation is **idempotent** in this implementation. Idempotency means that making the same request multiple times produces the same server state as making it once.

In this API, if a client sends `DELETE /api/v1/rooms/HALL-01`:
- **First call** — the room exists, it is deleted, the server returns `204 No Content`
- **Second call** — the room no longer exists, the server returns `404 Not Found`
- **Third call** — same result as the second call, `404 Not Found`

Although the HTTP status codes differ between the first and subsequent calls, the **server state is identical** after each call — the room does not exist. This satisfies the definition of idempotency. This behaviour is standard in REST design and protects against issues where a client accidentally sends the same DELETE request multiple times due to network retries.

---

## Part 3 – Sensor Operations

### Q1: @Consumes JSON vs Other Formats

When `@Consumes(MediaType.APPLICATION_JSON)` is declared on a POST method, JAX-RS uses **content negotiation** to match the incoming request format against what the method accepts. If a client sends a request with `Content-Type: text/plain` or `Content-Type: application/xml`, JAX-RS cannot find a matching resource method for that content type.

In this case, JAX-RS automatically returns `415 Unsupported Media Type` before the method body is even executed. The framework intercepts the mismatch at the request processing level, meaning the application code is never reached. This enforces a strict data format contract — the API only accepts JSON — and prevents malformed or unexpected data from causing unpredictable behaviour inside the application logic. The client receives a clear and specific error code that tells them exactly what format is required.

---

### Q2: QueryParam vs PathParam

Using `@QueryParam` for filtering (e.g., `GET /sensors?type=CO2`) is considered superior to embedding the type in the URL path (e.g., `GET /sensors/type/CO2`) for several important reasons.

**Semantic clarity** — Path parameters are intended to identify a specific resource (e.g., `/sensors/TEMP-001` identifies one sensor). Query parameters represent optional search or filter criteria applied to a collection. Mixing these concerns makes the API harder to understand.

**Optionality** — Query parameters are naturally optional. `GET /sensors` returns all sensors while `GET /sensors?type=CO2` filters them. With path parameters you would need two separate endpoints to support both behaviours.

**Multiple filters** — Query parameters scale naturally to multiple criteria, for example `?type=CO2&status=ACTIVE`. Path-based filtering becomes deeply nested and unmanageable with more than one filter condition.

**REST convention** — REST best practices clearly state that path parameters identify resources while query parameters refine or filter how those resources are retrieved.

---

## Part 4 – Sub-Resources

### Q1: Sub-resource locator benefits

The Sub-Resource Locator pattern delegates request handling for a nested path to a completely separate class rather than defining all nested paths inside one large controller. In this API, `SensorResource` delegates reading management to `SensorReadingResource`:

```java
@Path("/{sensorId}/readings")
public SensorReadingResource getReadingResource(@PathParam("sensorId") String sensorId) {
    return new SensorReadingResource(sensorId);
}
```

This approach provides several architectural benefits.

**Separation of concerns** — Each class has one clear responsibility. `SensorResource` manages sensors and `SensorReadingResource` manages readings. This follows the Single Responsibility Principle and makes each class easier to understand and maintain independently.

**Reduced complexity** — In a large API with many nested resources, placing all logic in one controller class would result in hundreds of methods and thousands of lines of code, making it very difficult to navigate and maintain. Separate classes keep each component small and focused.

**Reusability** — `SensorReadingResource` is instantiated with a specific sensor ID, making it reusable and independent of how it is invoked.

**Scalability** — Adding new operations for readings only requires changes to `SensorReadingResource` with zero impact on `SensorResource`, reducing the risk of introducing bugs when extending the API.

---

## Part 5 – Error Handling

### Q1: Why 422 instead of 404?

`404 Not Found` means the **requested URL or endpoint** does not exist. It is the correct response when a client requests `GET /rooms/FAKE-ID` and that specific room cannot be found at that URL.

`422 Unprocessable Entity` is more semantically accurate when the **request URL is valid** but the **content inside the JSON payload** contains an invalid reference. For example, when creating a new sensor with `"roomId": "FAKE-ROOM"`, the endpoint `/api/v1/sensors` exists and is perfectly valid. The problem is not the URL — it is that the request body references a room that does not exist in the system.

Using `422` communicates precisely: "I understood your request, I found your endpoint, I parsed your JSON successfully, but I cannot process this data because it contains a reference to a non-existent resource." This gives the client developer much more specific feedback and avoids confusion between a missing endpoint (404) and a missing data reference inside a valid request (422).

---

### Q2: Risks of Exposing Stack Traces

Exposing raw Java stack traces to external API consumers is a serious cybersecurity vulnerability for several reasons.

**Internal path disclosure** — Stack traces reveal the full file system paths and package structure of the application (e.g., `com.westminster.service.CampusService.java:45`), exposing the internal architecture to potential attackers.

**Library and version exposure** — Stack traces show exact library names and version numbers (e.g., `jersey-server-3.1.6`, `jackson-databind-2.17.2`). Attackers can search public vulnerability databases (CVE databases) for known exploits targeting those specific versions.

**Application logic disclosure** — Stack traces reveal method names, class names, and line numbers, effectively giving an attacker a detailed map of the application's internal logic and data flow, making it much easier to craft targeted attacks.

**Framework fingerprinting** — Knowing the exact framework stack (Jersey, Grizzly, Jackson) helps attackers narrow down attack vectors and known weaknesses specific to those technologies.

The `GlobalExceptionMapper` in this implementation prevents all of this by intercepting every unexpected exception and returning only a generic `500 Internal Server Error` with a safe message, keeping all internal technical details completely hidden from external consumers.

---

### Q3: Why Use Filters for Logging?

Using JAX-RS filters for cross-cutting concerns like logging is far superior to manually inserting `Logger.info()` statements inside every resource method for several reasons.

**No code duplication** — A single filter class automatically intercepts every request and response across the entire API. Without filters, the same logging code would need to be copy-pasted into every single resource method, violating the DRY (Don't Repeat Yourself) principle.

**Separation of concerns** — Logging is a cross-cutting concern that has nothing to do with business logic. Filters keep logging completely separate from the actual resource methods, making both easier to read and maintain.

**Consistency** — A filter guarantees that every single request and response is logged without exception. With manual logging, developers might forget to add log statements to new methods, creating gaps in observability.

**Easy to enable or disable** — A logging filter can be enabled or disabled in one place without touching any business logic code.

**Maintainability** — If the logging format needs to change, only the filter class needs to be updated, not every resource method across the entire codebase.

---

## Video Demonstration
[Video link — to be added after recording]

---

## GitHub Repository
[https://github.com/Hansaka-co/smart-campus-api](https://github.com/Hansaka-co/smart-campus-api)
