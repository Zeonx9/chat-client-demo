package com.ade.chatclient.application;

import javafx.scene.control.ButtonType;

public abstract class AbstractDialogModel<T> {
    public abstract T resultConverter(ButtonType buttonType);

    public T onOkClicked() {
        return resultConverter(ButtonType.OK);
    }
}
