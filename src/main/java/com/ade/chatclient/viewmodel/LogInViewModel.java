package com.ade.chatclient.viewmodel;

import com.ade.chatclient.application.AbstractViewModel;
import com.ade.chatclient.application.ViewHandler;
import com.ade.chatclient.dtos.AuthRequest;
import com.ade.chatclient.model.ClientModel;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;
import lombok.Setter;


import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;

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

    public void fillSavedLoginAndPassword() {
        try(Reader reader = Files.newBufferedReader(Paths.
                get("src/main/resources/com/ade/chatclient/login-password/package.json"))){
            ObjectMapper mapper = new ObjectMapper();
            JsonNode parser = mapper.readTree(reader);
            loginTextProperty.set(parser.path("login").asText());
            passwordProperty.set(parser.path("password").asText());
        }
        catch (Exception e){
            System.out.println("нет файла c сохраненными паролями");
        }
    }

    private void setSavedLoginAndPassword() {
        try(Writer writer = Files.newBufferedWriter(Paths.
                get("src/main/resources/com/ade/chatclient/login-password/package.json"))){
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(writer, AuthRequest.builder().login(loginTextProperty.get()).password(passwordProperty.get()).build());
        }
        catch (Exception e){
            System.out.println("нет файла для сохранения пароля");
        }
    }

    public void authorize() {
        boolean success = model.authorize(loginTextProperty.get(), passwordProperty.get());
        if (!success) {
            errorMessageProperty.set("Unsuccessful");
            return;
        }

        System.out.println("Авторизация успешна, переход к окну чатов");
        errorMessageProperty.set("Success!");

        setSavedLoginAndPassword();
        viewHandler.startBackGroundServices();
        viewHandler.openView(CHAT_PAGE_VIEW);

        errorMessageProperty.set("");
        passwordProperty.set("");
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
