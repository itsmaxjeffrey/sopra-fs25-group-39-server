package ch.uzh.ifi.hase.soprafs24.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

@Configuration
@PropertySource(value = "file:.env.local", ignoreResourceNotFound = true)
public class GoogleMapsConfig {

    private static final Logger log = LoggerFactory.getLogger(GoogleMapsConfig.class);

    @Autowired
    private Environment environment;

    @Bean
    public String googleMapsApiKey() {
        // First try to get from environment variable
        String apiKey = System.getenv("GOOGLE_MAPS_API_KEY");
        log.info("Environment variable GOOGLE_MAPS_API_KEY: {}", apiKey != null ? "***" : "null");
        
        // If not found in environment, try from properties
        if (apiKey == null) {
            apiKey = environment.getProperty("GOOGLE_MAPS_API_KEY");
            log.info("Property GOOGLE_MAPS_API_KEY: {}", apiKey != null ? "***" : "null");
        }
        
        if (apiKey == null) {
            log.error("GOOGLE_MAPS_API_KEY not found in environment or properties");
            throw new IllegalStateException("GOOGLE_MAPS_API_KEY environment variable is not set");
        }
        
        log.info("Successfully loaded Google Maps API key");
        return apiKey;
    }
} 