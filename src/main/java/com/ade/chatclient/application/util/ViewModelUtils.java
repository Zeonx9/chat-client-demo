package com.ade.chatclient.application.util;

import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

/**
 * Класс, который содержит часто использующиеся при работе с вью-моделью статические методы.
 */
public class ViewModelUtils {

    /**
     * Создает обертку над обработчиком событий. Обработчик будет выполняться в UI-потоке, без чего может возникнуть
     * исключение JavaFX
     * @param handler метод обработчик события.
     * @return новый слушатель
     */
    public static PropertyChangeListener runLaterListener(Consumer<PropertyChangeEvent> handler) {
        return (event) -> Platform.runLater(() -> handler.accept(event));
    }

    /**
     * Часто в обработчиках используется только один параметр - новое значение.
     * Этот метод конвертирует обработчик с одним аргументом в стандартный обработчик
     * @param actionOnNewElement обработчик нового значения
     * @return стандартный слушатель изменений.
     */
    public static <T> ChangeListener<T> changeListener(Consumer<T> actionOnNewElement) {
        return (obj, oldVal, newVal) -> actionOnNewElement.accept(newVal);
    }

    /**
     * Создает обработчик события, который получает переданный список значений и из события
     * и заменяет все значения на новые в указанном ListProperty
     * @param property списковое свойство, которое нужно будет обновлять
     * @return обработчик событий.
     */
    public static <T> Consumer<PropertyChangeEvent> listReplacer(ListProperty<T> property) {
        return event -> {
            @SuppressWarnings("unchecked")
            List<T> newList = (List<T>) event.getNewValue();
            property.clear();
            property.addAll(newList);
        };
    }

    /**
     * устанавливает переданное действие на выполнение по нажатию на Enter.
     * @param action ссылка на метод или другое действие.
     * @return обработчик нажатия на Enter.
     */
    public static EventHandler<KeyEvent> enterKeyHandler(Runnable action) {
        return keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                action.run();
            }
        };
    }

    /**
     * загружает заданную фабрику элементов для листвью из файла макета.
     * @param cellFactoryClass - класс для загрузки
     * @param fxmlFileName имя файла макета.
     */
    public static <T> T loadCellFactory(Class<T> cellFactoryClass, String fxmlFileName) {
        FXMLLoader loader = new FXMLLoader(cellFactoryClass.getResource(fxmlFileName));
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return  loader.getController();
    }

}
