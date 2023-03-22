package com.ade.chatclient.application;

import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.function.Consumer;
public class ViewModelUtils {
    public static PropertyChangeListener runLaterListener(Consumer<PropertyChangeEvent> handler) {
        return (event) -> Platform.runLater(() -> handler.accept(event));
    }

    public static <T> ChangeListener<T> changeListener(Consumer<T> actionOnNewElement) {
        return (obj, oldVal, newVal) -> actionOnNewElement.accept(newVal);
    }

    public static <T> Consumer<PropertyChangeEvent> listReplacer(ListProperty<T> property) {
        return event -> {
            @SuppressWarnings("unchecked")
            List<T> newList = (List<T>) event.getNewValue();
            property.clear();
            property.addAll(newList);
        };
    }

    public static EventHandler<KeyEvent> enterKeyHandler(Runnable action) {
        return keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                action.run();
            }
        };
    }

}
