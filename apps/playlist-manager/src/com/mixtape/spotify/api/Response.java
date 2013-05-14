package com.mixtape.spotify.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

/**
 * API response
 */
public class Response {

    private final Info info;
    private final Collection<Album> albums;
    private final Collection<Artist> artists;
    private final Collection<Track> tracks;

    @JsonCreator
    public Response(@JsonProperty("info") Info info,
                    @JsonProperty(value = "albums", required = false) Collection<Album> albums,
                    @JsonProperty(value = "artists", required = false) Collection<Artist> artists,
                    @JsonProperty(value = "tracks", required = false) Collection<Track> tracks) {
        this.info = info;
        this.albums = albums;
        this.artists = artists;
        this.tracks = tracks;
    }

    public Info getInfo() {
        return info;
    }

    public Collection<Album> getAlbums() {
        return albums;
    }

    public Collection<Artist> getArtists() {
        return artists;
    }

    public Collection<Track> getTracks() {
        return tracks;
    }

    @Override
    public String toString() {
        return "Response{" +
                "info=" + info +
                ", albums=" + albums +
                ", artists=" + artists +
                ", tracks=" + tracks +
                '}';
    }
}
