package com.ade.chatclient.viewmodel;

import com.ade.chatclient.application.AbstractViewModel;
import com.ade.chatclient.application.ViewHandler;
import com.ade.chatclient.model.ClientModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;
import lombok.Setter;

import static com.ade.chatclient.application.Views.CHAT_PAGE_VIEW;

@Getter
@Setter
public class LogInViewModel extends AbstractViewModel<ClientModel> {
    private StringProperty loginTextProperty = new SimpleStringProperty();
    private StringProperty passwordProperty = new SimpleStringProperty();
    private StringProperty errorMessageProperty = new SimpleStringProperty();
    private BooleanProperty disableButtonProperty = new SimpleBooleanProperty(true);

    public LogInViewModel(ViewHandler viewHandler, ClientModel model) {
        super(viewHandler, model);
    }

    public void authorize() {
        boolean success = model.Authorize(loginTextProperty.get(), passwordProperty.get());
        if (!success) {
            errorMessageProperty.set("Unsuccessful");
            return;
        }

        System.out.println("Авторизация успешна, переход к окну чатов");
        errorMessageProperty.set("Success!");

        viewHandler.startBackGroundServices();
        viewHandler.openView(CHAT_PAGE_VIEW);
    }

    public void onTextChanged(String newValue) {
        if (newValue == null)
            newValue = "";
        disableButtonProperty.set(newValue.isBlank() || newValue.contains(" "));
    }

    public Boolean checkChangedText(String newValue) {
        if (newValue == null) {
            return false;
        }
        return !newValue.isBlank() && !newValue.contains(" ");
    }

    public void onCheckFailed() {
        disableButtonProperty.set(true);
    }

    public void onCheckPassed() {
        disableButtonProperty.set(false);
    }
}
