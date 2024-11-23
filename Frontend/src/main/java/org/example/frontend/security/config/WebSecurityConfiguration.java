package org.example.frontend.security.config;

import org.example.frontend.security.details.DataDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {

    @Bean
    public UserDetailsService userDetailsService() {
        DataDetailsService dataDetailsService = new DataDetailsService();
        return dataDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                .requestMatchers("/","/registration","/login","/post","/test","/imageview/**").permitAll()
                .requestMatchers("/js/**").permitAll()
                .requestMatchers("/css/**").permitAll()
                .requestMatchers("/images/**").permitAll()
                .anyRequest().authenticated()
        );
        http.headers(Customizer.withDefaults());
        http.sessionManagement(Customizer.withDefaults());
        http.formLogin(formLogin ->
                formLogin.loginPage("/login")
                        .loginProcessingUrl("/login")
                        .failureForwardUrl("/?error")
                        .defaultSuccessUrl("/home",true)
                        .permitAll()

        );
        http.cors(cors -> cors.disable());
        http.anonymous(Customizer.withDefaults());
        http.csrf(Customizer.withDefaults());
        return http.build();
    }


    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationProvider authenticationProvider() {
        // The builder will ensure the passwords are encoded before saving in memory
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(userDetailsService());
        return daoAuthenticationProvider;
    }

}