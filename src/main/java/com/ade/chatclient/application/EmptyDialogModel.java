package com.ade.chatclient.application;

import javafx.scene.control.ButtonType;

public class EmptyDialogModel<T> extends AbstractDialogModel<T> {
    @Override
    public T resultConverter(ButtonType buttonType) {
        return null;
    }
}
