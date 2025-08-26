package com.moviesearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MovieSearchServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MovieSearchServiceApplication.class, args);
    }
}
