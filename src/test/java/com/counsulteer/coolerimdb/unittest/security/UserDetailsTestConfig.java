package com.counsulteer.coolerimdb.unittest.security;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.List;

@TestConfiguration
public class UserDetailsTestConfig {

    @Bean
    @Primary
    public UserDetailsService userDetailsService() {
        User basicUser = new User("user@gmail.com", "Sifraaa123!", List.of(new SimpleGrantedAuthority("USER")));

        return new InMemoryUserDetailsManager(List.of(basicUser));
    }

}
