package ch.uzh.ifi.hase.soprafs24.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;

@Configuration
@PropertySource(value = "file:.env.local", ignoreResourceNotFound = true)
public class GoogleMapsConfig {

    @Autowired
    private Environment environment;

    @Bean
    public String googleMapsApiKey() {
        // First try to get from environment variable
        String apiKey = System.getenv("GOOGLE_MAPS_API_KEY");
        
        // If not found in environment, try from properties
        if (apiKey == null) {
            apiKey = environment.getProperty("GOOGLE_MAPS_API_KEY");
        }
        
        if (apiKey == null) {
            throw new IllegalStateException("GOOGLE_MAPS_API_KEY environment variable is not set");
        }
        return apiKey;
    }
} 