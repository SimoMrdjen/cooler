package com.counsulteer.coolerimdb.config;

import com.counsulteer.coolerimdb.mapper.ActorMapper;
import com.counsulteer.coolerimdb.mapper.MovieMapper;
import com.counsulteer.coolerimdb.mapper.UserMapper;
import com.counsulteer.coolerimdb.mapper.WatchlistMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppConfig {
    @Bean
    public UserMapper userMapper() {
        return new UserMapper();
    }

    @Bean
    public MovieMapper movieMapper() {
        return new MovieMapper();
    }
    @Bean
    public ActorMapper actorMapper(){
        return new ActorMapper();
    }

    @Bean
    public WatchlistMapper watchlistMapper() {
        return new WatchlistMapper(movieMapper());
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedMethods("GET", "POST", "PUT", "DELETE")
                        .allowedOrigins("*")
                        .allowedHeaders("*");
            }
        };
    }
}
