package com.mixtape.spotify.api;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Artist {

    private final String href;
    private final String name;
    private final Double popularity;

    @JsonCreator
    public Artist(@JsonProperty("href") String href,
                  @JsonProperty("name") String name,
                  @JsonProperty(value = "popularity", required = false) Double popularity) {

        this.href = href;
        this.name = name;
        this.popularity = popularity;
    }

    public String getHref() {
        return href;
    }

    public String getName() {
        return name;
    }

    public Double getPopularity() {
        return popularity;
    }


    @Override
    public String toString() {
        return "Artist{" +
                "href='" + href + '\'' +
                ", name='" + name + '\'' +
                ", popularity=" + popularity +
                '}';
    }
}
