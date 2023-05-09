package com.ade.chatclient.viewmodel;

import com.ade.chatclient.dtos.AuthRequest;
import com.ade.chatclient.dtos.ChangePasswordRequest;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ButtonType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangingPasswordDialogModel {
    private final StringProperty errorMessageProperty = new SimpleStringProperty();
    private final BooleanProperty isCurPasswordBlank = new SimpleBooleanProperty(true);
    private final BooleanProperty isNewPasswordBlank = new SimpleBooleanProperty(true);
    private String curPassword;
    private String newPassword;
    public void onCurPasswordTextChanged(String newText) {
        if (newText == null) {
            newText = "";
        }
        isCurPasswordBlank.set(newText.isBlank());
    }
    public void onNewPasswordTextChanged(String newText) {
        if (newText == null) {
            newText = "";
        }
        isNewPasswordBlank.set(newText.isBlank());
    }

    public ChangePasswordRequest resultConverter(ButtonType buttonType) {
        if (buttonType != ButtonType.OK) {
            return null;
        }
        AuthRequest info = AuthRequest.builder().password(curPassword).build();
        return ChangePasswordRequest.builder().authRequest(info).newPassword(newPassword).build();
    }

    public ChangePasswordRequest onChangeClicked(String curP, String newP) {
        curPassword = curP;
        newPassword = newP;
        return resultConverter(ButtonType.OK);
    }
}
