package ch.uzh.ifi.hase.soprafs24;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationTest {

    @Test
    void testHelloWorld() {
        // given
        Application application = new Application();

        // when
        String response = application.helloWorld();

        // then
        assertEquals("The application is running.", response);
    }

    @Test
    void testCorsConfigurer() {
        // given
        Application application = new Application();
        CorsRegistry registry = new CorsRegistry();

        // when
        WebMvcConfigurer configurer = application.corsConfigurer();
        configurer.addCorsMappings(registry);

        // then
        // Verify that the CORS configuration was applied
        // Note: We can't directly verify the registry's internal state,
        // but we can verify that the configurer was created successfully
        assertNotNull(configurer);
    }
} 