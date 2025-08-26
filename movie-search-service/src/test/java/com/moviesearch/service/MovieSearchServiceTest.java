package com.moviesearch.service;

import com.moviesearch.dto.Movie;
import com.moviesearch.dto.MovieSearchResponse;
import com.moviesearch.exception.MaintenanceModeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Collections;

@ExtendWith(MockitoExtension.class)
class MovieSearchServiceTest {

    @Mock
    private FeatureFlagService featureFlagService;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.Builder webClientBuilder;

    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    private MovieSearchService movieSearchService;

    @BeforeEach
    void setUp() {
        when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        movieSearchService = new MovieSearchService(webClientBuilder, featureFlagService);
        
        // Setup mock response data
        MovieSearchResponse mockResponse = new MovieSearchResponse();
        Movie movie = new Movie();
        movie.setTitle("Test Movie");
        movie.setImdbId("tt1234567");
        mockResponse.setSearch(Collections.singletonList(movie));
        mockResponse.setResponse("True");
        mockResponse.setTotalResults("1");
    }

    @Test
    void searchMovies_WhenMaintenanceModeEnabled_ShouldThrowException() {
        when(featureFlagService.isMaintenanceMode()).thenReturn(true);

        StepVerifier.create(movieSearchService.searchMovies("test")) 
                .expectError(MaintenanceModeException.class)
                .verify();

        verify(featureFlagService).isMaintenanceMode();
        verifyNoInteractions(responseSpec);
    }

    @Test
    void getMovieDetails_WhenMaintenanceModeEnabled_ShouldThrowException() {
        when(featureFlagService.isMaintenanceMode()).thenReturn(true);

        StepVerifier.create(movieSearchService.getMovieDetails("tt1234567"))
                .expectError(MaintenanceModeException.class)
                .verify();

        verify(featureFlagService).isMaintenanceMode();
        verifyNoInteractions(responseSpec);
    }
}
