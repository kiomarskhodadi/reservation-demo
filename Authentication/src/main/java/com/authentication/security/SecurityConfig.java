package com.authentication.security;

import com.authentication.security.filter.CustomAuthenticationFilter;
import com.authentication.security.filter.CustomAuthorizationFilter;
import com.authentication.service.UserService;
import com.infrastructure.service.component.ApplicationContextProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserService userService;
    private final VariableSecurity variableSecurity;

    private final PasswordEncoder passwordEncoder;
    @Bean
    @Order(0)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        http.exceptionHandling(exception -> exception.accessDeniedHandler(new CustomAccessDeniedException()));
        http.addFilter(customAuthenticationFilter());
        http.addFilterBefore(customAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService.userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authProvider);
    }


    @Bean
    public CustomAuthenticationFilter customAuthenticationFilter() {
        AuthenticationManager authenticationManager = authenticationManager();
        CustomAuthenticationFilter filter = new CustomAuthenticationFilter(
                authenticationManager,
                variableSecurity
        );
        filter.setAuthenticationManager(authenticationManager);
        filter.setFilterProcessesUrl("/api/login");
        return filter;
    }

    @Bean
    public CustomAuthorizationFilter customAuthorizationFilter() {
        return new CustomAuthorizationFilter(variableSecurity);
    }

}
