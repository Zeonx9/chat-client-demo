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


// class that wraps HttpClient and forms HttpRequest, sends them and maps the responses
public class RequestHandler {
    private final String url;
    private final HttpClient client;
    private final ObjectMapper mapper;

    // url is a combination of tunnel address and the base of endpoint - a part that is the same for all of them
    // in our application it is "/chat_api/v1"
    public RequestHandler(String url, HttpClient client) {
        this.url = url;
        this.client = client;
        mapper = new ObjectMapper();
    }

    // private method used internally to pre-build the request
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

    // private method used internally to send the request and get the string from response body
    private String sendRequestAndGetResponse(HttpRequest request) {
        try {
            var resp = client.send(request, HttpResponse.BodyHandlers.ofString());
            return resp.body();
        }
        catch (InterruptedException | IOException exc) {
            throw new IllegalStateException("Something went wrong during sending request", exc);
        }
    }

    // private method used internally to map response using Jackson
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

    // constructs a GET http request with parameters passed in a map
    // path presents only a variable part of the path to end point
    // for example "/user" in "http://localhost:8080/chat_api/v1/user"
    // it should always start with the backslash
    public HttpRequest GETRequest(String path, Map<String, String> params) {
        return getPresetRequest(path, params).GET().build();
    }

    // constructs a GET http request without parameters
    // path has the same requirements as another overloaded version
    public HttpRequest GETRequest(String path) {
        return getPresetRequest(path, Map.of()).GET().build();
    }


    // returns an object deserialized from json-string
    public <T> T mapResponse(HttpRequest request, Class<T> objClass) {
        return mapResponse(request, objClass, null);
    }

    // returns an object deserialized from json-string
    public <T> T mapResponse(HttpRequest request, TypeReference<T> typeRef) {
        return mapResponse(request, null, typeRef);
    }
}
