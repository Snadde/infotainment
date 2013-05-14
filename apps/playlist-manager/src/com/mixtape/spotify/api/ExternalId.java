package com.mixtape.spotify.api;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ExternalId {

    private final String type;
    private final String id;


    @JsonCreator
    public ExternalId(@JsonProperty("type") String type,
                      @JsonProperty("id") String id) {
        this.type = type;
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "ExternalId{" +
                "type='" + type + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
