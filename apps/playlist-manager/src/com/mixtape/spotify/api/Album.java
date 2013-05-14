package com.mixtape.spotify.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;


public class Album {

    private final String name;
    private final double popularity;
    private final String href;
    private final Collection<Artist> artists;
    private final Collection<ExternalId> externalIds;
    private final Availability availability;
    private final Integer releaseYear;

    @JsonCreator
    public Album(@JsonProperty("name") String name,
                 @JsonProperty("popularity") double popularity,
                 @JsonProperty("href") String href,
                 @JsonProperty("artists") Collection<Artist> artists,
                 @JsonProperty("external-ids") Collection<ExternalId> externalIds,
                 @JsonProperty("availability") Availability availability,
                 @JsonProperty(value = "released", required = false) Integer releaseYear) {
        this.name = name;
        this.popularity = popularity;
        this.href = href;
        this.artists = artists;
        this.externalIds = externalIds;
        this.availability = availability;
        this.releaseYear = releaseYear;
    }

    public String getName() {
        return name;
    }

    public double getPopularity() {
        return popularity;
    }

    public String getHref() {
        return href;
    }

    public Collection<Artist> getArtists() {
        return artists;
    }

    public Collection<ExternalId> getExternalIds() {
        return externalIds;
    }

    public Availability getAvailability() {
        return availability;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    @Override
    public String toString() {
        return "Album{" +
                "name='" + name + '\'' +
                ", popularity=" + popularity +
                ", href='" + href + '\'' +
                ", artists=" + artists +
                ", externalIds=" + externalIds +
                ", availability=" + availability +
                '}';
    }
}
