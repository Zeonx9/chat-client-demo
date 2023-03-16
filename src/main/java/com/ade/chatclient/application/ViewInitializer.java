package com.ade.chatclient.application;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Дата-класс, который упаковывает объекты необходимые для инициализации View или Pane
 */
@Getter
@AllArgsConstructor
public class ViewInitializer {
    private FXMLLoader loader;
    private ViewHandler viewHandler;
    private Pane parent;

    public ViewModelProvider getViewModelProvider() {
        return viewHandler.getViewModelProvider();
    }
}
