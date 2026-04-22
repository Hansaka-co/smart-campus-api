package com.westminster;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;

public class Main {

    public static final String BASE_URI = "http://0.0.0.0:8080/";

    public static void main(String[] args) throws Exception {
        final ResourceConfig config = new ResourceConfig()
                .packages("com.westminster.resource",
                          "com.westminster.exception",
                          "com.westminster.filter")
                .register(JacksonFeature.class);

        final HttpServer server = GrizzlyHttpServerFactory
                .createHttpServer(URI.create(BASE_URI), config);

        System.out.println("====================================");
        System.out.println(" Smart Campus API is RUNNING!");
        System.out.println(" URL: http://localhost:8080/api/v1");
        System.out.println(" Press ENTER to stop...");
        System.out.println("====================================");
        System.in.read();
        server.shutdownNow();
    }
}