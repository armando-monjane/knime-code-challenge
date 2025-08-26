package com.moviesearch.service;

import com.moviesearch.dto.MovieSearchResponse;
import com.moviesearch.exception.MaintenanceModeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
public class MovieSearchService {

    private static final Logger logger = LoggerFactory.getLogger(MovieSearchService.class);

    private final WebClient webClient;
    private final FeatureFlagService featureFlagService;

    @Value("${omdb.api.key:${OMDB_API_KEY:demo_key}}")
    private String apiKey;

    @Autowired
    public MovieSearchService(WebClient.Builder webClientBuilder, FeatureFlagService featureFlagService) {
        this.webClient = webClientBuilder.baseUrl("http://www.omdbapi.com").build();
        this.featureFlagService = featureFlagService;
    }

    public Mono<MovieSearchResponse> searchMovies(String title) {
        // Check maintenance mode
        if (featureFlagService.isMaintenanceMode()) {
            logger.warn("Movie search blocked due to maintenance mode");
            return Mono.error(new MaintenanceModeException("Service is currently under maintenance"));
        }

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("s", title)
                        .queryParam("apikey", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(MovieSearchResponse.class)
                .doOnError(error -> {
                    if (error instanceof WebClientResponseException) {
                        logger.error("OMDB API error for title '{}': {}", title, error.getMessage());
                    } else {
                        logger.error("Unexpected error searching for title '{}': {}", title, error.getMessage());
                    }
                });
    }

    public Mono<MovieSearchResponse> getMovieDetails(String imdbId) {
        // Check maintenance mode
        if (featureFlagService.isMaintenanceMode()) {
            logger.warn("Movie details request blocked due to maintenance mode");
            return Mono.error(new MaintenanceModeException("Service is currently under maintenance"));
        }

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("i", imdbId)
                        .queryParam("apikey", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(MovieSearchResponse.class)
                .doOnError(error -> {
                    logger.error("Error getting movie details for IMDB ID '{}': {}", imdbId, error.getMessage());
                });
    }
}
