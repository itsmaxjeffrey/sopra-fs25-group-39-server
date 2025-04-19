package ch.uzh.ifi.hase.soprafs24.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
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
    public static final String GOOGLE_MAPS_API_KEY_NAME = "GOOGLE_MAPS_API_KEY";
    
    private final Environment environment;

    public GoogleMapsConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public String googleMapsApiKey() {
        log.info("Starting Google Maps API key initialization...");
        
        // First try to get from environment variable (GitHub Actions secret)
        String apiKey = System.getenv(GOOGLE_MAPS_API_KEY_NAME);
        log.info("Environment variable {}: {}", GOOGLE_MAPS_API_KEY_NAME, apiKey != null ? "***" : "null");
        
        // If not found in environment, try from system properties
        if (apiKey == null) {
            log.info("API key not found in environment variables, checking system properties...");
            apiKey = System.getProperty(GOOGLE_MAPS_API_KEY_NAME);
            log.info("System property {}: {}", GOOGLE_MAPS_API_KEY_NAME, apiKey != null ? "***" : "null");
        }
        
        // If not found in system properties, try from Spring properties
        if (apiKey == null) {
            log.info("API key not found in system properties, checking Spring properties...");
            apiKey = environment.getProperty(GOOGLE_MAPS_API_KEY_NAME);
            log.info("Spring property {}: {}", GOOGLE_MAPS_API_KEY_NAME, apiKey != null ? "***" : "null");
        }
        
        // If still not found, try from .env.local file (for local development)
        if (apiKey == null) {
            log.info("API key not found in properties, checking .env.local file...");
            try {
                Properties props = PropertiesLoaderUtils.loadProperties(new ClassPathResource(".env.local"));
                apiKey = props.getProperty(GOOGLE_MAPS_API_KEY_NAME);
                log.info("Found API key in .env.local file: {}", apiKey != null ? "***" : "null");
            } catch (IOException e) {
                log.warn("Could not load .env.local file: {}", e.getMessage());
            }
        }
        
        // Validate the API key
        if (apiKey == null || apiKey.trim().isEmpty()) {
            log.error("{} is not set or empty", GOOGLE_MAPS_API_KEY_NAME);
            throw new IllegalStateException(GOOGLE_MAPS_API_KEY_NAME + " environment variable is not set or empty");
        }
        
        if (apiKey.length() < 20) {
            log.error("{} appears to be invalid (too short)", GOOGLE_MAPS_API_KEY_NAME);
            throw new IllegalStateException(GOOGLE_MAPS_API_KEY_NAME + " appears to be invalid");
        }
        
        log.info("Successfully loaded Google Maps API key");
        return apiKey;
    }
} 