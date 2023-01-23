package com.ade.chatclient;

import com.ade.chatclient.application.StartClientApp;
import javafx.application.Application;
import javafx.stage.Stage;

// entry point of the application
public class ClientApplication extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("InTouch");
        StartClientApp.start(stage);
    }
}
