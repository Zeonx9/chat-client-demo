package com.ade.chatclient.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Map;

public class RequestHandler {
    private final String url;
    private final HttpClient client;
    private final ObjectMapper mapper;

    public RequestHandler(String url, HttpClient client) {
        this.url = url;
        this.client = client;
        mapper = new ObjectMapper();
    }

    private HttpRequest.Builder getPresetRequest(String path, Map<String, String> params) {
        String uriStr = url + path;
        if (!params.isEmpty())
            uriStr += "?";

        var paramValues = new ArrayList<String>();
        params.forEach((k, v) -> paramValues.add(k + "=" + v));
        uriStr += String.join("&", paramValues);

        return HttpRequest.newBuilder()
                .uri(URI.create(uriStr))
                .header("Content-Type", "application/json")
                .header("ngrok-skip-browser-warning", "skip");
    }

    private String sendRequestAndGetResponse(HttpRequest request) {
        try {
            var resp = client.send(request, HttpResponse.BodyHandlers.ofString());
            return resp.body();
        }
        catch (InterruptedException | IOException exc) {
            throw new IllegalStateException("Something went wrong during sending request", exc);
        }
    }

    private  <T> T mapResponse(HttpRequest request, Class<T> objClass, TypeReference<T> typeRef) {
        var response = sendRequestAndGetResponse(request);
        T mappedObj;
        try {
            if (objClass != null)
                mappedObj = mapper.readValue(response, objClass);
            else if (typeRef != null)
                mappedObj = mapper.readValue(response, typeRef);
            else
                throw new IllegalStateException("wrong parameters given to mapping function");

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return mappedObj;
    }

    public HttpRequest GETRequest(String path, Map<String, String> params) {
        return getPresetRequest(path, params).GET().build();
    }

    public HttpRequest GETRequest(String path) {
        return getPresetRequest(path, Map.of()).GET().build();
    }

    public <T> T mapResponse(HttpRequest request, Class<T> objClass) {
        return mapResponse(request, objClass, null);
    }

    public <T> T mapResponse(HttpRequest request, TypeReference<T> typeRef) {
        return mapResponse(request, null, typeRef);
    }
}
