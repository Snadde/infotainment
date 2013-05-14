package com.mixtape.spotify.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.Collection;


public class Availability {

    private final Collection<String> territories;

    @JsonCreator
    public Availability(@JsonProperty("territories") String territoriesString) {
        this.territories = Arrays.asList(territoriesString.split(" "));
    }

    public Collection<String> getTerritories() {
        return territories;
    }

    @Override
    public String toString() {
        return "Availability{" +
                "territories=" + territories +
                '}';
    }
}
