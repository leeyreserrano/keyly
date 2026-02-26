package com.keyly.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // Las rutas que se pasen por el requestMatchers, permite conectarse sin hacer login a las rutas
    // @Bean
    // public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    //     http
    //             .csrf(csrf -> csrf.disable())
    //             .authorizeHttpRequests(auth -> auth
    //                     .requestMatchers(
    //                         "/sucursals"
    //                     ).permitAll() // Permet accedir a les rutes sense autentificar
    //                     .anyRequest().authenticated());

    //     return http.build();
    // }

    /**
     * Habilita todas las conexiones para ir haciendo pruebas
     * 
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
