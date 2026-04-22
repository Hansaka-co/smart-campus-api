package com.westminster.resource;

import com.westminster.exception.RoomNotEmptyException;
import com.westminster.model.Room;
import com.westminster.service.CampusService;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Collection;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    private final CampusService service = CampusService.getInstance();

    // GET /api/v1/rooms — get all rooms
    @GET
    public Response getAllRooms() {
        Collection<Room> rooms = service.getAllRooms();
        return Response.ok(rooms).build();
    }

    // POST /api/v1/rooms — create a new room
    @POST
    public Response createRoom(Room room) {
        if (room.getId() == null || room.getId().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Room ID is required\"}")
                    .build();
        }
        if (service.roomExists(room.getId())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\":\"Room with this ID already exists\"}")
                    .build();
        }
        service.addRoom(room);
        return Response.status(Response.Status.CREATED)
                .entity(room)
                .build();
    }

    // GET /api/v1/rooms/{roomId} — get one room
    @GET
    @Path("/{roomId}")
    public Response getRoomById(@PathParam("roomId") String roomId) {
        Room room = service.getRoomById(roomId);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Room not found: " + roomId + "\"}")
                    .build();
        }
        return Response.ok(room).build();
    }

    // DELETE /api/v1/rooms/{roomId} — delete a room
    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = service.getRoomById(roomId);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Room not found: " + roomId + "\"}")
                    .build();
        }
        // Business logic: cannot delete room if it has sensors
        if (!room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException(roomId);
        }
        service.deleteRoom(roomId);
        return Response.noContent().build();
    }
}