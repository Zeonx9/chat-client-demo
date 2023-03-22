package com.ade.chatclient;

import com.ade.chatclient.application.StartClientApp;
import javafx.application.Application;
import javafx.scene.image.Image;
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
    public void start(Stage stage) {
        stage.setTitle("InTouch");
        Image image = new Image("C:/Users/ed230/IdeaProjects/chat-client-demo/src/main/resources/com/ade/chatclient/img/icon.png");
        stage.getIcons().add(image);
        StartClientApp.start(stage);
    }
}
