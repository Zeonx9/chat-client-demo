package com.ade.chatclient.viewmodel;

import javafx.application.Platform;
import javafx.beans.property.ListProperty;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.function.Consumer;

public class ViewModelUtils {
    public static PropertyChangeListener runLaterListener(Consumer<PropertyChangeEvent> handler) {
        return (event) -> Platform.runLater(() -> handler.accept(event));
    }

    public static <T> Consumer<PropertyChangeEvent> listReplacer(ListProperty<T> property) {
        return event -> {
            property.clear();
            property.addAll((List<T>) event.getNewValue());
        };
    }
}
