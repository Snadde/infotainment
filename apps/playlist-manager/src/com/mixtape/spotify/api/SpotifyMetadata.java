package com.mixtape.spotify.api;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;


public class SpotifyMetadata {

    private final static String QUERY_TEMPLATE = "http://ws.spotify.com/search/1/%s.json?q=%s&page=%d";



    public Response search(String searchString, RequestType type) throws IOException {
        return search(searchString, 1, type);
    }

    public Response search(String searchString, int page, RequestType type) throws IOException {
        String urlEncodedSearchString = URLEncoder.encode(searchString, "UTF-8");
        String searchUrl = String.format(QUERY_TEMPLATE, type.name(), urlEncodedSearchString, page);
        InputStream stream = request(searchUrl);
        try {
            return ResponseParser.parse(stream);
        } finally {
            stream.close();
        }
    }

    private InputStream request(String searchUrl) throws IOException {
        URL url = new URL(searchUrl);
        URLConnection connection = url.openConnection();
        return connection.getInputStream();
    }

}