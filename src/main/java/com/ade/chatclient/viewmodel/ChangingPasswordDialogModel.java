package com.ade.chatclient.viewmodel;

import com.ade.chatclient.application.AbstractDialogModel;
import com.ade.chatclient.dtos.AuthRequest;
import com.ade.chatclient.dtos.ChangePasswordRequest;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ButtonType;
import lombok.Getter;

@Getter
public class ChangingPasswordDialogModel extends AbstractDialogModel<ChangePasswordRequest> {
    private final StringProperty errorMessageProperty = new SimpleStringProperty();
    private final BooleanProperty isCurPasswordBlank = new SimpleBooleanProperty(true);
    private final BooleanProperty isNewPasswordBlank = new SimpleBooleanProperty(true);
    private final StringProperty curPassword = new SimpleStringProperty();
    private final StringProperty newPassword = new SimpleStringProperty();
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

    @Override
    public ChangePasswordRequest resultConverter(ButtonType buttonType) {
        if (buttonType != ButtonType.OK) {
            return null;
        }
        AuthRequest info = AuthRequest.builder().password(curPassword.getValue()).build();
        return ChangePasswordRequest.builder().authRequest(info).newPassword(newPassword.getValue()).build();
    }

    @Override
    public ChangePasswordRequest onOkClicked() {
        return resultConverter(ButtonType.OK);
    }
}
