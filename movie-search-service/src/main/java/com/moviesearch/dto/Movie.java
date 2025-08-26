package com.moviesearch.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Movie {

    @JsonProperty("Title")
    private String title;

    @JsonProperty("Year")
    private String year;

    @JsonProperty("imdbID")
    private String imdbId;

    @JsonProperty("Type")
    private String type;

    @JsonProperty("Poster")
    private String poster;

    // Constructors
    public Movie() {}

    public Movie(String title, String year, String imdbId, String type, String poster) {
        this.title = title;
        this.year = year;
        this.imdbId = imdbId;
        this.type = type;
        this.poster = poster;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getImdbId() {
        return imdbId;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "title='" + title + '\'' +
                ", year='" + year + '\'' +
                ", imdbId='" + imdbId + '\'' +
                ", type='" + type + '\'' +
                ", poster='" + poster + '\'' +
                '}';
    }
}
