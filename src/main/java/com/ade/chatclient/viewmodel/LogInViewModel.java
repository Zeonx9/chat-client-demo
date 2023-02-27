package com.ade.chatclient.viewmodel;

import com.ade.chatclient.model.ClientModel;
import com.ade.chatclient.view.ViewHandler;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LogInViewModel {
    private final StringProperty loginTextProperty = new SimpleStringProperty();
    private final StringProperty passwordProperty = new SimpleStringProperty();
    private final StringProperty errorMessageProperty = new SimpleStringProperty();
    private final BooleanProperty disableButtonProperty = new SimpleBooleanProperty(true);

    private final ViewHandler viewHandler;
    private final ClientModel model;

    public void authorize() {
        boolean success = model.Authorize(loginTextProperty.get(), passwordProperty.get());
        if (!success) {
            errorMessageProperty.set("Unsuccessful");
            return;
        }

        System.out.println("Авторизация успешна, переход к окну чатов");
        errorMessageProperty.set("Success!");
        viewHandler.startBackGroundServices();
        viewHandler.openView(ViewHandler.Views.CHAT_PAGE_VIEW);
    }

    public void onTextChanged(String newValue) {
        if (newValue == null)
            newValue = "";
        disableButtonProperty.set(newValue.isBlank() || newValue.contains(" "));
    }
}
