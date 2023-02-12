package com.ade.chatclient.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@NoArgsConstructor
public class AsyncRequestHandler {
    private final static HttpClient client = HttpClient.newHttpClient();
    private final static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.findAndRegisterModules();
    }

    @Setter
    private String url;
    @Setter
    private String authToken;

    /**
     * метод, который помогает упростить сборку запросов
     * метод не для внешнего использования
     * @param params Map содержащий параметры для запроса в виде пар ключ-значение
     * @param path путь до точки входа на сервере
     * @return билдер запроса
     */
    private HttpRequest.Builder configureRequest(String path, Map<String, String> params, boolean authorized) {
        if (url == null)
            throw new IllegalStateException("URL of server is not set yet!");
        if (authorized && authToken == null)
            throw new IllegalStateException("Authorized request cannot be built, token is not set yet!");

        String uriStr = url + path;
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

    /**
     * @return пустой запрос, должен использоваться только для тестов
     */
    public static HttpRequest getEmptyReq() {
        return HttpRequest.newBuilder().uri(URI.create("https://foo.com/bar")).GET().build();
    }

    /**
     * строит GET запрос с параметрами
     * @param path путь до точки входа на сервер
     * @param params набор пар ключ-значение определяющих нужные параметры в запросе
     * @return собранный запрос
     */
    public CompletableFuture<HttpResponse<String>> sendGETAsync(String path, Map<String, String> params) {
        return client.sendAsync(
                configureRequest(path, params, true)
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );
    }

    /**
     * строит GET запрос без параметров
     * @param path путь до точки входа на сервер
     * @return собранный запрос
     */
    public CompletableFuture<HttpResponse<String>> sendGETAsync(String path) {
        return sendGETAsync(path, Map.of());
    }

    /**
     * строит POST запрос с параметрами
     * @param path путь до точки входа на сервер
     * @param params набор пар ключ-значение определяющих нужные параметры в запросе
     * @param body строка, которая представляет тело запроса, должна быть валидной строкой JSON
     * @return собранный запрос
     */
    public CompletableFuture<HttpResponse<String>> sendPOSTAsync(
            String path,
            Map<String, String> params,
            String body,
            boolean authorized
    ) {
        return client.sendAsync(
                configureRequest(path, params, authorized)
                        .POST(HttpRequest.BodyPublishers.ofString(body))
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );
    }

    /**
     * строит POST запрос без параметров
     *
     * @param path путь до точки входа на сервер
     * @param body строка, которая представляет тело запроса, должна быть валидной строкой JSON
     * @return собранный запрос
     */
    public CompletableFuture<HttpResponse<String>> sendPOSTAsync(String path, String body, boolean authorized) {
        return sendPOSTAsync(path, Map.of(), body, authorized);
    }

    /**
     * строит POST запрос с параметрами
     * @param path путь до точки входа на сервер
     * @param params набор пар ключ-значение определяющих нужные параметры в запросе
     * @param bodyObj объект, который будет сериализован и послан на сервер, как JSON-строка
     * @return собранный запрос
     * @throws RuntimeException при невозможности сериализовать объект
     */
    public <T> CompletableFuture<HttpResponse<String>> sendPOSTAsync(
            String path,
            Map<String, String> params,
            T bodyObj,
            boolean authorized
    ) {
        try {
            return sendPOSTAsync(path, params, mapper.writeValueAsString(bodyObj), authorized);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Cannot send. Unable to serialize the object", e);
        }
    }

    /**
     * строит POST запрос без параметров
     * @param path путь до точки входа на сервер
     * @param bodyObj объект, который будет сериализован и послан на сервер, как JSON-строка
     * @return собранный запрос
     */
    public <T> CompletableFuture<HttpResponse<String>> sendPOSTAsync(String path, T bodyObj, boolean authorized) {
        return sendPOSTAsync(path, Map.of(), bodyObj, authorized);
    }

    /**
     * Проверяет, что пришедший ответ имеет статус OK
     * @param response пришедший ответ
     * @throws RuntimeException если статус ответа не ОК
     */
    private static void checkResponse(HttpResponse<String> response) {
        if (response.statusCode() != 200) {
            throw new RuntimeException(
                    "Unsuccessful request: status = " + response.statusCode()
                            + ", body = " + response.body()
            );
        }
    }

    /**
     * @param response полученный ответ
     * @param objectClass ссылка на тип объекта, используется для коллекций других объектов
     * @return объект заданного класса полученного из ответа от сервера
     * @throws RuntimeException если невозможно десериалиазовать объект
     * или если ответ от сервера имеет ошибочный статус
     */
    public static <T> T mapResponse(HttpResponse<String> response, Class<T> objectClass) {
        checkResponse(response);

        try {
            return mapper.readValue(response.body(), objectClass);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error during decoding response", e);
        }
    }

    /**
     * @param response реквест, который нужно отправить
     * @param typeRef ссылка на тип объекта, используется для коллекций других объектов
     * @return объект заданного класса полученного из ответа от сервера
     * @throws RuntimeException если невозможно десериалиазовать объект
     * или если ответ от сервера имеет ошибочный статус
     */
    public static <T> T mapResponse(HttpResponse<String> response, TypeReference<T> typeRef) {
        checkResponse(response);

        try {
            return mapper.readValue(response.body(), typeRef);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error during decoding response", e);
        }
    }
}

