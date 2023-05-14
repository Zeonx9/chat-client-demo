package com.ade.chatclient.application.structure;

import javafx.scene.control.ButtonType;

/**
 * класс, который предназначе заменять вью-модел в диалгах, где она на требуется
 */
public final class EmptyDialogModel<T> extends AbstractDialogModel<T> {
    @Override
    public T resultConverter(ButtonType buttonType) {
        return null;
    }
}
