package com.ade.chatclient.viewmodel;

import com.ade.chatclient.model.ClientModel;
import com.ade.chatclient.view.CommandLineView;

// middle layer between view and the model
// has both references to the view and the model
// calls the model when view has changed and otherwise changes the view when model has changed
public class CommandLineViewModel {
    private final ClientModel model;

    private CommandLineView view;

    public CommandLineViewModel(ClientModel model) {
        this.model = model;
    }

    // saves the reference to view, when gui will be introduced
    // this method will be replaced by data binding
    // that is a part of JavaFX Framework
    public void bindToView(CommandLineView view) {
        this.view = view;
    }

    // method to execute when user enters the name
    public void namePropertyChanged() {
        model.Authorize(view.getMyName());
        view.setMyId(model.getMyId());
        view.setMyChats(model.getMyChats());
    }
}
