package com.remotepulse.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(
            ResourceHandlerRegistry registry
    ) {

        String iconLocation =
                Path.of(
                        "data",
                        "icons"
                )
                .toAbsolutePath()
                .toUri()
                .toString();

        registry
                .addResourceHandler(
                        "/icons/**"
                )
                .addResourceLocations(
                        iconLocation
                );
    }
}