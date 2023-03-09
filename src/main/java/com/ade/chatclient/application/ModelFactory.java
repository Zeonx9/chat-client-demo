package com.ade.chatclient.application;

import com.ade.chatclient.model.ClientModel;
import com.ade.chatclient.model.ClientModelImpl;
import lombok.NoArgsConstructor;

/**
 * фабрика для моделей в приложении, на данный момент в приложении всего одна модель, но со временем она может
 * усложинться и придется разбить на 2 модели или болле,
 * этот класс будет предоставлять доступ к моделям для вью-моделей
 * так же зависит от строки адреса сервера (туннеля ngrok)
 */
@NoArgsConstructor
public class ModelFactory {
    private ClientModel clientModel;
    private final AsyncRequestHandler handler = new AsyncRequestHandler();

    /**
     * вставляет URL сервера в RequestHandler
     * @param serverUrl - полученный url
     */
    public void injectServerUrl(String serverUrl) {
        handler.setUrl(serverUrl + "/chat_api/v1");
    }

    /**
     * пока что позволяет получить только одну модель, и реализует "линивую загрузку",
     * что значит создается только один объект
     * @return модель для приложения
     */
    public ClientModel getModel() {
        if (clientModel == null) {
            clientModel = new ClientModelImpl(handler);
        }
        return clientModel;
    }
}
