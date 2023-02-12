package com.ade.chatclient.application;

import com.ade.chatclient.domain.Chat;
import com.ade.chatclient.domain.Message;
import com.ade.chatclient.domain.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Класс, который позволяет отправлять и получать http запросы на сервер
 * Каждый класс модели использует свой RequestHandler, который ему предоставляет
 * фабрика ModelFactory
 */
public class RequestHandler {

    /**
     * класс со статическими константами, используются для маппинга ответов от сервера в объекты домена (сущности)
     */
    public static class Types {
        public static final TypeReference<List<Chat>> ListOfChat = new TypeReference<>() {};
        public static final TypeReference<List<Message>> ListOfMessage = new TypeReference<>() {};
        public static final TypeReference<List<User>> ListOfUser = new TypeReference<>() {};
    }

    @Setter
    private String url;
    private final HttpClient client;
    private final ObjectMapper mapper;

    /**
     * создает новый экземпляр класса
     * @param url - адресс сервера выданного ngrok
     */
    public RequestHandler() {
        this.client = HttpClient.newHttpClient();
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
    }

    /**
     * метод, который помогает упростить сборку запросов
     * метод не для внешнего использования
     * @param params Map содержащий параметры для запроса в виде пар ключ-значение
     * @param path путь до точки входа на сервере
     * @return билдер запроса
     */
    private HttpRequest.Builder getPresetRequest(String path, Map<String, String> params) {
        if (url == null)
            throw new IllegalStateException("URL of server is not set yet!");

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

    /**
     * отправляет запрос на сервер и возвращает ответ в виде строки
     * @param request сфоримированный запрос
     * @return строку ответа от сервера
     * @throws IllegalStateException когда не удается отправить запрос
     */
    private String sendRequestAndGetResponse(HttpRequest request) {
        try {
            var resp = client.send(request, HttpResponse.BodyHandlers.ofString());
            return resp.body();
        }
        catch (InterruptedException | IOException exc) {
            throw new IllegalStateException("Something went wrong during sending request", exc);
        }
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
    public HttpRequest GETRequest(String path, Map<String, String> params) {
        return getPresetRequest(path, params).GET().build();
    }

    /**
     * строит GET запрос без параметров
     * @param path путь до точки входа на сервер
     * @return собранный запрос
     */
    public HttpRequest GETRequest(String path) {
        return GETRequest(path, Map.of());
    }

    /**
     * строит POST запрос с параметрами
     * @param path путь до точки входа на сервер
     * @param params набор пар ключ-значение определяющих нужные параметры в запросе
     * @param body строка, которая представляет тело запроса, должна быть валидной строкой JSON
     * @return собранный запрос
     */
    public HttpRequest POSTRequest(String path, Map<String, String> params, String body) {
        return getPresetRequest(path, params)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
    }

    /**
     * строит POST запрос без параметров
     * @param path путь до точки входа на сервер
     * @param body строка, которая представляет тело запроса, должна быть валидной строкой JSON
     * @return собранный запрос
     */
    public HttpRequest POSTRequest(String path, String body) {
        return POSTRequest(path, Map.of(), body);
    }

    /**
     * строит POST запрос с параметрами
     * @param path путь до точки входа на сервер
     * @param params набор пар ключ-значение определяющих нужные параметры в запросе
     * @param bodyObj объект, который будет сериализован и послан на сервер, как JSON-строка
     * @return собранный запрос
     * @throws RuntimeException при невозможности сериализовать объект
     */
    public <T> HttpRequest POSTRequest(String path, Map<String, String> params, T bodyObj) {
        try {
            return POSTRequest(path, params, mapper.writeValueAsString(bodyObj));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * строит POST запрос без параметров
     * @param path путь до точки входа на сервер
     * @param bodyObj объект, который будет сериализован и послан на сервер, как JSON-строка
     * @return собранный запрос
     */
    public <T> HttpRequest POSTRequest(String path, T bodyObj) {
        return POSTRequest(path, Map.of(), bodyObj);
    }

    /**
     * общий метод маппинга ответа от вервера в объект из домена
     * существует, чтобы не писать один и тот же код дважды
     * @param request реквест, который нужно отправить
     * @param objClass ссылка на класс объекта, в который нужно маппить
     * @param typeRef ссылка на тип объекта, используется для коллекций других объектов
     * @return объект заданного класса полученного из ответа от сервера
     * @throws RuntimeException если не возможно десерелиазовать объект
     */
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
            System.out.println("there is exception, response body looks like that:\n" + response);
            throw new RuntimeException(e);
        }
        return mappedObj;
    }

    /**
     * @param request реквест, который нужно отправить
     * @param objClass ссылка на класс объекта, в который нужно маппить
     * @return объект заданного класса полученного из ответа от сервера
     * @throws RuntimeException если не возможно десерелиазовать объект
     */
    public <T> T mapResponse(HttpRequest request, Class<T> objClass) {
        return mapResponse(request, objClass, null);
    }

    /**
     * @param request реквест, который нужно отправить
     * @param typeRef ссылка на тип объекта, используется для коллекций других объектов
     * @return объект заданного класса полученного из ответа от сервера
     * @throws RuntimeException если не возможно десерелиазовать объект
     */
    public <T> T mapResponse(HttpRequest request, TypeReference<T> typeRef) {
        return mapResponse(request, null, typeRef);
    }

    /**
     * отправляет POST запрос, если произойдет ошибка, то выпадет иселючение
     * @param request реквест, который нужно отправить
     * @throws IllegalStateException когда не удается отправить запрос
     */
    public void sendPOST(HttpRequest request) {
        sendRequestAndGetResponse(request);
    }
}
