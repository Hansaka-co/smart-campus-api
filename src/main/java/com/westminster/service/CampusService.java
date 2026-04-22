package com.westminster.service;

import com.westminster.model.Room;
import com.westminster.model.Sensor;
import com.westminster.model.SensorReading;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CampusService {

    private static final CampusService INSTANCE = new CampusService();

    private final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private final Map<String, Sensor> sensors = new ConcurrentHashMap<>();
    private final Map<String, List<SensorReading>> readings = new ConcurrentHashMap<>();

    private CampusService() {
        // Sample data
        Room r1 = new Room("LIB-301", "Library Quiet Study", 50);
        Room r2 = new Room("LAB-101", "Computer Lab", 30);
        rooms.put(r1.getId(), r1);
        rooms.put(r2.getId(), r2);

        Sensor s1 = new Sensor("TEMP-001", "Temperature", "ACTIVE", 22.5, "LIB-301");
        Sensor s2 = new Sensor("CO2-001", "CO2", "ACTIVE", 400.0, "LAB-101");
        Sensor s3 = new Sensor("OCC-001", "Occupancy", "MAINTENANCE", 0.0, "LIB-301");
        sensors.put(s1.getId(), s1);
        sensors.put(s2.getId(), s2);
        sensors.put(s3.getId(), s3);

        r1.addSensorId(s1.getId());
        r1.addSensorId(s3.getId());
        r2.addSensorId(s2.getId());

        readings.put(s1.getId(), new ArrayList<>());
        readings.put(s2.getId(), new ArrayList<>());
        readings.put(s3.getId(), new ArrayList<>());
    }

    public static CampusService getInstance() { return INSTANCE; }

    // --- Rooms ---
    public Collection<Room> getAllRooms() { return rooms.values(); }
    public Room getRoomById(String id) { return rooms.get(id); }
    public void addRoom(Room room) { rooms.put(room.getId(), room); }
    public boolean roomExists(String id) { return rooms.containsKey(id); }
    public boolean deleteRoom(String id) {
        if (!rooms.containsKey(id)) return false;
        rooms.remove(id);
        return true;
    }

    // --- Sensors ---
    public Collection<Sensor> getAllSensors() { return sensors.values(); }
    public Sensor getSensorById(String id) { return sensors.get(id); }
    public boolean sensorExists(String id) { return sensors.containsKey(id); }
    public void addSensor(Sensor sensor) {
        sensors.put(sensor.getId(), sensor);
        readings.put(sensor.getId(), new ArrayList<>());
        Room room = rooms.get(sensor.getRoomId());
        if (room != null) room.addSensorId(sensor.getId());
    }

    // --- Readings ---
    public List<SensorReading> getReadings(String sensorId) {
        return readings.getOrDefault(sensorId, new ArrayList<>());
    }
    public void addReading(String sensorId, SensorReading reading) {
        readings.computeIfAbsent(sensorId, k -> new ArrayList<>()).add(reading);
        Sensor sensor = sensors.get(sensorId);
        if (sensor != null) sensor.setCurrentValue(reading.getValue());
    }
}
