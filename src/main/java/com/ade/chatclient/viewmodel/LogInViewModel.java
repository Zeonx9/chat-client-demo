package com.ade.chatclient.viewmodel;

import com.ade.chatclient.model.ClientModel;
import com.ade.chatclient.view.ViewHandler;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@Getter
@RequiredArgsConstructor
public class LogInViewModel {
    // TODO Егор сделай стиль для сообщения об ошибке или удачном завершении авторизации
    private final StringProperty loginTextProperty = new SimpleStringProperty();
    private final StringProperty errorMessageProperty = new SimpleStringProperty();
    private final BooleanProperty disableButtonProperty = new SimpleBooleanProperty(true);

    private final ViewHandler viewHandler;
    private final ClientModel model;

    public void authorize() {
        boolean success = model.Authorize(loginTextProperty.get());
        if (!success) {
            errorMessageProperty.set("Unsuccessful");
            return;
        }
        errorMessageProperty.set("Success!");
        try {
            System.out.println("Авторизация успешна, переход к окну чатов");
            viewHandler.startBackGroundServices();
            viewHandler.openView(ViewHandler.Views.CHAT_PAGE_VIEW);
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
            errorMessageProperty.set("cannot switch to another view!");
        }
    }

    public void onTextChanged(Observable obj, String oldValue, String newValue) {
        if (newValue == null)
            newValue = "";
        disableButtonProperty.set(newValue.isBlank());
    }
}
