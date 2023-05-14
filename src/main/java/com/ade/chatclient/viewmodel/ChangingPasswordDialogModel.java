package com.ade.chatclient.viewmodel;

import com.ade.chatclient.application.structure.AbstractDialogModel;
import com.ade.chatclient.dtos.AuthRequest;
import com.ade.chatclient.dtos.ChangePasswordRequest;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ButtonType;
import lombok.Getter;
import lombok.Setter;

/**
 * Класс отвечающий за окно для смены пароля, связан с ChangingPasswordDialog(view)
 */
@Getter
@Setter
public class ChangingPasswordDialogModel extends AbstractDialogModel<ChangePasswordRequest> {
    private final StringProperty errorMessageProperty = new SimpleStringProperty();
    private final BooleanProperty isCurPasswordBlank = new SimpleBooleanProperty(true);
    private final BooleanProperty isNewPasswordBlank = new SimpleBooleanProperty(true);
    private final StringProperty curPassword = new SimpleStringProperty();
    private final StringProperty newPassword = new SimpleStringProperty();

    /**
     * Метод проверяет, заполнено ли поле с текущим паролем пользователя
     * @param newText строка, введенная пользователем в TextField
     */
    public void onCurPasswordTextChanged(String newText) {
        if (newText == null) {
            newText = "";
        }
        isCurPasswordBlank.set(newText.isBlank());
    }

    /**
     * Метод проверяет, заполнено ли поле с новым паролем пользователя
     * @param newText строка, введенная пользователем в TextField
     */
    public void onNewPasswordTextChanged(String newText) {
        if (newText == null) {
            newText = "";
        }
        isNewPasswordBlank.set(newText.isBlank());
    }

    /**
     * Метод собирает результат работы диалогового окна и создает запрос на смену пароля
     * @param buttonType как было закрыто диалоговое окно: используя тип OK или CLOSE
     * @return ChangePasswordRequest - содержит данные для смены пароля
     */
    @Override
    public ChangePasswordRequest resultConverter(ButtonType buttonType) {
        if (buttonType != ButtonType.OK) {
            return null;
        }
        AuthRequest info = AuthRequest.builder().password(curPassword.getValue()).build();
        return ChangePasswordRequest.builder().authRequest(info).newPassword(newPassword.getValue()).build();
    }
}
