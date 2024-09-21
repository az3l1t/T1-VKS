package net.az3l1t.authentication_server.config;

import net.az3l1t.authentication_server.filters.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfiguration(UserDetailsService userDetailsService, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                //Needs to be enabled in production
                .csrf(AbstractHttpConfigurer::disable)
                //Cors - enabled
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Настройка CORS
                .authorizeHttpRequests(
                        req->req
                                .requestMatchers("/api/v1/auth/register",
                                        "/api/v1/auth/getEmail",
                                        "/api/v1/auth/login",
                                        "/api/v1/auth/getBoolean",
                                        "/api/v1/auth/changeTheBooleanToFalse",
                                        "/api/v1/auth/changeTheBooleanToTrue",
                                        "/api/v1/auth/changeTheEmail",
                                        "/api/v1/auth/changeTheFirstname",
                                        "/api/v1/auth/changeTheLastname",
                                        "/api/v1/auth/changeThePassword",
                                        "/api/v1/auth/changeThePhone")
                                .permitAll().anyRequest().authenticated()
                ).userDetailsService(userDetailsService)
                .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Укажите конкретные домены, которые могут отправлять запросы к вашему серверу
        configuration.setAllowedOrigins(List.of("https://10.4.56.69/", "http://localhost:3000"));

        // Укажите методы, которые могут использоваться в запросах
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Укажите заголовки, которые могут быть отправлены в запросах
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));

        // Разрешить отправку учётных данных (например, cookie)
        configuration.setAllowCredentials(true);

        // Настройка конфигурации для всех путей
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
