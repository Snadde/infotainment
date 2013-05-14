package com.mixtape.spotify.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;


public class Track {

    private final String href;
    private final String name;
    private final Collection<ExternalId> externalIds;
    private final double popularity;
    private final int length;
    private final Collection<Artist> artists;
    private final Album album;
    private final int trackNumber;
    private final boolean explicit;


    @JsonCreator
    public Track(@JsonProperty("href") String href,
                 @JsonProperty("name") String name,
                 @JsonProperty("external-ids") Collection<ExternalId> externalIds,
                 @JsonProperty("popularity") double popularity,
                 @JsonProperty("length") String length,
                 @JsonProperty("artists") Collection<Artist> artists,
                 @JsonProperty("album") Album album,
                 @JsonProperty("track-number") int trackNumber,
                 @JsonProperty("explicit") boolean explicit) {

        this.href = href;
        this.name = name;
        this.externalIds = externalIds;
        this.popularity = popularity;
        this.length = Integer.valueOf(length.split("\\.")[0]);
        this.artists = artists;
        this.album = album;
        this.trackNumber = trackNumber;
        this.explicit = explicit;
    }


    public String getHref() {
        return href;
    }

    public String getName() {
        return name;
    }

    public Collection<ExternalId> getExternalIds() {
        return externalIds;
    }

    public double getPopularity() {
        return popularity;
    }

    public int getLength() {
        return length;
    }

    public Collection<Artist> getArtists() {
        return artists;
    }

    public Album getAlbum() {
        return album;
    }

    public int getTrackNumber() {
        return trackNumber;
    }

    public boolean isExplicit() {
        return explicit;
    }
}
