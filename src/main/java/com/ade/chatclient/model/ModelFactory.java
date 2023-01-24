package com.ade.chatclient.model;

import com.ade.chatclient.application.RequestHandler;

/**
 * фабрика для моделей в приложении, на данный момент в приложении всего одна модель, но со временем она может
 * усложинться и придется разбить на 2 модели или болле,
 * этот класс будет предоставлять доступ к моделям для вью-моделей
 * так же зависит от строки адреса сервера (туннеля ngrok)
 */
public class ModelFactory {
    private ClientModel clientModel;
    private final String serverUrl;

    /**
     * создает фабрику задавая адрес туннеля ngrok
     */
    public ModelFactory(String serverUrl) {
        this.serverUrl = serverUrl + "/chat_api/v1";
    }

    /**
     * пока что позволяет получить только одну модель, и реализует "линивую загрузку",
     * что значит создается только один объект
     * @return модель для приложения
     */
    public ClientModel getModel() {
        if (clientModel == null) {
            RequestHandler handler = new RequestHandler(serverUrl);
            clientModel = new ClientModelManager(handler);
        }

        return clientModel;
    }
}
