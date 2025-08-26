package com.moviesearch.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    @ConditionalOnMissingBean(WebClient.class)
    public WebClient webClient(WebClient.Builder builder,
                               @Value("${omdb.api.base-url:http://www.omdbapi.com}") String baseUrl) {
        return builder.baseUrl(baseUrl).build();
    }
}
