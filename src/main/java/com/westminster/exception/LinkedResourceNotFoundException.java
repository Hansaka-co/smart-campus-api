package com.westminster.exception;

public class LinkedResourceNotFoundException extends RuntimeException {
    private final String roomId;

    public LinkedResourceNotFoundException(String roomId) {
        super("Room not found: " + roomId);
        this.roomId = roomId;
    }

    public String getRoomId() {
        return roomId;
    }
}
