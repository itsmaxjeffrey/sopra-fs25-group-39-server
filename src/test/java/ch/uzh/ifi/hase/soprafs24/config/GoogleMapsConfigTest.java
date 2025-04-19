package ch.uzh.ifi.hase.soprafs24.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoogleMapsConfigTest {

    @Mock
    private Environment environment;

    private GoogleMapsConfig googleMapsConfig;
    private static final String VALID_API_KEY = "valid-api-key-that-is-long-enough";
    private static final String SHORT_API_KEY = "too-short";
    private String originalEnvValue;

    @BeforeEach
    void setup() {
        googleMapsConfig = new GoogleMapsConfig(environment);
        // Store original environment variable value
        originalEnvValue = System.getenv(GoogleMapsConfig.GOOGLE_MAPS_API_KEY_NAME);
        // Clear the environment variable for testing
        System.clearProperty(GoogleMapsConfig.GOOGLE_MAPS_API_KEY_NAME);
    }

    @Test
    void testApiKeyFromEnvironmentVariable() {
        // Set up environment variable
        System.setProperty(GoogleMapsConfig.GOOGLE_MAPS_API_KEY_NAME, VALID_API_KEY);
        
        String apiKey = googleMapsConfig.googleMapsApiKey();
        assertNotNull(apiKey);
        assertEquals(VALID_API_KEY, apiKey);
    }

    @Test
    void testApiKeyFromSpringProperties() {
        // Mock environment to return API key
        when(environment.getProperty(GoogleMapsConfig.GOOGLE_MAPS_API_KEY_NAME))
            .thenReturn(VALID_API_KEY);
        
        String apiKey = googleMapsConfig.googleMapsApiKey();
        assertNotNull(apiKey);
        assertEquals(VALID_API_KEY, apiKey);
    }

    @Test
    void testApiKeyFromEnvLocalFile() throws IOException {
        // Mock environment to return null first
        when(environment.getProperty(GoogleMapsConfig.GOOGLE_MAPS_API_KEY_NAME))
            .thenReturn(null);
        
        // Create test properties
        Properties props = new Properties();
        props.setProperty(GoogleMapsConfig.GOOGLE_MAPS_API_KEY_NAME, VALID_API_KEY);
        
        // Mock the static method
        try (MockedStatic<PropertiesLoaderUtils> utilities = mockStatic(PropertiesLoaderUtils.class)) {
            utilities.when(() -> PropertiesLoaderUtils.loadProperties(any(ClassPathResource.class)))
                    .thenReturn(props);
            
            String apiKey = googleMapsConfig.googleMapsApiKey();
            assertNotNull(apiKey);
            assertEquals(VALID_API_KEY, apiKey);
        }
    }

    @Test
    void testApiKeyNotFound() {
        // Mock environment to return null
        when(environment.getProperty(GoogleMapsConfig.GOOGLE_MAPS_API_KEY_NAME))
            .thenReturn(null);
        
        // Clear any system properties
        System.clearProperty(GoogleMapsConfig.GOOGLE_MAPS_API_KEY_NAME);
        
        assertThrows(IllegalStateException.class, () -> {
            googleMapsConfig.googleMapsApiKey();
        });
    }

    @Test
    void testApiKeyTooShort() {
        // Mock environment to return a short API key
        when(environment.getProperty(GoogleMapsConfig.GOOGLE_MAPS_API_KEY_NAME))
            .thenReturn(SHORT_API_KEY);
        
        assertThrows(IllegalStateException.class, () -> {
            googleMapsConfig.googleMapsApiKey();
        });
    }

    @Test
    void testApiKeyEmpty() {
        // Mock environment to return empty string
        when(environment.getProperty(GoogleMapsConfig.GOOGLE_MAPS_API_KEY_NAME))
            .thenReturn("");
        
        assertThrows(IllegalStateException.class, () -> {
            googleMapsConfig.googleMapsApiKey();
        });
    }

    @Test
    void testApiKeyNull() {
        // Mock environment to return null
        when(environment.getProperty(GoogleMapsConfig.GOOGLE_MAPS_API_KEY_NAME))
            .thenReturn(null);
        
        assertThrows(IllegalStateException.class, () -> {
            googleMapsConfig.googleMapsApiKey();
        });
    }

    @Test
    void testEnvLocalFileIOException() throws IOException {
        // Mock environment to return null
        when(environment.getProperty(GoogleMapsConfig.GOOGLE_MAPS_API_KEY_NAME))
            .thenReturn(null);
        
        // Mock the static method to throw IOException
        try (MockedStatic<PropertiesLoaderUtils> utilities = mockStatic(PropertiesLoaderUtils.class)) {
            utilities.when(() -> PropertiesLoaderUtils.loadProperties(any(ClassPathResource.class)))
                    .thenThrow(new IOException("Test exception"));
            
            assertThrows(IllegalStateException.class, () -> {
                googleMapsConfig.googleMapsApiKey();
            });
        }
    }

    @AfterEach
    void cleanup() {
        // Restore original environment variable value
        if (originalEnvValue != null) {
            System.setProperty(GoogleMapsConfig.GOOGLE_MAPS_API_KEY_NAME, originalEnvValue);
        } else {
            System.clearProperty(GoogleMapsConfig.GOOGLE_MAPS_API_KEY_NAME);
        }
    }
} 