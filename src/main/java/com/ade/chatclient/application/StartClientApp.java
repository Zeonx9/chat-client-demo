package com.ade.chatclient.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.stage.Stage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

/**
 * Вспомогательный класс, который помогает настроить приложение во время запуска
 */
public class StartClientApp {
    private static ViewModelProvider viewModelProvider;

    /**
     * метод, который запрашивает адрес сервера приложения
     * создает фабрики для компонентов приложения (модель, вью, вью-модель)
     * передает управление на ViewHandler
     * @param stage объект Stage, который получен от метода start в главном классе приложения
     */
    public static void start(Stage stage) {
        System.out.println("Starting the application ...");

        // создание фабрик для управления слоями приложения
        ModelFactory modelFactory = new ModelFactory();
//        requestTunnelUrlAsync()
//                .thenAccept(modelFactory::injectServerUrl)
//                .handle((unused, throwable) -> {
//                    System.err.println(throwable.getMessage());
//                    return unused;
//                });
        modelFactory.injectServerUrl("http://195.133.196.67:8080");


        viewModelProvider = new ViewModelProvider(modelFactory);
        ViewHandler viewHandler = new ViewHandler(stage, viewModelProvider);

        // включает начальное вью
        viewHandler.start();
    }

    /**
     * останавливает запущенный фоновый поток.
     */
    public static void stop() {
        viewModelProvider.getBackgroundService().stop();
    }

    /**
     * Асинхронно отправляет запрос к серверу ngrok и ставит в очередь метод по обработке
     * возвращенного ответа
     * @return CompletableFuture, который будет содержать строку с тунелем или выдаст ошибку
     */
    private static CompletableFuture<String> requestTunnelUrlAsync() {
        return HttpClient.newHttpClient().sendAsync(
                        CreateRequestToNgrok(),
                        HttpResponse.BodyHandlers.ofString()
                )
                .thenApply(StartClientApp::parseResponseFromNgrok);
    }

    /**
     * Строит запрос к серверу ngrok, который проверяет открытые тунели на аккауте Артёма
     * @return созданный реквест
     */
    private static HttpRequest CreateRequestToNgrok() {
        // токен ngrok от аккаунта Артёма
        final String token = "2K8b1iawfqvJEWz8UhK7i9UOzml_36h3tWg7TzPSJbyRQSwpQ";

        // настроить реквест для ngrok
        return HttpRequest.newBuilder()
                .uri(URI.create("https://api.ngrok.com/tunnels"))
                .header("Authorization", String.format("Bearer %s", token))
                .header("Ngrok-Version", "2")
                .GET()
                .build();
    }

    /**
     * достает url сервера приложения из ответа от ngrok
     * @param response ответ, который пришел от сервера
     * @return url, на котором расположен сервер
     * @throws RuntimeException если нет открытых туннелей или произошла ошибка парсинга
     */
    private static String parseResponseFromNgrok(HttpResponse<String> response) {
        ObjectMapper mapper = new ObjectMapper();

        JsonNode tunnel;
        try {
            tunnel = mapper.readTree(response.body()).get("tunnels").get(0);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error during decoding ngrok response", e);
        }

        if (tunnel == null)
            throw new RuntimeException("No tunnel");

        var url = tunnel.get("public_url").asText();
        System.out.println("Server is located at: " + url);

        return url;
    }
}
