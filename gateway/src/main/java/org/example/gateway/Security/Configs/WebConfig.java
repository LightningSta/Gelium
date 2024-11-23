package org.example.gateway.Security.Configs;

import org.example.gateway.Security.DetailsService.PersonDetailService;
import org.example.gateway.Security.Filters.JwtFilter;
import org.example.gateway.Security.JWT.JWTtoken;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.config.EnableWebFlux;

import java.util.Arrays;

@Configuration
@EnableWebFluxSecurity
public class WebConfig {


    @Bean
    public PasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public PersonDetailService personDetailService(){
        return new PersonDetailService();
    }
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(bCryptPasswordEncoder());
        authenticationProvider.setUserDetailsService(personDetailService());
        return authenticationProvider;
    }
    @Bean
    public JWTtoken jwTtoken(){
        return new JWTtoken();
    }
    @Bean
    public JwtFilter jwtFilter() {
        JwtFilter jwtFilter = new JwtFilter();
        return jwtFilter;
    }


    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        http
                .authorizeExchange(exchanges -> exchanges.anyExchange().permitAll())
                .csrf(csrf -> csrf.disable()) // Отключение CSRF
                // Настройка CORS
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.addAllowedOrigin("http://localhost:8080"); // Разрешаем доступ с фронтенда
                    config.addAllowedMethod(HttpMethod.GET);
                    config.addAllowedMethod(HttpMethod.POST);
                    config.addAllowedMethod(HttpMethod.PUT);
                    config.addAllowedMethod(HttpMethod.DELETE);
                    config.addAllowedMethod(HttpMethod.OPTIONS); // Разрешаем OPTIONS
                    config.addAllowedHeader("*"); // Разрешаем все заголовки
                    config.setAllowCredentials(true); // Разрешаем отправку cookies и токенов
                    return config;
                }))
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance()) // Stateless сессии
                .addFilterAt(jwtFilter(), SecurityWebFiltersOrder.AUTHENTICATION); // Добавление JWT-фильтра

        return http.build();
    }

}
