package com.mixtape.spotify.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response info
 */
public class Info {


    private final int numResults;
    private final int limit;
    private final int offset;
    private final String query;
    private final RequestType type;
    private final int page;

    @JsonCreator
    public Info(@JsonProperty("num_results") int numResults,
                @JsonProperty("limit") int limit,
                @JsonProperty("offset") int offset,
                @JsonProperty("query") String query,
                @JsonProperty("type") RequestType type,
                @JsonProperty("page") int page) {

        this.numResults = numResults;
        this.limit = limit;
        this.offset = offset;
        this.query = query;
        this.type = type;
        this.page = page;
    }

    public int getNumResults() {
        return numResults;
    }

    public int getLimit() {
        return limit;
    }

    public int getOffset() {
        return offset;
    }

    public String getQuery() {
        return query;
    }

    public RequestType getType() {
        return type;
    }

    public int getPage() {
        return page;
    }

    @Override
    public String toString() {
        return "Info{" +
                "numResults=" + numResults +
                ", limit=" + limit +
                ", offset=" + offset +
                ", query='" + query + '\'' +
                ", type='" + type + '\'' +
                ", page=" + page +
                '}';
    }
}
