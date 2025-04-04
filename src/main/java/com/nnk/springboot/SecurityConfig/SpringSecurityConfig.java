package com.nnk.springboot.SecurityConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    public SpringSecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/").permitAll();
                    auth.requestMatchers("/403").permitAll();
                    auth.requestMatchers("/static.css/**").permitAll();
                    auth.requestMatchers("/user/**").hasRole("ADMIN");
                    auth.requestMatchers("/bidList/**").hasAnyRole("ADMIN", "USER");
                    auth.requestMatchers("/curvePoint/**").hasAnyRole("ADMIN", "USER");
                    auth.requestMatchers("/rating/**").hasAnyRole("ADMIN", "USER");
                    auth.requestMatchers("/trade/**").hasAnyRole("ADMIN", "USER");
                    auth.requestMatchers("/ruleName/**").hasAnyRole("ADMIN", "USER");
                    auth.anyRequest().authenticated();
                })
                .formLogin(form->form
                        .defaultSuccessUrl("/", true)
                        .permitAll())
                .formLogin(Customizer.withDefaults())
                .build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(bCryptPasswordEncoder());
        return authProvider;
    }

}
