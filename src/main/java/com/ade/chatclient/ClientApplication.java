package com.ade.chatclient;

import com.ade.chatclient.application.StartClientApp;
import com.ade.chatclient.view.LogInView;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

// entry point of the application
public class ClientApplication extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(LogInView.class.getResource("log-in-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 816, 446);
        stage.setTitle("InTouch");
        stage.setScene(scene);
        stage.show();

        Thread thread = new Thread(StartClientApp::start);
        thread.start();
    }
}
