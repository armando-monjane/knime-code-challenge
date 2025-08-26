package com.moviesearch.controller;

import com.moviesearch.dto.MovieSearchResponse;
import com.moviesearch.service.MovieSearchService;
import com.moviesearch.exception.MaintenanceModeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/movies")
@CrossOrigin(origins = "*")
public class MovieSearchController {

    private static final Logger logger = LoggerFactory.getLogger(MovieSearchController.class);

    private final MovieSearchService movieSearchService;

    @Autowired
    public MovieSearchController(MovieSearchService movieSearchService) {
        this.movieSearchService = movieSearchService;
    }

    @GetMapping("/search")
    public Mono<ResponseEntity<MovieSearchResponse>> searchMovies(@RequestParam String title) {        
        return movieSearchService.searchMovies(title)
                .map(response -> {
                    if (response.isSuccess()) {
                        return ResponseEntity.ok(response);
                    } else {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                    }
                })
                .onErrorResume(MaintenanceModeException.class, error -> {
                    logger.warn("Maintenance mode active, blocking search request");
                    return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                            .body(new MovieSearchResponse()));
                })
                .onErrorResume(Exception.class, error -> {
                    logger.error("Error searching for movies with title '{}': {}", title, error.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(new MovieSearchResponse()));
                });
    }

    @GetMapping("/{imdbId}")
    public Mono<ResponseEntity<MovieSearchResponse>> getMovieDetails(@PathVariable String imdbId) {
        return movieSearchService.getMovieDetails(imdbId)
                .map(response -> {
                    if (response.isSuccess()) {
                        return ResponseEntity.ok(response);
                    } else {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                    }
                })
                .onErrorResume(MaintenanceModeException.class, error -> {
                    logger.warn("Maintenance mode active, blocking movie details request");
                    return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                            .body(new MovieSearchResponse()));
                })
                .onErrorResume(Exception.class, error -> {
                    logger.error("Error getting movie details for IMDB ID '{}': {}", imdbId, error.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(new MovieSearchResponse()));
                });
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        logger.debug("GET /api/health - Health check");
        return ResponseEntity.ok("Movie Search Service is healthy");
    }
}
