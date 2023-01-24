package com.ade.chatclient.viewmodel;

import com.ade.chatclient.model.ClientModel;
import com.ade.chatclient.view.ViewHandler;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;

import java.io.IOException;

@Getter
public class LogInViewModel {
    private final StringProperty loginTextProperty;
    private final StringProperty errorMessageProperty;
    private final BooleanProperty disableButtonProperty;

    private final ClientModel model;
    private final ViewHandler viewHandler;

    public LogInViewModel(ViewHandler viewHandler, ClientModel model) {
        this.model = model;
        this.viewHandler = viewHandler;
        loginTextProperty = new SimpleStringProperty();
        errorMessageProperty = new SimpleStringProperty();
        disableButtonProperty = new SimpleBooleanProperty(true);
    }

    public void authorize() {
        boolean success = model.Authorize(loginTextProperty.get());
        if (success) {
            errorMessageProperty.set("Success!");
            System.out.println("You have been authorized, your data:" + model.getMyself().getId());
            try {
                viewHandler.openChatPageView();
            }
            catch (IOException e) {
                System.out.println(e.getMessage());
                errorMessageProperty.set("cannot switch to another view!");
            }
        }
        else
            errorMessageProperty.set("Something went wrong!");
    }

    public void onTextChanged(Observable obj, String oldValue, String newValue) {
        if (newValue == null)
            newValue = "";
        disableButtonProperty.set(newValue.isBlank());
    }
}
