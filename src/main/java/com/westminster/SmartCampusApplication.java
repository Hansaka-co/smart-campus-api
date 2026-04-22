package com.westminster;

import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("/api/v1")
public class SmartCampusApplication extends ResourceConfig {

    public SmartCampusApplication() {
        packages("com.westminster.resource");
        packages("com.westminster.exception");
        packages("com.westminster.filter");
    }
}