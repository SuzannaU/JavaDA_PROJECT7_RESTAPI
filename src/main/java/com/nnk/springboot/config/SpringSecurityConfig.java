package com.nnk.springboot.config;

import com.nnk.springboot.services.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security Configuration class.
 * This class configures the security settings for the application,
 * including authentication and authorization.
 *
 * @see CustomUserDetailsService
 */
@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    public SpringSecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    /**
     * Configures the security filter chain for the application.
     * This method defines the authorization rules for different URL patterns,
     * sets up form login, and configures session management.
     *
     * @param http the HttpSecurity object used to configure security settings
     * @return the configured SecurityFilterChain
     * @throws Exception if an error occurs during configuration
     * @see HttpSecurity
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(
                                    "/",
                                    "/css/**")
                            .permitAll();
                    auth.requestMatchers("/user/**")
                            .hasRole("ADMIN");
                    auth.requestMatchers(
                                    "/bidList/**",
                                    "/curvePoint/**",
                                    "/rating/**",
                                    "/trade/**",
                                    "/ruleName/**")
                            .hasAnyRole("ADMIN", "USER");
                    auth.anyRequest().authenticated();
                })
                .formLogin(form -> form
                        .defaultSuccessUrl("/", true)
                        .permitAll())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .build();
    }

    /**
     * Configures the password encoder to be used for encoding passwords.
     * This method returns a BCryptPasswordEncoder with a strength of 10.
     *
     * @return a BCryptPasswordEncoder instance
     * @see BCryptPasswordEncoder
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    /**
     * Configures the authentication provider for the application.
     * This method sets up a DaoAuthenticationProvider with a custom UserDetailsService
     * and a password encoder.
     *
     * @return an AuthenticationProvider instance
     * @see DaoAuthenticationProvider
     * @see CustomUserDetailsService
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(bCryptPasswordEncoder());
        return authProvider;
    }

}
