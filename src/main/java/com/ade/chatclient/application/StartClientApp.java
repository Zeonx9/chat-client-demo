package com.ade.chatclient.application;

import com.ade.chatclient.model.ModelFactory;
import com.ade.chatclient.view.ViewHandler;
import com.ade.chatclient.viewmodel.ViewModelProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

// TODO write documentation

// this class configures the app before it is able to run
public class StartClientApp {

    // sends request to api.ngrok.com/tunnels and returns the url of tunnel if it is open
    static String getTunnelUrl(HttpClient client) {

        // ngrok auth token
        final String token = "2K8b1iawfqvJEWz8UhK7i9UOzml_36h3tWg7TzPSJbyRQSwpQ";

        // configure a request to ngrok api
        var request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.ngrok.com/tunnels"))
                .header("Authorization", String.format("Bearer %s", token))
                .header("Ngrok-Version", "2")
                .GET()
                .build();

        // send a request and get a response
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }
        catch (IOException | InterruptedException e) {
            return "Connection error";
        }

        ObjectMapper mapper = new ObjectMapper();
        // get a first tunnel from the response json
        JsonNode tunnel;
        try {
            tunnel = mapper.readTree(response.body()).get("tunnels").get(0);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // check if tunnel exists
        if (tunnel == null) {
            return "no tunnel";
//            throw new RuntimeException("No accessible tunnels");
        }

        return tunnel.get("public_url").asText();
    }

    // calls necessary methods and initiates everything
    public static void start(Stage stage) throws IOException {
        System.out.println("Starting the application ...");

        var httpClient = HttpClient.newHttpClient();
        var url = getTunnelUrl(httpClient);
        var baseEndPoint = "/chat_api/v1";
        var handler = new RequestHandler(url + baseEndPoint, httpClient);

        System.out.println("Server is located at: " + url);

        // create factories to manage layers
        ModelFactory modelFactory = new ModelFactory(handler);
        ViewModelProvider viewModelProvider = new ViewModelProvider(modelFactory);
        ViewHandler viewHandler = new ViewHandler(stage, viewModelProvider);

        // call a start method in viewHandler
        viewHandler.start();
    }
}
