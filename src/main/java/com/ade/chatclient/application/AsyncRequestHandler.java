package com.ade.chatclient.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static com.ade.chatclient.application.StartClientApp.URLS;

/**
 * Главный класс обрабатывающий HTTP запрос исходящие из приложения
 * два обязательх поля, которые должны быть вставленны через сеттеры.
 */
@RequiredArgsConstructor
public class AsyncRequestHandler {
    private final static HttpClient client = HttpClient.newHttpClient();
    private final static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.findAndRegisterModules();
    }

    @Setter
    private String authToken;

    /**
     * строит GET запрос с параметрами и преобразует ответ к нужному типу
     * @param path путь до точки входа на сервер
     * @param params параметры запроса
     * @param tReference указывает тип, к которому надо привести ответ от сервера
     * @return преобразованный ответ от сервера
     * @throws RuntimeException при невозможности сериализовать объект
     */
    public <T> CompletableFuture<T> sendGet(String path, Map<String, String> params, TypeReference<T> tReference) {
        return sendGETAsync(path, params).thenApply(mapperOf(tReference));
    }

    /**
     * строит GET запрос без параметров и преобразует ответ к нужному типу
     * @param path путь до точки входа на сервер
     * @param tReference указывает тип, к которому надо привести ответ от сервера
     * @return преобразованный ответ от сервера
     * @throws RuntimeException при невозможности сериализовать объект
     */
    public <T> CompletableFuture<T> sendGet(String path, TypeReference<T> tReference) {
        return sendGETAsync(path).thenApply(mapperOf(tReference));
    }

    /**
     * строит GET запрос без параметров и преобразует ответ к нужному типу
     * @param path путь до точки входа на сервер
     * @param tClass указывает тип, к которому надо привести ответ от сервера
     * @return преобразованный ответ от сервера
     * @throws RuntimeException при невозможности сериализовать объект
     */
    public <T> CompletableFuture<T> sendGet(String path, Class<T> tClass) {
        return sendGETAsync(path).thenApply(mapperOf(tClass));
    }

    /**
     * строит POST запрос без параметров и преобразует ответ к нужному типу
     *
     * @param path    путь до точки входа на сервер
     * @param bodyObj объект, который необходимо отправить
     * @param vClass  указывает тип, к которому надо привести ответ от сервера
     * @return преобразованный ответ от сервера
     * @throws RuntimeException при невозможности сериализовать объект
     */
    public <V, T> CompletableFuture<V> sendPost(String path, T bodyObj, Class<V> vClass, boolean authorized) {
        return sendPOSTAsync(path, bodyObj, authorized).thenApply(mapperOf(vClass));
    }

    /**
     * строит PUT запрос без параметров и преобразует ответ к нужному типу
     * @param path путь до точки входа на сервер
     * @param bodyObj объект, который необходимо отправить
     * @param vClass указывает тип, к которому надо привести ответ от сервера
     * @return преобразованный ответ от сервера
     * @throws RuntimeException при невозможности сериализовать объект
     */
    public <V, T> CompletableFuture<V> sendPut(String path, T bodyObj, Class<V> vClass) {
        return sendPUTAsync(path, bodyObj).thenApply(mapperOf(vClass));
    }

    private CompletableFuture<HttpResponse<String>> sendGETAsync(String path, Map<String, String> params) {
        return client.sendAsync(
                configureRequest(path, params, true)
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );
    }

    private CompletableFuture<HttpResponse<String>> sendGETAsync(String path) {
        return sendGETAsync(path, Map.of());
    }

    private CompletableFuture<HttpResponse<String>> sendPOSTAsync(String path, String body, boolean authorized) {
        return client.sendAsync(
                configureRequest(path, Map.of(), authorized)
                        .POST(HttpRequest.BodyPublishers.ofString(body))
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );
    }

    private <T> CompletableFuture<HttpResponse<String>> sendPOSTAsync(String path, T bodyObj, boolean authorized) {
        try {
            return sendPOSTAsync(path, mapper.writeValueAsString(bodyObj), authorized);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Cannot send. Unable to serialize the object", e);
        }
    }

    private  <T> CompletableFuture<HttpResponse<String>> sendPUTAsync(String path, T body) {
        try {
            return client.sendAsync(
                    configureRequest(path, Map.of(), true)
                            .PUT(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)))
                            .build(),
                    HttpResponse.BodyHandlers.ofString()
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    private HttpRequest.Builder configureRequest(String path, Map<String, String> params, boolean authorized) {
        if (authorized && authToken == null) {
            throw new IllegalStateException("Authorized request cannot be built, token is not set yet!");
        }

        String uriStr = URLS.getServerUrl() + path;
        if (!params.isEmpty())
            uriStr += "?";

        var paramValues = new ArrayList<String>();
        params.forEach((k, v) -> paramValues.add(k + "=" + v));
        uriStr += String.join("&", paramValues);

        var builder = HttpRequest.newBuilder()
                .uri(URI.create(uriStr))
                .header("Content-Type", "application/json")
                .header("ngrok-skip-browser-warning", "skip");
        if (!authorized) {
            return builder;
        }
        return builder.header("Authorization", "Bearer " + authToken);
    }

    private static void checkResponse(HttpResponse<String> response) {
        if (response.statusCode() != 200) {
            throw new RuntimeException(
                    "Unsuccessful request: status = " + response.statusCode()
                            + ", body = " + response.body()
            );
        }
    }

    private static <T> T mapResponse(HttpResponse<String> response, Class<T> objectClass) {
        checkResponse(response);

        try {
            return mapper.readValue(response.body(), objectClass);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error during decoding response", e);
        }
    }

    private static <T> T mapResponse(HttpResponse<String> response, TypeReference<T> typeRef) {
        checkResponse(response);

        try {
            return mapper.readValue(response.body(), typeRef);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error during decoding response", e);
        }
    }

    private static <T> Function<HttpResponse<String>, T> mapperOf(Class<T> objectClass) {
        return response -> mapResponse(response, objectClass);
    }

    private static <T> Function<HttpResponse<String>, T> mapperOf(TypeReference<T> typRef) {
        return response -> mapResponse(response, typRef);
    }
}

