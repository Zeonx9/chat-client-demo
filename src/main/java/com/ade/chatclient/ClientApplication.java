package com.ade.chatclient;

import com.ade.chatclient.application.StartClientApp;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Точка входа в приложение, этот класс наследуется от класса JavaFX Application
 */
public class ClientApplication extends Application {
    public static void main(String[] args) {
        launch();
    }

    /**
     * обязательный для переопределенмя метод из Application, запускает приложение
     */
    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("InTouch");
        StartClientApp.start(stage);
    }
}
