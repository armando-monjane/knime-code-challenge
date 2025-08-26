package com.moviesearch.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class MovieSearchResponse {

    @JsonProperty("Search")
    private List<Movie> search;

    @JsonProperty("totalResults")
    private String totalResults;

    @JsonProperty("Response")
    private String response;

    @JsonProperty("Error")
    private String error;

    // Constructors
    public MovieSearchResponse() {}

    public MovieSearchResponse(List<Movie> search, String totalResults, String response) {
        this.search = search;
        this.totalResults = totalResults;
        this.response = response;
    }

    // Getters and Setters
    public List<Movie> getSearch() {
        return search;
    }

    public void setSearch(List<Movie> search) {
        this.search = search;
    }

    public String getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(String totalResults) {
        this.totalResults = totalResults;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public boolean isSuccess() {
        return "True".equalsIgnoreCase(response);
    }

    @Override
    public String toString() {
        return "MovieSearchResponse{" +
                "search=" + search +
                ", totalResults='" + totalResults + '\'' +
                ", response='" + response + '\'' +
                ", error='" + error + '\'' +
                '}';
    }
}
