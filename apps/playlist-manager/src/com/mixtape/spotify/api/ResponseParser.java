package com.mixtape.spotify.api;


import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;


public class ResponseParser {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Response parse(String jsonBlob) throws IOException {
        return objectMapper.readValue(jsonBlob, Response.class);
    }

    public static Response parse(InputStream stream) throws IOException {
        return objectMapper.readValue(stream, Response.class);
    }


}
