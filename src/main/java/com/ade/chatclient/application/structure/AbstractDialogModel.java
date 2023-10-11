package com.ade.chatclient.application.structure;

import javafx.scene.control.ButtonType;

/**
 * класс от которого наследуются все классы, управляющее диалогами,
 * @param <T> тип, который будет возвращен из диалога.
 */
public abstract class AbstractDialogModel<T> {

    /**
     * метод, конвертер, который задает, как собрать ответ при нажатии на кнопку.
     */
    public abstract T resultConverter(ButtonType buttonType);

    /**
     * метод который долже быть вызван при нажатии на кнопку OK
     */
    public final T onOkClicked() {
        return resultConverter(ButtonType.OK);
    }
}
