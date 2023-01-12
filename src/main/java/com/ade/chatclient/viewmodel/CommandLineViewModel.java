package com.ade.chatclient.viewmodel;

import com.ade.chatclient.model.ClientModel;

import java.util.List;

// middle layer between view and the model
// has both references to the view and the model
// calls the model when view has changed and otherwise changes the view when model has changed
public class CommandLineViewModel {
    private final ClientModel model;

    public CommandLineViewModel(ClientModel model) {
        this.model = model;
    }

    // method to execute when user enters the name
    public void setMyName(String name) {
        model.Authorize(name);
    }

    public String getMyName() {
        return model.getMyName();
    }

    public Long getMyId() {
        return model.getMyId();
    }

    public List<String> getMyChats() {
        return model.getMyChats();
    }
}