package com.ade.chatclient.viewmodel;

import com.ade.chatclient.model.ClientModel;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LogInViewModel {
    private final ClientModel model;

    private final StringProperty enteredLogin;
    private final StringProperty errorMessage;
    private final BooleanProperty buttonDisabled;

    public LogInViewModel(ClientModel model) {
        this.model = model;
        enteredLogin = new SimpleStringProperty();
        errorMessage = new SimpleStringProperty();
        buttonDisabled = new SimpleBooleanProperty(true);
    }

    public void authorize() {
        boolean success = model.Authorize(enteredLogin.get());
        if (success) {
            // here we should change the view
            // TODO make views switch
            errorMessage.set("Success!");
            System.out.println("You have been authorized, your ID is:" + model.getMyId());
        }
        else
            errorMessage.set("Something went wrong!");
    }

    public StringProperty getLoginProperty() {
        return enteredLogin;
    }

    public StringProperty getErrorMessageProperty() {
        return errorMessage;
    }

    public BooleanProperty getDisableProperty() {
        return buttonDisabled;
    }

    public void onTextChanged(Observable obj, String oldValue, String newValue) {
        buttonDisabled.set(newValue.isBlank());
    }
}
