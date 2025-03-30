
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.web.SecurityFilterChain;



// @Configuration
// @EnableWebSecurity
// public class SecurityConfig{

//     @Bean
//     public SecurityFilterChain SecurityFilterChain(HttpSecurity http) throws Exception{
//         http
//             .csrf().disbale()
//             .authorizeRequests()
//             .anyRequest().authenticated()
//             .and()
//             .httpBasic();
//         return http.build();
//     }