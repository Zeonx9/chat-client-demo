package com.ade.chatclient.model;

import com.ade.chatclient.application.RequestHandler;

public class ModelFactory {
    private ClientModel clientModel;
    private final RequestHandler handler;

    public ModelFactory(RequestHandler handler) {
        this.handler = handler;
    }

    public ClientModel getModel() {
        if (clientModel == null)
            clientModel = new ClientModelManager(handler);

        return clientModel;
    }
}
