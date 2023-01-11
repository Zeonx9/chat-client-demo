package com.ade.chatclient.model;

import com.ade.chatclient.application.RequestHandler;

// factory that creates an instance of model, actually uses lazy initiation
// and a singleton pattern
public class ModelFactory {
    private ClientModel clientModel;
    private final RequestHandler handler;

    public ModelFactory(RequestHandler handler) {
        this.handler = handler;
    }

    // creates an object only if none wos created before
    public ClientModel getModel() {
        if (clientModel == null)
            clientModel = new ClientModelManager(handler);

        return clientModel;
    }
}
