package com.westminster;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import java.net.URI;

public class Main {
    public static void main(String[] args) throws Exception {
        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(
            URI.create("http://0.0.0.0:8080/"),
            new SmartCampusApplication(),
            false);
        server.start();
        System.out.println("Server running: http://localhost:8080/api/v1/rooms");
        System.out.println("Press ENTER to stop...");
        System.in.read();
        server.shutdownNow();
    }
}