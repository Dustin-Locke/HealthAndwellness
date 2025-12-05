package edu.fscj.cen4940.capstone.auth;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import edu.fscj.cen4940.capstone.jwt.filters.JwtRequestFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private AuthFailHandler failureHandler;
    @Autowired
    private AuthSuccessHandler successHandler;
    private final JwtRequestFilter jwtFilter;

    public SecurityConfig(JwtRequestFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> {})
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(reg -> reg
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/**",
                                         "/actuator/**",
                                         "/v3/api-docs/**",
                                         "/swagger-ui/**",
                                         "localhost:8080/swagger-ui/index.html",
                                         "/api/auth/pre-register",
                                         "/api/auth/complete-registration",
                                         "/api/auth/verify-email",
                                         "api/auth/send-verification-email",
                                         "/api/auth/verify-code",
                                         "/api/auth/forgot-password",
                                         "/api/auth/verify-reset-code",
                                         "/api/auth/reset-password"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(customizer ->
                        customizer
                                .failureHandler(failureHandler)
                                .successHandler(successHandler)
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization","Content-Type","X-Requested-With"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}