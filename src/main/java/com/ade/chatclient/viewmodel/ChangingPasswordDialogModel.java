package com.ade.chatclient.viewmodel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ButtonType;
import lombok.Getter;

@Getter
public class ChangingPasswordDialogModel {
    private final StringProperty errorMessageProperty = new SimpleStringProperty();
    private final BooleanProperty isCurPasswordBlank = new SimpleBooleanProperty(true);
    private final BooleanProperty isNewPasswordBlank = new SimpleBooleanProperty(true);
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
    public Boolean resultConverter(ButtonType buttonType) {
        return null;
    }

    public Boolean onChangeClicked() {
        return resultConverter(ButtonType.OK);
    }
}
