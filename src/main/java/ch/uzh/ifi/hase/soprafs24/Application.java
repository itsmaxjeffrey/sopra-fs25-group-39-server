package ch.uzh.ifi.hase.soprafs24;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RestController
@SpringBootApplication
@EnableScheduling
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @GetMapping(value = "/", produces = MediaType.TEXT_PLAIN_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public String helloWorld() {
    return "The application is running.";
  }

  @Bean
  public WebMvcConfigurer corsConfigurer() {
      return new WebMvcConfigurer() {
          @Override
          public void addCorsMappings(@NonNull CorsRegistry registry) {
              registry.addMapping("/**")
                  .allowedOrigins(
                      "http://localhost:3000", // Development frontend
                      "https://sopra-fs25-group-39-client.vercel.app" // Production frontend
                  )      
                  .allowedMethods("GET","POST","PUT","DELETE","OPTIONS")
                  .allowedHeaders("*")
                  .allowCredentials(true);                       // <-- enable cookies/withCredentials
          }
      };
  }
}
