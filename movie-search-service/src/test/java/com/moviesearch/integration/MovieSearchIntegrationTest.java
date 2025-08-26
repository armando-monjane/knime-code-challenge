package com.moviesearch.integration;

import com.moviesearch.dto.FlagUpdateEvent;
import com.moviesearch.dto.Movie;
import com.moviesearch.dto.MovieSearchResponse;
import com.moviesearch.service.FeatureFlagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.Collections;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class MovieSearchIntegrationTest {

    @LocalServerPort
    private int port;

    private WebTestClient webTestClient;

    @Autowired
    private FeatureFlagService featureFlagService;

    @MockBean
    private WebClient.Builder webClientBuilder;

    @MockBean
    private WebClient webClient;

    @BeforeEach
    void mockWebClient() {
        WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        MovieSearchResponse mockResponse = new MovieSearchResponse();
        mockResponse.setResponse("True");
        mockResponse.setTotalResults("1");
        Movie movie = new Movie();
        movie.setTitle("Test Movie");
        movie.setImdbId("tt1234567");
        mockResponse.setSearch(Collections.singletonList(movie));

        when(responseSpec.bodyToMono(MovieSearchResponse.class))
            .thenReturn(Mono.just(mockResponse));

    }

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient
            .bindToServer()
            .baseUrl("http://localhost:" + port)
            .build();

        // Enable maintenance mode flag
        FlagUpdateEvent event = new FlagUpdateEvent("maintenance_mode", true, "CREATED");
        event.setTimestamp(LocalDateTime.now());
        featureFlagService.handleFlagUpdate(event);
    }

    @Test
    void searchMovies_WhenMaintenanceModeEnabled_ShouldReturnServiceUnavailable() {
        webTestClient.get()
                .uri("/api/movies/search?title=test")
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void getMovieDetails_WhenMaintenanceModeEnabled_ShouldReturnServiceUnavailable() {
        webTestClient.get()
                .uri("/api/movies/tt1234567")
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void getFlagStatus_ShouldReturnCorrectStatus() {
        webTestClient.get()
                .uri("/api/flags/name/maintenance_mode")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class).isEqualTo(true);
    }

    @Test
    void getMaintenanceMode_ShouldReturnCorrectStatus() {
        webTestClient.get()
                .uri("/api/flags/maintenance_mode")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class).isEqualTo(true);
    }

    @Test
    void healthCheck_ShouldReturnOk() {
        webTestClient.get()
                .uri("/api/movies/health")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Movie Search Service is healthy");
    }
}