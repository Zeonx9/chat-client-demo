package com.ade.chatclient.viewmodel;

import com.ade.chatclient.model.ClientModel;
import com.ade.chatclient.view.ViewHandler;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;

import java.io.IOException;

public class AllUsersViewModel {
    private final ClientModel model;
    private final ViewHandler viewHandler;
    private final StringProperty messageTextProperty;
    AllUsersViewModel(ViewHandler viewHandler, ClientModel model) {
        this.model = model;
        this.viewHandler = viewHandler;
        messageTextProperty = new SimpleStringProperty();

    }

    public void showChats() {
        try {
            viewHandler.openView(ViewHandler.Views.CHAT_PAGE_VIEW);
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
            messageTextProperty.set("cannot switch to another view!");
        }
    }
}
