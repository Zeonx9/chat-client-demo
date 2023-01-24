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

/**
 * Вспомогательный класс, который помогает настроить приложение во время запуска
 */
public class StartClientApp {

    /**
     * метод, который обращается на сервер ngrok и запрашивает открытые туннели для аккаута Артёма
     * @return реальный адрес сервера, если тунель открыт
     *         Connection error если не удалось подключиться к сети
     *         no tunnel если нет открытого туннеля
     */
    static String getTunnelUrl() {
        // токен ngrok от аккаунта Артёма
        final String token = "2K8b1iawfqvJEWz8UhK7i9UOzml_36h3tWg7TzPSJbyRQSwpQ";

        // настроить реквест для ngroka
        var request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.ngrok.com/tunnels"))
                .header("Authorization", String.format("Bearer %s", token))
                .header("Ngrok-Version", "2")
                .GET()
                .build();

        // отправить запрос и получить ответ
        HttpResponse<String> response;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        }
        catch (IOException | InterruptedException e) {
            return "Connection error";
        }

        ObjectMapper mapper = new ObjectMapper();

        // получить адрес туннеля из ответа от сервера ngrok
        JsonNode tunnel;
        try {
            tunnel = mapper.readTree(response.body()).get("tunnels").get(0);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // проверить, что туннель есть
        if (tunnel == null) {
            return "no tunnel";
        }

        return tunnel.get("public_url").asText();
    }

    /**
     * метод, который запрашивает адрес сервера приложения
     * создает фабрики для компонентов приложения (модель, вью, вью-модель)
     * передает управление на ViewHandler
     * @param stage объект Stage, который получен от метода start в главном классе приложения
     */
    public static void start(Stage stage) throws IOException {
        System.out.println("Starting the application ...");
        var url = getTunnelUrl();
        System.out.println("Server is located at: " + url);

        // создание фабрик для управления слоями приложения
        ModelFactory modelFactory = new ModelFactory(url);
        ViewModelProvider viewModelProvider = new ViewModelProvider(modelFactory);
        ViewHandler viewHandler = new ViewHandler(stage, viewModelProvider);

        // включает начальное вью
        viewHandler.start();
    }
}
