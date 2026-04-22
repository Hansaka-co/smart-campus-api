package com.westminster.exception;

public class SensorUnavailableException extends RuntimeException {
    private final String sensorId;

    public SensorUnavailableException(String sensorId) {
        super("Sensor " + sensorId + " is currently under MAINTENANCE.");
        this.sensorId = sensorId;
    }

    public String getSensorId() {
        return sensorId;
    }
}