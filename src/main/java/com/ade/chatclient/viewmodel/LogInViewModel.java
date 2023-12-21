package com.ade.chatclient.viewmodel;

import com.ade.chatclient.application.Settings;
import com.ade.chatclient.application.SettingsManager;
import com.ade.chatclient.application.ViewHandler;
import com.ade.chatclient.application.structure.AbstractViewModel;
import com.ade.chatclient.model.ClientModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;
import lombok.Setter;

import static com.ade.chatclient.application.Views.ADMIN_VIEW;
import static com.ade.chatclient.application.Views.CHAT_PAGE_VIEW;

/**
 * Класс, который связывает model с LogInView.
 * Регистрирует лисенер - "savePassword"
 */
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

    /**
     * сохраняет пароль и логин в login-password/package.json
     */
    private void saveLoginAndPasswordToSettings(String login, String password) {
        Settings settings = SettingsManager.getSettings();
        settings.setLogin(login);
        settings.setPassword(password);
        SettingsManager.saveSettings(settings);
    }

    /**
     * Заполняет поле логина и пароля данными из файла package.json, в котором сохранены данные последней авторизации
     */
    public void fillSavedLoginAndPassword() {
        Settings settings = SettingsManager.getSettings();
        loginTextProperty.set(settings.getLogin());
        passwordProperty.set(settings.getPassword());
    }

    /**
     * Метод собирает введенные пользователем данные и отправляет их в модель для входа в аккаунт, после чего: либо открывает соответствующее View (пользователя или админа), либо выводит сообщение об ошибке авторизации
     */
    public void authorize() {
        String login = loginTextProperty.get(), password = passwordProperty.get();
        boolean success = model.authorize(login, password);
        if (!success) {
            errorMessageProperty.set("Unsuccessful");
            return;
        }

//        System.out.println("Авторизация успешна, переход к окну чатов");
        errorMessageProperty.set("Success!");
        saveLoginAndPasswordToSettings(login, password);
        if (model.isAdmin()) {
            viewHandler.openView(ADMIN_VIEW);
        }
        else {
            viewHandler.startBackGroundServices();
            viewHandler.openView(CHAT_PAGE_VIEW);
        }

        errorMessageProperty.set("");
        passwordProperty.set("");
    }

    /**
     * блокирует кнопку, если данные в поле логина/пароля пустые или содержат пробелы
     * @param newValue измененные данные в TextField
     */
    public void onTextChanged(String newValue) {
        if (newValue == null)
            newValue = "";
        disableButtonProperty.set(newValue.isBlank() || newValue.contains(" "));
    }

    /**
     * Метод проверяет, пустая ли строка для ввода
     * @param newValue данные, введенные пользователем в TextField
     * @return значение типа Boolean
     */
    public Boolean checkChangedText(String newValue) {
        if (newValue == null) {
            return false;
        }
        return !newValue.isBlank() && !newValue.contains(" ");
    }

    /**
     * Если хотя бы одно поле для ввода пустое, то кнопка авторизации неактивна
     */
    public void onCheckFailed() {
        disableButtonProperty.set(true);
    }

    /**
     * Если поля логин и пароль заполнены, то кнопка авторизации активна
     */
    public void onCheckPassed() {
        disableButtonProperty.set(false);
    }
}
